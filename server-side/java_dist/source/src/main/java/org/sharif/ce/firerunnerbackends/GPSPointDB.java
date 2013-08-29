/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sharif.ce.firerunnerbackends;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sharif.ce.firerclient.util.CalloriesCalculator;
import org.sharif.ce.firerclient.util.DistanceCalculator;

/**
 *
 * @author milano
 */
public class GPSPointDB {

    static {
        try {
            open();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GPSPointDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GPSPointDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static Connection conn;

    public static void open() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");

        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/firerunnerdb", "firerunner", "12345");
    }

    public boolean isUserValid(String username) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql;
        sql = "Select * from \"ways\" where userid='%s';";

        ResultSet set = stmt.executeQuery(String.format(sql, username));
        return set.next();
    }

    public void createGPXfile(String username, int wayid, String path) throws SQLException {
//        File f = new File(path);
//        if (f.exists()) {
//            return;
//        }
        List<GpsPoint> all = getAllPointByUsername(username);

        String markersTemplate = "<wpt lat=\"%f\" lon=\"%f\"> <name>%s</name> <desc>%s</desc> </wpt>\n";
        String routesTemplate1 = "<trk> <name> Your route as blow</name> <trkseg>";
        String routesTemplate2 = " </trkseg> </trk>";
        String routeTemplate = "<trkpt lat=\"%f\" lon=\"%f\"></trkpt>\n";


        String markers = "";

        for (int i = 0; i < all.size(); i++) {
            GpsPoint e = all.get(i);

            markers += String.format(markersTemplate, e.getLatitude(), e.getLongitude(), "Marked Point", "Lat : " + e.getLatitude() + " Long : " + e.getLongitude()
                    + " Date : " + e.getTimeTag());

            routesTemplate1 += String.format(routeTemplate, e.getLatitude(), e.getLongitude());

        }

        routesTemplate1 += routesTemplate2;

        routesTemplate1 += markers;

        String allinone = "<?xml version=\"1.0\"?>\n" +
"<gpx version=\"1.0\" creator=\"Viking -- http://viking.sf.net/\"\n" +
"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
"xmlns=\"http://www.topografix.com/GPX/1/0\"\n" +
"xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">\n" +
"\n" +
"    \n" +
"    %s\n" +
"    \n" +
"</gpx>\n" +
"";

        allinone = String.format(allinone, routesTemplate1);

        File file = new File(path);

        try {
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(allinone);
            bw.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GpsPoint> getAllPointByUsername(String username) throws SQLException {
        List<GpsPoint> ret = new ArrayList<GpsPoint>();
        Statement stmt = conn.createStatement();
        String sql;
        sql = "Select * from \"ways\" where userid='%s';";

        ResultSet set = stmt.executeQuery(String.format(sql, username));

        while (set.next()) {
            GpsPoint e = new GpsPoint();
            e.setLatitude(set.getDouble("lat"));
            e.setLongitude(set.getDouble("lng"));
            e.setAltitude(set.getDouble("alt"));
            e.setWayId(set.getInt("wayid"));
            e.setTimeTag(set.getString("timetag"));
            e.weight = set.getDouble("weight");
            ret.add(e);
        }
        return ret;
    }

    public List<WayInfo> getGroupPoint(String username) throws SQLException {
        List<GpsPoint> all = getAllPointByUsername(username);

        List<WayInfo> ret = new ArrayList<WayInfo>();

        WayInfo currentWay = new WayInfo();
        GpsPoint last = null;

        for (int i = 0; i < all.size(); i++) {
            if (last == null) {
                last = all.get(i);
                currentWay.wayId = all.get(i).getWayId();
                currentWay.startTime = all.get(i).getTimeTag();
                currentWay.distance = currentWay.calory = 0.0;
            } else {
                if (last.getWayId() != all.get(i).getWayId()) {
                    currentWay.endTime = all.get(i).getTimeTag();

                    ret.add(currentWay);
                    currentWay = new WayInfo();
                    currentWay.wayId = all.get(i).getWayId();
                    currentWay.startTime = all.get(i).getTimeTag();
                    currentWay.distance = currentWay.calory = 0.0;
                } else {
                    currentWay.distance += DistanceCalculator.getDistance(last.getLatitude(),
                            last.getLongitude(), last.getAltitude(), all.get(i).getLatitude(),
                            all.get(i).getLongitude(), all.get(i).getAltitude());
                    currentWay.calory += CalloriesCalculator.getSimpleCalories(currentWay.distance,
                            all.get(i).weight);
                }
                last = all.get(i);
            }
        }

        if (last != null) {
            currentWay.endTime = last.getTimeTag();
            ret.add(currentWay);
        }

        return ret;
    }

    public void save(List<GpsPoint> list, String userid, double weight)
            throws ClassNotFoundException, SQLException {

        Statement stmt = conn.createStatement();
        String sql;
        sql = "INSERT INTO \"ways\"(wayid, lat, lng, alt, timetag, userid, weight) " + " VALUES (%d, %f, %f, %f, '%s', '%s', %f);";

        for (int i = 0; i < list.size(); i++) {
            if (!pointIsRepeated(list.get(i), userid)) {
                String toExec = String.format(sql, list.get(i).getWayId(), list.get(i).getLatitude(),
                        list.get(i).getLongitude(), list.get(i).getAltitude(), list.get(i).getTimeTag(), userid, weight);


                stmt.executeUpdate(toExec);
            } else {
                System.out.println("The point is repated");
            }
        }
    }

    private boolean pointIsRepeated(GpsPoint get, String username) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql;
        sql = "Select * from \"ways\" where userid='%s' and timetag='%s';";

        ResultSet set = stmt.executeQuery(String.format(sql, username, get.getTimeTag()));
        return set.next();
    }
}
