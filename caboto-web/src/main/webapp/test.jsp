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
    <script type="text/javascript" src="./js/prototype.js"></script>
    <script type="text/javascript" src="./js/annotations.js"></script>

	<style type="text/css" media="screen">
<% 
    String extStyle = request.getParameter("style");
	if ((extStyle != null) && !extStyle.contains("\"")) // simple check for naughtiness
	{
		out.print("		@import \""); out.print(extStyle); out.println("\";");
	}
%>		
		input.oid {
   			background: url(http://p-stat.livejournal.com/img/openid-inputicon.gif) no-repeat;
   			background-position: 0 50%;
   			padding-left: 18px;
 		}
	</style>
</head>
<body onload="initializeAnnotations();">


<div class="annotations">

    <fieldset class="fieldSet">
        <legend>Annotations</legend>
        <div id="annotations-results"><p>Sorry, you need a JavaScript enabled browser.</p></div>


        <p>Log in to add your own annotation...</p>
		
		<p>
			<form id="openid-form" action="j_spring_openid_security_check" method="POST">
				<label><strong>Openid :</strong></label>
				<input type="text" size="30" name="j_username" class="oid"/>
				<input type="submit" value="log in..."/>
			</form>
		
			<form id="openid-logout" action="j_spring_security_logout" method="POST">
				<input type="submit" value="log out..."/>
			</form>
		</p>

        <div id="annotation-messages"></div>
	
        <form id="annotation-comment-form"
              action="javascript:processForm('./annotation/person/'+getCookie('uid')+'/public/')"
              method="post">
            <p>
                <label><strong>Title:</strong></label><br/>
                <input id="annotation-title" type="text" name="title" size="50" disabled="disabled"/><br/>
                <label><strong>Body:</strong></label><br/>
                <textarea id="annotation-body" rows="5" cols="50"
                          name="description" disabled="disabled"></textarea><br/>
                <input type="hidden" name="type" value="SimpleComment"/>
                <input type="hidden" name="annotates" value="http://www.google.com/"/>
                <input id="annotation-submit" type="submit" name="submit" value="Submit"
                       disabled="disabled"/>
            </p>
        </form>
		
		


    </fieldset>
</div>

</html>