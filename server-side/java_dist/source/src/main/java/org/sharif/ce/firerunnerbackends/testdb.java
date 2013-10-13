/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sharif.ce.firerunnerbackends;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

/**
 *
 * @author milano
 */
public class testdb {

    public static void main(String[] str) {
       
        
        try {
            List<GpsPoint> list = new ArrayList<GpsPoint>();
            
            GpsPoint p = new GpsPoint();
            p.setAltitude(12.3);
            p.setLongitude(12.3);
            p.setLatitude(12.33);
            p.setTimeTag("2013-09-09");
            p.setWayId(1);
            list.add(p);
            GPSPointDB db = new GPSPointDB();
            db.save(list, "user1", 80.0);
            
        } catch (Exception e) {
            try {
                //em.getTransaction().rollback();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
