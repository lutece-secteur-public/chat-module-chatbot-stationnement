/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.chatbot.modules.stationnement.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.chatbot.modules.stationnement.business.NoFeesDay;
import fr.paris.lutece.plugins.chatbot.modules.stationnement.business.NoFeesDayHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

public class NoFeesDayActionServlet extends HttpServlet
{

    private static final String PROPERTY_TOKEN = "chatbot-stationnement.recast.token";

    /**
         *
         */
    private static final long serialVersionUID = -2387659805321292879L;
    
//Example input
//{
//  "conversation": {
//    "language": "fr",
//    "skill_occurences": 3,
//    "skill": "63d3d0d8-7340-4cf3-9292-21596f4acfc9",
//    "memory": {
//      "location": {
//        "confidence": 0.98,
//        "raw": "227 rue de bercy paris",
//        "place": "ChIJjWtmOwNy5kcRTfOJS7ErxCk",
//        "type": "street_address",
//        "lng": 2.369777,
//        "lat": 48.8464993,
//        "formatted": "227 Rue de Bercy, 75012 Paris-12E-Arrondissement, France"
//      },
//      "date": {
//        "confidence": 0.99,
//        "raw": "aujourd'hui",
//        "state": "relative",
//        "chronology": "present",
//        "accuracy": "day",
//        "iso": "2017-10-27T14:29:02+00:00",
//        "formatted": "vendredi 27 octobre 2017 Ã  14h29m02s (+0000)"
//      }
//    },
//    "skill_stack": [
//      "63d3d0d8-7340-4cf3-9292-21596f4acfc9"
//    ],
//    "conversation_id": "4d07d6a0-8faa-4d87-9e43-d238683ab667"
//  },
//  "nlp": {
//    "status": 200,
//    "timestamp": "2017-10-27T14:29:12.939995+00:00",
//    "version": "2.10.1",
//    "processing_language": "fr",
//    "uuid": "89158fd7-11d5-4541-bd04-4d292779cb2d",
//    "source": "227 rue de bercy paris",
//    "intents": [
//      {
//        "confidence": 0.54,
//        "slug": "freepark"
//      }
//    ],
//    "act": "assert",
//    "type": null,
//    "sentiment": "neutral",
//    "entities": {
//      "location": [
//        {
//          "confidence": 0.98,
//          "raw": "227 rue de bercy paris",
//          "place": "ChIJjWtmOwNy5kcRTfOJS7ErxCk",
//          "type": "street_address",
//          "lng": 2.369777,
//          "lat": 48.8464993,
//          "formatted": "227 Rue de Bercy, 75012 Paris-12E-Arrondissement, France"
//        }
//      ]
//    },
//    "language": "fr"
//  }
//}
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    protected void service( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        String strBody = IOUtils.toString(request.getReader());
        AppLogService.info ("Got request:" + strBody);
        JsonNode actualObj = mapper.readTree(strBody);
        String strConversionId = actualObj.path("conversation").get("conversation_id").asText();
        String strDate = actualObj.path("conversation").path("memory").path("date").get("iso").asText();
        String strDateSql = strDate.substring(0, 10);
        HttpAccess httpAccess = new HttpAccess(  );
        Map<String, Object> responseJson = new HashMap<>();
        List<Object> messages = new ArrayList<>();
        responseJson.put("messages", messages);

        Map<String, Object> message = new HashMap<>();
        messages.add(message);

        List<NoFeesDay> listNoFeesDays =  NoFeesDayHome.getNoFeesDaysList();
        String strNoFeeDays = listNoFeesDays.stream()
            .map(it -> it.getDate().toString() )
            .collect(Collectors.joining(", "));
        boolean isEqual = listNoFeesDays.stream().anyMatch(it -> {
            return it.getDate().toString().equals(strDateSql);
        });

        message.put("type", "text");
        message.put("content", "List des jours fériés : " + strNoFeeDays);

        message = new HashMap<>();
        messages.add(message);
        message.put("type", "text");
        message.put("content", isEqual ? "Donc oui, c'est gratuit !" : "Donc non, c'est payant...");

        String strResponseJson = mapper.writeValueAsString( responseJson );

        Map<String, String> headers = new HashMap<>();
        String strToken = AppPropertiesService.getProperty( PROPERTY_TOKEN );
        headers.put( "Authorization", "Token " + strToken);


        Map<String, String> headersResponse = new HashMap<>();
        try {
            //example output
            //curl -H "Content-type: application/json" -H "Authorization: Token 10791544c2df29868656179a4ba523bb" -XPOST 'https://api.recast.ai/connect/v1/conversations/4d07d6a0-8faa-4d87-9e43-d238683ab667/messages' -d '{"messages": [{ "type": "text", "content": "SEAGULS !"}]}'
            String strResponse = httpAccess.doPostJSON(
                    "https://api.recast.ai/connect/v1/conversations/" + strConversionId + "/messages",
                    strResponseJson, headers, headersResponse);
            AppLogService.info ("Got reponse " + strResponse) ;
        } catch (HttpAccessException e) {
            AppLogService.error ("Error posting message to conversion " + strBody, e);

        }

        Map<String, Object> hookResponse = new HashMap<>();
        Map<String, Object> newMemory = mapper.convertValue(actualObj.path("conversation").get("memory"),  Map.class);
        newMemory.put("isfreeday", isEqual);
        hookResponse.put("memory", newMemory);
        hookResponse.put("memory", newMemory);
        hookResponse.put("messages", messages);

        String strFullResponse = mapper.writeValueAsString(hookResponse);
        AppLogService.info( "Full hook response : " + strFullResponse );
        response.setHeader( "Content-Type", "application/json" );
        response.getWriter().print(strFullResponse);
    }
}
