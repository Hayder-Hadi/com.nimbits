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

package com.nimbits.cloudplatform.server.process.task;

import com.google.gwt.core.client.GWT;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueRpcServiceImpl;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service("deleteTask")

public class DeleteRecordedValuesTask extends HttpServlet  implements org.springframework.web.HttpRequestHandler{

//    private static final Logger log = Logger.getLogger(UpdatePointStatsTask.class.getName());

    private static final long serialVersionUID = 1L;
    private ValueRpcServiceImpl valueService;

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {

        final String pointJson = req.getParameter(Parameters.json.getText());
        Point point = GsonFactory.getInstance().fromJson(pointJson, PointModel.class);


        try {

                deleteData(point);

        } catch (NumberFormatException e) {
            GWT.log(e.getMessage());
        }
    }

     //TODO - delete blobs
    private void deleteData(final Point point)  {
        ValueTransaction.deleteExpiredData(point);


    }

    public void setValueService(ValueRpcServiceImpl valueService) {
        this.valueService = valueService;
    }

    public ValueRpcServiceImpl getValueService() {
        return valueService;
    }
}
