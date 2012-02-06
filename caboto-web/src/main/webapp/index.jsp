<?xml version="1.0"?>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:security="http://www.springframework.org/security/tags">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="caboto-annotation" content="http://caboto.org"/>
    <style type="text/css" media="screen">@import "./style.css";</style>
    <script type="text/javascript" src="./js/prototype.js"></script>
    <script type="text/javascript" src="./js/annotations.js"></script>
        <script language="javascript" src="./js/protoplasm.js"></script>
        <script language="javascript">
            // transform() calls can be chained together
            Protoplasm.use('datepicker')
                .transform('input.datepicker', { 'locale': 'en_GB' });
        </script>    <title>Caboto Test Example</title>
</head>
<body onload="initializeAnnotations();">

<%
    Cookie uid = new Cookie("uid", null);
    Cookie admin = new Cookie("admin", null);

    if (request.getUserPrincipal() != null) {
        uid.setValue(request.getUserPrincipal().getName());
        if (request.isUserInRole("ROLE_ADMIN")) {
            admin.setValue("true");
        }
    }

    response.addCookie(uid);
    response.addCookie(admin);
%>

<div class="annotations">

    <fieldset class="fieldSet">
    
    <security:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
	    <div class="logout"><a href="./logout.jsp">Logout</a></div>
	</security:authorize>

        <legend>Annotations</legend>

        <%-- show form if they are logged in --%>
        <security:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
            <form id="date-query-form"
                  action="javascript:processDateForm()"
                  method="get">
                <p>
                    <label><strong>Start Date:</strong></label>
                    <input id="start-date" type="date" name="from" class="datepicker"/>
                    <label><strong>End Date:</strong></label>
                    <input id="end-date" type="date" name="to" class="datepicker"/>
                    <input id="date-query-submit" type="submit" name="submit" value="Submit"
                           disabled="disabled"/>
                </p>
            </form>

            <form id="search-query-form"
                  action="javascript:processSearchForm()"
                  method="get">
                <p>
                    <label><strong>Search term:</strong></label>
                    <input id="search-term" type="text" name="search" size="50"/>
                    <input id="search-query-submit" type="submit" name="submit" value="Submit"
                           disabled="disabled"/>
                </p>
            </form>
            
            <a href="#" onclick="clearForm();">clear form</a>
            
        </security:authorize>

        <%-- message if not logged in --%>
        <security:authorize ifNotGranted="ROLE_USER,ROLE_ADMIN">
            <p>You must be <a href="secured/">logged in</a> to add an annotation.</p>
        </security:authorize>

        <div id="annotation-messages"></div>

    </fieldset>

    <div id="annotations-results"><p>Sorry, you need a JavaScript enabled browser.</p></div>

</div>

</body>
</html>
