<jsp:useBean id="managenofeesdaysNoFeesDay" scope="session" class="fr.paris.lutece.plugins.chatbot.modules.stationnement.web.NoFeesDayJspBean" />
<% String strContent = managenofeesdaysNoFeesDay.processController ( request , response ); %>

<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../../AdminFooter.jsp" %>
