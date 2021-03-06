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
        </script>    
        <title>Caboto Test Example</title>
    </head>
    <body onload="initializeAnnotations();">

        <security:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
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

                <%-- show form if they are logged in --%>
                <security:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
                    Filter by date:
                    <form id="date-query-form"
                          action="javascript:processDateForm()"
                          method="get">
                        <p>
                            <label><strong>Start Date:</strong></label>
                            <input id="start-date" type="text" name="from" class="datepicker"/>
                            <label><strong>End Date:</strong></label>
                            <input id="end-date" type="text" name="to" class="datepicker"/>
                            <input id="date-query-submit" type="submit" name="submit" value="Submit"
                                   disabled="disabled"/>
                        </p>
                    </form>
                    <a href="#" onclick="clearDateForm();">clear</a>


                </security:authorize>

                <div id="annotations-results"><p>Sorry, you need a JavaScript enabled browser.</p></div>

                <p>Add your own annotation...</p>

                <div id="annotation-messages"></div>

                <%-- show form if they are logged in --%>
                <security:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
                    <form id="annotation-comment-form"
                          action="javascript:processForm('<%=request.getUserPrincipal().getName()%>')"
                          method="post">
                        <p>
                            <label><strong>Title:</strong></label><br/>
                            <input id="annotation-title" type="text" name="title" size="50"/><br/>
                            <label><strong>Description:</strong></label><br/>
                            <textarea id="annotation-description1" rows="5" cols="50"
                                      name="description"></textarea><br/>
                            <label><strong>Description (repeated field):</strong></label><br/>
                            <textarea id="annotation-description2" rows="5" cols="50"
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
                                <security:authorize ifNotGranted="ROLE_USER,ROLE_ADMIN">
                                    <p>You must be <a href="secured/">logged in</a> to add an annotation.</p>

                                    <p>The test accounts are mike/potato, damian/carrot, jasper/turnip, nikki/pea,
                                        simon/radish and admin/boss</p>

                                </security:authorize>

                                </fieldset>
                                </div>

                                </body>
                                </html>