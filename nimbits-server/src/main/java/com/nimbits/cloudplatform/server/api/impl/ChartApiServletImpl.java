/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.api.impl;

import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.constants.Path;
import com.nimbits.cloudplatform.client.constants.Words;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.api.ApiServlet;
import com.nimbits.cloudplatform.server.time.TimespanService;
import com.nimbits.cloudplatform.server.time.TimespanServiceFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;


import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Deprecated

@Service("chart")
public class ChartApiServletImpl extends ApiServlet implements org.springframework.web.HttpRequestHandler {
    private static final Logger log = Logger.getLogger(ChartApiServletImpl.class.getName());
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String autoscaleCode = "&chds=a";
    private static final String chartDateCode = "&chd=t:";
    private static final int INT = 512;
    private static final Pattern COMPILE = Pattern.compile(",");


    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (isPost(req)) {

            doPost(req, resp);
        }
        else {
            doGet(req, resp);
        }

    }
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        final String formatParam = req.getParameter(Parameters.format.getText());


        try {
            super.doInit(req, resp, getContentType(formatParam));
            final Timespan timespan = getTimestamp(req);


            final ExportType type = getContentType(formatParam);
            log.info(req.getQueryString());
            log.info(req.getParameter(Parameters.email.getText()));

            final boolean doScale = !Utils.isEmptyString(getParam(Parameters.autoscale)) && getParam(Parameters.autoscale).equals(Words.WORD_TRUE);

            if (user==null)  {
                log.severe("Null user in chart api");
            }
            else {
                final List<EntityName> pointList = createPointList(getParam(Parameters.points), getParam(Parameters.point));
                final int count = Utils.isEmptyString(getParam(Parameters.count)) ? 10 : Integer.valueOf(getParam(Parameters.count));

                if (type == ExportType.png) {
                    final String params = generateImageChartParams(req, timespan, count, doScale, user, pointList);
                    sendChartImage(resp, params);
                }


            }
        } catch (IOException e) {
            LogHelper.logException(ChartApiServletImpl.class, e);

        } catch (Exception e) {
            log.warning(e.getMessage());

        }
    }

    private static ExportType getContentType(final String formatParam) {

        return Utils.isEmptyString(formatParam)
                ? ExportType.png : formatParam.equals("image")
                ? ExportType.png : formatParam.equals("table")
                ? ExportType.table : ExportType.plain;
    }



    private String generateImageChartParams(final HttpServletRequest req,
                                                   final Timespan timespan,
                                                   final int valueCount,
                                                   final boolean doScale,
                                                   final User u,
                                                   final Iterable<EntityName> pointList) throws Exception {

        final StringBuilder params = new StringBuilder(INT);
        params.append(req.getQueryString());
        params.append(chartDateCode);

        if (u != null) {
            for (final EntityName pointName : pointList) {


                final List<Entity> list = EntityServiceImpl.getEntityByName(u, pointName, EntityType.point);
                if (list.isEmpty()) {
                    log.info("Couldn't find a point in the chart request.");
                } else {
                    final Entity p = list.get(0);

                    if (p.getProtectionLevel().equals(ProtectionLevel.everyone) || !u.isRestricted()) {


                        final List<Value> values = timespan != null ?

                                ValueTransaction.getDataSegment(p, timespan) :
                                ValueTransaction.getTopDataSeries(p, valueCount);


                        for (final Value v : values) {
                            params.append(v.getDoubleValue()).append(Const.DELIMITER_COMMA);

                        }
                        if (params.lastIndexOf(Const.DELIMITER_COMMA) > 0) {
                            params.deleteCharAt(params.lastIndexOf(Const.DELIMITER_COMMA));

                        }
                        params.append(Const.DELIMITER_BAR);

                    }


                }
                if (params.lastIndexOf(Const.DELIMITER_BAR) > 0) {
                    params.deleteCharAt(params.lastIndexOf(Const.DELIMITER_BAR));
                }

                if (doScale) {
                    params.append(autoscaleCode);
                }
            }
        }
        return params.toString();
    }

    private static void sendChartImage(final ServletResponse resp, final String params) throws IOException {
        final URL url = new URL(Path.PATH_GOOGLE_CHART_API);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod(Const.METHOD_POST);
        connection.setReadTimeout(Const.DEFAULT_HTTP_TIMEOUT);
        final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(params);
        writer.close();
        final InputStream is = connection.getInputStream();
        resp.setContentType(ExportType.png.getCode());
        final int length = connection.getContentLength();
        log.info(params);
        final OutputStream out = resp.getOutputStream();
        resp.setContentLength(length);
        final byte[] buffer = new byte[length];
        int i;
        while ((i = is.read(buffer)) >= 0) {
            out.write(buffer, 0, i);
        }
        out.close();
    }


    private static List<EntityName> createPointList(final String pointsListParam, final String pointParamName)  {
        final List<EntityName> pointList = new ArrayList<EntityName>(10);
        if (!Utils.isEmptyString(pointParamName)) {
            pointList.add(CommonFactory.createName(pointParamName, EntityType.point));
        } else if (!Utils.isEmptyString(pointsListParam)) {
            final String[] p1 = COMPILE.split(pointsListParam);
            final List<String> pointsParams = Arrays.asList(p1);
            for (final String pn : pointsParams) {
                pointList.add(CommonFactory.createName(pn, EntityType.point));
            }
        }
        return pointList;
    }

    private static Timespan getTimestamp(final ServletRequest req) throws Exception {
        String startDate = req.getParameter(Parameters.sd.getText());
        String endDate = req.getParameter(Parameters.ed.getText());
        //support for legacy st param
        if (startDate == null) {
            startDate = req.getParameter("st");
        }
        //support for legacy et param
        if (endDate == null) {
            endDate = req.getParameter("et");
        }

        Timespan timespan = null;
        if (startDate != null && endDate != null) {

            TimespanServiceFactory.getInstance();
            timespan = TimespanService.createTimespan(startDate, endDate);

        }
        return timespan;
    }


}
