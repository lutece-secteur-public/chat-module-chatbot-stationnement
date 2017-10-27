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
import java.util.function.Function;
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
//  "conversation": {
//    "language": "fr",
//    "skill_occurences": 1,
//    "skill": "4561356e-7d53-4390-992f-0c59990e926f",
//    "memory": {},
//    "skill_stack": [
//      "4561356e-7d53-4390-992f-0c59990e926f"
//    ],
//    "conversation_id": "recast-test-1509100728093"
//  },
//  "nlp": {
//    "status": 200,
//    "timestamp": "2017-10-27T10:55:12.307224+00:00",
//    "version": "2.10.1",
//    "processing_language": "fr",
//    "uuid": "b3788189-8794-41f1-adbc-2c5a1b2e206c",
//    "source": "bonjour",
//    "intents": [
//      {
//        "confidence": 0.99,
//        "slug": "greetings"
//      }
//    ],
//    "act": "assert",
//    "type": null,
//    "sentiment": "vpositive",
//    "entities": {},
//    "language": "fr"
//  }
//}
//
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    protected void service( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        String strBody = IOUtils.toString(request.getReader());
        AppLogService.info ("Got request:" + strBody);
        JsonNode actualObj = mapper.readTree(strBody);
        String strConversionId = actualObj.path("conversation").get("conversation_id").asText();

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
        message.put("type", "text");
        message.put("content", "List des jours fériés : " + strNoFeeDays);
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

        response.getWriter().print("OK");
    }
}
