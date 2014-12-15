/**
 * This file is part of the PRIME middleware.
 * See http://www.erc-smscom.org
 * 
 * Copyright (C) 2008-2013 ERC-SMSCOM Project
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307,
 * USA, or send email
 * 
 * @author Mauro Caporuscio 
 */


package example.citypulse.monitor.resources;

import org.prime.core.PrimeResource;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import example.citypulse.data.SensorInfo;
import example.citypulse.monitor.SensorMonitor;

/**
 * Resource that manages a list of items.
 * 
 */
public class ListItemResource extends PrimeResource{

	String itemID;  
	  
    SensorInfo item;  
  
    @Override  
    public void doInit() {  
    	super.doInit();
        this.itemID = (String) getRequestAttributes().get("item");  
        this.item = ((SensorMonitor) getBase()).getSensorInfo(Integer.valueOf(itemID)); // Could be a lookup to a domain object.  
    }  
		
    @Get("html")
	public Representation toHTML(){
    	
    	if (item == null){
    		String rep = "<html> \n"
 				   + "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
 				   + "<title>Sensor information</title></head>\n"
 				   + "<body>";
 	

 			rep += "<p>ERROR: Requested Resorce does not exist</p>";
 			rep += "</body></html>";
 			setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
 	    	return new StringRepresentation(rep, MediaType.TEXT_HTML);
 			
    	}
    	
    	
    	String imgsrc = "http://maps.googleapis.com/maps/api/staticmap?zoom=2&size=500x500&markers=color:red%7C" + item.getLatitude()+","+ item.getLongitude() + "&sensor=false"; 
    	
		String rep = "<html> \n"
				   + "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				   + "<meta http-equiv=\"refresh\" content=\"30\"/>"
				   + "<title>Sensor information</title></head>\n"
				   + "<body>";
	

			rep += "<p>Sensor <a target=\"_blank\" href=\"http://"+ item.getName() + "\">" +item.getName() + "</a>:<ul>";
			rep += "<li>Type: " + item.getType() + "</li>";
			rep += "<li>Value: " + item.getValue() + "</li></ul></p>";
			rep += "<img src=\"" + imgsrc +"\">"; 		
			rep += "</body></html>";
			
			setStatus(Status.SUCCESS_OK);
	    	return new StringRepresentation(rep, MediaType.TEXT_HTML);
			
	}
    //rep += "<li>Position: "+  val.getLatitude() +", "+val.getLongitude() + "</li>"; 
    
   
    
    
    
    
    
//  @Post
//  public Representation acceptItem(Representation entity) {
//      Representation result = null;
//      // Parse the given representation and retrieve pairs of
//      // "name=value" tokens.
//      Form form = new Form(entity);
//      String itemName = form.getFirstValue("name");
//      String itemDescription = form.getFirstValue("description");
//
//      // Register the new item if one is not already registered.
//      //getBase().push(new GeoInfo(itemName, itemDescription));
//      
//      // Set the response's status and entity
//      setStatus(Status.SUCCESS_CREATED);
//      Representation rep = new StringRepresentation("Item created", MediaType.TEXT_PLAIN);
//      
//      // Indicates where is located the new resource.
//      rep.setLocationRef(getRequest().getResourceRef().getIdentifier() + "/top");
//      result = rep;
//      
//
//      return result;
//  }

}
