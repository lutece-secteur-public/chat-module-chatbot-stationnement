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
 	
package fr.paris.lutece.plugins.stationnement.web;

import fr.paris.lutece.plugins.stationnement.business.NoFeesDay;
import fr.paris.lutece.plugins.stationnement.business.NoFeesDayHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage NoFeesDay features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageNoFeesDays.jsp", controllerPath = "jsp/admin/plugins/stationnement/", right = "STATIONNEMENT_NOFEESDAYS_MANAGEMENT" )
public class NoFeesDayJspBean extends AbstractManageNoFeesDaysJspBean
{
    // Templates
    private static final String TEMPLATE_MANAGE_NOFEESDAYS = "/admin/plugins/stationnement/manage_nofeesdays.html";
    private static final String TEMPLATE_CREATE_NOFEESDAY = "/admin/plugins/stationnement/create_nofeesday.html";
    private static final String TEMPLATE_MODIFY_NOFEESDAY = "/admin/plugins/stationnement/modify_nofeesday.html";

    // Parameters
    private static final String PARAMETER_ID_NOFEESDAY = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_NOFEESDAYS = "stationnement.manage_nofeesdays.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_NOFEESDAY = "stationnement.modify_nofeesday.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_NOFEESDAY = "stationnement.create_nofeesday.pageTitle";

    // Markers
    private static final String MARK_NOFEESDAY_LIST = "nofeesday_list";
    private static final String MARK_NOFEESDAY = "nofeesday";

    private static final String JSP_MANAGE_NOFEESDAYS = "jsp/admin/plugins/stationnement/ManageNoFeesDays.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_NOFEESDAY = "stationnement.message.confirmRemoveNoFeesDay";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "stationnement.model.entity.nofeesday.attribute.";

    // Views
    private static final String VIEW_MANAGE_NOFEESDAYS = "manageNoFeesDays";
    private static final String VIEW_CREATE_NOFEESDAY = "createNoFeesDay";
    private static final String VIEW_MODIFY_NOFEESDAY = "modifyNoFeesDay";

    // Actions
    private static final String ACTION_CREATE_NOFEESDAY = "createNoFeesDay";
    private static final String ACTION_MODIFY_NOFEESDAY = "modifyNoFeesDay";
    private static final String ACTION_REMOVE_NOFEESDAY = "removeNoFeesDay";
    private static final String ACTION_CONFIRM_REMOVE_NOFEESDAY = "confirmRemoveNoFeesDay";

    // Infos
    private static final String INFO_NOFEESDAY_CREATED = "stationnement.info.nofeesday.created";
    private static final String INFO_NOFEESDAY_UPDATED = "stationnement.info.nofeesday.updated";
    private static final String INFO_NOFEESDAY_REMOVED = "stationnement.info.nofeesday.removed";
    
    // Session variable to store working values
    private NoFeesDay _nofeesday;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_NOFEESDAYS, defaultView = true )
    public String getManageNoFeesDays( HttpServletRequest request )
    {
        _nofeesday = null;
        List<NoFeesDay> listNoFeesDays = NoFeesDayHome.getNoFeesDaysList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_NOFEESDAY_LIST, listNoFeesDays, JSP_MANAGE_NOFEESDAYS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_NOFEESDAYS, TEMPLATE_MANAGE_NOFEESDAYS, model );
    }

    /**
     * Returns the form to create a nofeesday
     *
     * @param request The Http request
     * @return the html code of the nofeesday form
     */
    @View( VIEW_CREATE_NOFEESDAY )
    public String getCreateNoFeesDay( HttpServletRequest request )
    {
        _nofeesday = ( _nofeesday != null ) ? _nofeesday : new NoFeesDay(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_NOFEESDAY, _nofeesday );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_NOFEESDAY, TEMPLATE_CREATE_NOFEESDAY, model );
    }

    /**
     * Process the data capture form of a new nofeesday
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_NOFEESDAY )
    public String doCreateNoFeesDay( HttpServletRequest request )
    {
        populate( _nofeesday, request );

        // Check constraints
        if ( !validateBean( _nofeesday, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_NOFEESDAY );
        }

        NoFeesDayHome.create( _nofeesday );
        addInfo( INFO_NOFEESDAY_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_NOFEESDAYS );
    }

    /**
     * Manages the removal form of a nofeesday whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_NOFEESDAY )
    public String getConfirmRemoveNoFeesDay( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_NOFEESDAY ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_NOFEESDAY ) );
        url.addParameter( PARAMETER_ID_NOFEESDAY, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_NOFEESDAY, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a nofeesday
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage nofeesdays
     */
    @Action( ACTION_REMOVE_NOFEESDAY )
    public String doRemoveNoFeesDay( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_NOFEESDAY ) );
        NoFeesDayHome.remove( nId );
        addInfo( INFO_NOFEESDAY_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_NOFEESDAYS );
    }

    /**
     * Returns the form to update info about a nofeesday
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_NOFEESDAY )
    public String getModifyNoFeesDay( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_NOFEESDAY ) );

        if ( _nofeesday == null || ( _nofeesday.getId(  ) != nId ))
        {
            _nofeesday = NoFeesDayHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_NOFEESDAY, _nofeesday );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_NOFEESDAY, TEMPLATE_MODIFY_NOFEESDAY, model );
    }

    /**
     * Process the change form of a nofeesday
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_NOFEESDAY )
    public String doModifyNoFeesDay( HttpServletRequest request )
    {
        populate( _nofeesday, request );

        // Check constraints
        if ( !validateBean( _nofeesday, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_NOFEESDAY, PARAMETER_ID_NOFEESDAY, _nofeesday.getId( ) );
        }

        NoFeesDayHome.update( _nofeesday );
        addInfo( INFO_NOFEESDAY_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_NOFEESDAYS );
    }
}