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
    String wayid = request.getParameter("wayid");

    GPSPointDB db = new GPSPointDB();
    if (!db.isUserValid(username)) {
        response.sendRedirect("login.jsp?invalid=yes");
    }
    
    String filename = username+wayid + ".gpx";
    
    String fullpath = getServletContext().getRealPath("/") + "routes";
    
    fullpath += "/" + filename;
    
    db.createGPXfile(username, Integer.parseInt(wayid), fullpath);

%>

<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Way on map</title>
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
        <link href="./theme/default/style.css" rel="stylesheet">

        <script src="./js/jquery.js"></script>
        <script src="./js/bootstrap.js"></script>
        <script src="./OpenLayers.js"></script>






    </head>



    <body onload="init()">

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
                            <li class="active"><a href="<% out.print("/FireRunnerBackends/list.jsp?userid="+username); %>">Home</a></li>
                            <li><a href="#myModal" data-toggle="modal">About</a></li>
                            <li><a href="#contact">Contact</a></li>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>

        <div class="container" >
            <div id="Map" style="height: 600px;"></div>

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
        </div>

        <script src="OpenLayers.js"></script>
        <script>
        var lat = 47.35387;
        var lon = 8.43609;
        var zoom = 18;

        var fromProjection = new OpenLayers.Projection("EPSG:4326"); // Transform from WGS 1984
        var toProjection = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection
        //var position = new OpenLayers.LonLat(lon, lat).transform(fromProjection, toProjection);

        map = new OpenLayers.Map("Map");
        var mapnik = new OpenLayers.Layer.OSM();
        map.addLayer(mapnik);



        var lgpx = new OpenLayers.Layer.Vector("Way1", {
            strategies: [new OpenLayers.Strategy.Fixed()],
            protocol: new OpenLayers.Protocol.HTTP({
                url: '<% out.print("./routes/"+filename); %>',
                format: new OpenLayers.Format.GPX({extractWaypoints: true, extractRoutes: true, extractAttributes: true})
            }),
            style: {strokeColor: "green", strokeWidth: 5, strokeOpacity: 0.5},
            projection: new OpenLayers.Projection("EPSG:4326")
        });
        map.addLayer(lgpx);


        lgpx.events.register('loadend', lgpx, function(evt) {
            map.zoomToExtent(lgpx.getDataExtent())
        });

        var bounds = lgpx.getDataExtent();
        map.zoomToExtent(bounds);

        </script>
    </body>
</html>