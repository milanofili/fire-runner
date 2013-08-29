<%-- 
    Document   : list
    Created on : Aug 27, 2013, 11:05:43 AM
    Author     : milano
--%>

<%@page import="org.sharif.ce.firerunnerbackends.WayInfo"%>
<%@page import="org.sharif.ce.firerunnerbackends.GpsPoint"%>
<%@page import="java.util.List"%>
<%@page import="org.sharif.ce.firerunnerbackends.GPSPointDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%
    String username = request.getParameter("userid");

    GPSPointDB db = new GPSPointDB();
    if (!db.isUserValid(username)) {
        response.sendRedirect("login.jsp?invalid=yes");
    }

%>

<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>List of ways</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <style>
            body {
                padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
            }
        </style>

        <link href="./css/bootstrap-responsive.css" rel="stylesheet">
        <link href="./css/bootstrap.css" rel="stylesheet">
        <script src="./js/jquery.js"></script>
        <script src="./js/bootstrap.js"></script>    
    </head>



    <body>

        <div class="navbar navbar-inverse navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container">
                    <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="brand" href="#">Fire Runner</a>
                    <div class="nav-collapse collapse">
                        <ul class="nav">
                            <li class="active"><a href="#" >Home</a></li>
                            <li><a href="#myModal" data-toggle="modal">About</a></li>
                            <li><a href="#contact">Contact</a></li>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>

        <div class="container">

            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Way id</th>
                        <th>Start time</th>
                        <th>End time</th>
                        <th>Calories [Cal]</th>
                        <th>Distance [KM] </th>
                        <th>Method</th>
                    </tr>
                </thead>

                <%
                    List<WayInfo> ret = db.getGroupPoint(username);

                    for (int i = 0; i < ret.size(); i += 1) {
                        WayInfo e = ret.get(i);
                        out.print("<tr>");
                        out.print("<td>" + e.wayId + "</td>");
                        out.print("<td>" + e.startTime + "</td>");
                        out.print("<td>" + e.endTime + "</td>");
                        out.print("<td>" + String.format("%.3f", e.calory) + "</td>");
                        out.print("<td>" + String.format("%.3f", e.distance) + "</td>");
                        out.print("<td> <a href=\"map.jsp?userid=" +username + "&wayid=" + e.wayId + "\">"); 
                        out.print("<button class=\"btn btn-small btn-info\" type=\"button\">Show on map</button>");
                        out.print("</a></td>");
                        
                        out.print("</tr>");
                    }
                %>

            </table>

        </div> 

        <div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h3 id="myModalLabel">About me</h3>
            </div>
            <div class="modal-body">
                <p>Fire Runner is version 1.0 </p>
                <br />
                <p>Copy rights reserved by computer engineering department of Sharif university of technology 2012-2013</p>
            </div>
            <div class="modal-footer">
                <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>

            </div>
        </div>
    </body>
</html>