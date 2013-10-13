/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sharif.ce.firerunnerbackends;


import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import org.codehaus.jettison.json.JSONException;


/**
 * REST Web Service
 *
 * @author milano
 */

@Path("andservices")
public class AndroidServices {

    @Context
    private UriInfo context;
    
    
    /**
     * Creates a new instance of AndroidServices
     */
    public AndroidServices() {
    }

    /**
     * Retrieves representation of an instance of
     * org.sharif.ce.firerunnerbackends.AndroidServices
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@PathParam("username") String username) {
        //TODO return proper representation object
        return "[" + username + "]";
    }

    @POST
    @Path("{username}/{weight}")
    @Consumes("application/json")
    @Produces("application/json")
    public String postJson(@PathParam("username") String username, @PathParam("weight") double weight,
        String content) throws JSONException {
        
        List<GpsPoint> list = GpsPoint.parseJson(content);
        
        
        try {
           GPSPointDB db = new GPSPointDB();
           
            db.save(list, username, weight);
            
            return "Saved";
        } catch (Exception e) {
            
            return e.getMessage();
        }

       

    }
}
