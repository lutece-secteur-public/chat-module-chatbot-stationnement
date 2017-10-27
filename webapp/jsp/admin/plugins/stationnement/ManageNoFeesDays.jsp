<jsp:useBean id="managenofeesdaysNoFeesDay" scope="session" class="fr.paris.lutece.plugins.stationnement.web.NoFeesDayJspBean" />
<% String strContent = managenofeesdaysNoFeesDay.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
