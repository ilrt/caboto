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

<html>

<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="caboto-annotation" content="http://www.google.com/"/>
    <title>Testing annotations</title>
    <style type="text/css" media="screen">@import "./style.css";</style>
    <script type="text/javascript" src="./js/prototype.js"></script>
    <script type="text/javascript" src="./js/annotations.js"></script>
</head>
<body onload="initializeAnnotations();">


<div class="annotations">

    <fieldset class="fieldSet">
        <legend>Annotations</legend>
        <div id="annotations-results"><p>Sorry, you need a JavaScript enabled browser.</p></div>


        <p>Add your own annotation...</p>

        <div id="annotation-messages"></div>

        <%
            if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
        %>
		<!-- java.net.URLEncoder.encode(request.getUserPrincipal().getName(), "UTF-8") -->
        <form id="annotation-comment-form"
              action="javascript:processForm('./annotation/person/<%=request.getUserPrincipal().getName()%>/public/')"
              method="post">
            <p>
                <label><strong>Title:</strong></label><br/>
                <input id="annotation-title" type="text" name="title" size="50"/><br/>
                <label><strong>Body:</strong></label><br/>
                <textarea id="annotation-body" rows="5" cols="50"
                          name="description"></textarea><br/>
                <input type="hidden" name="type" value="SimpleComment"/>
                <input type="hidden" name="annotates" value="http://www.google.com/"/>
                <input id="annotation-submit" type="submit" name="submit" value="Submit"
                       disabled="disabled"/>
            </p>
        </form>

        <% } else { %>
        <p>You need to be <a href="./displayProfile.do">logged in</a> to add an annotation.
            You can <a href="./registration.do">register</a> if you do not have an account.<br/>
            <a href="./forgottenPassword.do">Forgotten</a> your password?</p>
        <% } %>

    </fieldset>
</div>

</html>