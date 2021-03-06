package com.nimbits.cloudplatform.server.api;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.search.EntitySearchService;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 3/11/13
 * Time: 3:56 PM

 */
@Service("searchApi")
public class SearchApi extends ApiServlet implements org.springframework.web.HttpRequestHandler {


    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final PrintWriter out = resp.getWriter();


        try {
            doInit(req, resp, ExportType.json);
            String search = req.getParameter(Parameters.search.name());
            String t =getParam(Parameters.type);
            int tr = Integer.valueOf(t);
            EntityType type = EntityType.get(tr);
            List<Entity> result;

            if (type != null && ! Utils.isEmptyString(search)) {
                Results<ScoredDocument> results = EntitySearchService.findEntity(search, type);
                Iterator iterator = results.iterator();
                result = new ArrayList<Entity>(results.getNumberReturned());
                while (iterator.hasNext()) {
                    try {
                        ScoredDocument doc = (ScoredDocument) iterator.next();
                        String owner = doc.getOnlyField(Parameters.owner.name()).getText();
                        EntityName name = CommonFactory.createName(doc.getOnlyField(Parameters.name.name()).getText(), type);
                        ProtectionLevel level = ProtectionLevel.get(Integer.valueOf(doc.getOnlyField(Parameters.protection.name()).getText()));
                        if ((user != null && user.getEmail().getValue().equals(owner)) || level.equals(ProtectionLevel.everyone)) {
                            result.add(EntityModelFactory.createEntity(
                                    name, type

                            ));
                        }
                    }catch (IllegalArgumentException ex) {

                    }



                }
            }
            else {
                result = Collections.emptyList();
            }
            String json = GsonFactory.getInstance().toJson(result);
            out.print(json);


        } catch (Exception e) {
            resp.addHeader("error details", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        finally {
            out.close();
        }



    }
}