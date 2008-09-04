<?xml version="1.0"?>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="caboto-annotation" content="http://caboto.org"/>
    <style type="text/css" media="screen">@import "./style.css";</style>
    <script type="text/javascript" src="./js/prototype.js"></script>
    <script type="text/javascript" src="./js/annotations.js"></script>
    <title>Caboto Test Example</title>
</head>
<body onload="initializeAnnotations();">

<security:authorize ifAnyGranted="USER,ADMIN">
    <h3 class="logout"><a href="./logout.jsp">Logout</a></h3>
</security:authorize>

<%
    Cookie uid = new Cookie("uid", null);
    Cookie admin = new Cookie("admin", null);

    if (request.getUserPrincipal() != null) {
        uid.setValue(request.getUserPrincipal().getName());
        if (request.isUserInRole("ADMIN")) {
            admin.setValue("true");
        }
    }

    response.addCookie(uid);
    response.addCookie(admin);
%>

<div class="annotations">

    <fieldset class="fieldSet">
        <legend>Annotations</legend>
        <div id="annotations-results"><p>Sorry, you need a JavaScript enabled browser.</p></div>


        <p>Add your own annotation...</p>

        <div id="annotation-messages"></div>

        <%-- show form if they are logged in --%>
        <security:authorize ifAnyGranted="USER,ADMIN">
            <form id="annotation-comment-form"
                  action="javascript:processForm('<%=request.getUserPrincipal().getName()%>')"
                  method="post">
                <p>
                    <label><strong>Title:</strong></label><br/>
                    <input id="annotation-title" type="text" name="title" size="50"/><br/>
                    <label><strong>Body:</strong></label><br/>
                    <textarea id="annotation-body" rows="5" cols="50"
                              name="description"></textarea><br/>
                    <input type="radio" name="privacy" value="public" checked="checked"> Public
                    <input type="radio" name="privacy" value="private"> Private
                    <input type="hidden" name="type" value="SimpleComment"/>
                    <input type="hidden" name="annotates" value="http://caboto.org"/><br />
                    <input id="annotation-submit" type="submit" name="submit" value="Submit"
                           disabled="disabled"/>
                </p>
            </form>
        </security:authorize>

        <%-- message if not logged in --%>
        <security:authorize ifNotGranted="USER,ADMIN">
            <p>You must be <a href="secured/">logged in</a> to add an annotation.</p>

            <p>The test accounts are mike/potato, damian/carrot, jasper/turnip, nikki/pea,
                simon/radish and admin/boss</p>

        </security:authorize>

    </fieldset>
</div>

</body>
</html>