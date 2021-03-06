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

import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.location.LocationFactory;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueDataModel;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.user.UserTransaction;
import com.nimbits.cloudplatform.server.transactions.value.ValueRpcServiceImpl;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Service("mailTask")

public class IncomingMailTask extends HttpServlet  implements org.springframework.web.HttpRequestHandler{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //  private final Map<String, Point> points = new HashMap<String, Point>();
    private static final Logger log = Logger.getLogger(IncomingMailTask.class.getName());
    private static final Pattern COMPILE = Pattern.compile(",");
    private static final Pattern PATTERN = Pattern.compile("\n");
    private static final Pattern COMPILE1 = Pattern.compile("\r");
    private static final Pattern COMPILE2 = Pattern.compile(";");
    private EntityServiceImpl entityService;
    private UserTransaction userService;
    private ValueRpcServiceImpl valueService;


    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {

        final String fromAddress = req.getParameter(Parameters.fromAddress.getText());
        final String inContent = req.getParameter(Parameters.inContent.getText());

        try {
            final EmailAddress internetAddress = CommonFactory.createEmailAddress(fromAddress);


            log.info("Incoming mail post: " + internetAddress);
            //u = UserTransactionFactory.getInstance().getNimbitsUser(internetAddress);
            List<Entity> result = EntityServiceImpl.getEntityByKey(userService.getAdmin(), internetAddress.getValue(), EntityType.user);

            final String content = COMPILE1.matcher(PATTERN.matcher(inContent).replaceAll("")).replaceAll("");
            final String Data[] = COMPILE2.split(content);
            log.info("Incoming mail post: " + inContent);

            if (result.isEmpty()) {
                log.severe("Null user for incoming mail:" + fromAddress);

            } else {
                final User u = (User) result.get(0);
                u.addAccessKey(userService.authenticatedKey(u));
                if (Data.length > 0) {
                    for (String s : Data) {
                        processLine(u, s);
                    }
                }
            }
        } catch (Exception e) {
            LogHelper.logException(this.getClass(), e);
        }


    }

    private void processLine(final User u, final CharSequence s) throws Exception {
        final String emailLine[] = COMPILE.split(s);
        final EntityName pointName = CommonFactory.createName(emailLine[0], EntityType.point);

        List<Entity> e =  EntityServiceImpl.getEntityByName(u, pointName, EntityType.point);


        if (! e.isEmpty()) {
            sendValue(u, e.get(0), emailLine);
        }
    }

    private void sendValue(final User u,
                                  final Entity point,
                                  final String k[])  {


        if (k != null && k.length > 1) {

            Double v = 0.0;
            try {
                v = Double.valueOf(k[1].trim());
            } catch (NumberFormatException e1) {
                log.info("Invalid mail message from: " + u.getEmail() + ' ' + k[0] + ',' + k[1]);
            }

            long timestamp;
            if (k.length == 3) {

                try {
                    String ts = k[2].trim();
                    timestamp = Long.parseLong(ts);
                } catch (NumberFormatException e) {
                    timestamp = new Date().getTime();
                    log.info("Invalid mail message from: " + u.getEmail() + ' ' + k[0] + ',' + k[1] + ',' + k[2]);
                }
            } else {
                timestamp = new Date().getTime();
            }
            String note = k.length == 4 ? k[3].trim() : "";
            final Value value = ValueFactory.createValueModel(LocationFactory.createLocation(), v, new Date(timestamp), note, ValueDataModel.getEmptyInstance(), AlertType.OK);
            try {
                ValueTransaction.recordValue(u, point, value);
            } catch (Exception e) {
                log.severe(e.getMessage());

            }
        }


    }

    public void setEntityService(EntityServiceImpl entityService) {
        this.entityService = entityService;
    }

    public EntityServiceImpl getEntityService() {
        return entityService;
    }

    public void setUserService(UserTransaction userService) {
        this.userService = userService;
    }

    public UserTransaction getUserService() {
        return userService;
    }

    public void setValueService(ValueRpcServiceImpl valueService) {
        this.valueService = valueService;
    }

    public ValueRpcServiceImpl getValueService() {
        return valueService;
    }
}
