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

import java.util.Iterator;

import org.prime.core.PrimeResource;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;

import example.citypulse.data.SensorInfo;
import example.citypulse.monitor.SensorMonitor;

/**
 * Resource that manages a list of items.
 * 
 */
public class MapAllResource extends PrimeResource{
	
	
    @Get()
    public Response toImage(){
    	
    	String url = "http://maps.googleapis.com/maps/api/staticmap?zoom=1&size=1000x1000&markers=color:red";
    	//String url = "http://maps.googleapis.com/maps/api/staticmap?center=45.463705,9.188132&zoom=13&size=640x640&markers=color:yellow";
    	
    	for (Iterator<SensorInfo> i = ((SensorMonitor) getBase()).getAllSensorsInfo(5000).values().iterator(); i.hasNext();){
    		SensorInfo info = i.next();
    		url = url.concat("%7C" + info.getLatitude()+","+info.getLongitude());
    	}
    	url = url.concat("&sensor=false");
    	
    	//log.info("Sensors map url: "+url);
    	
		Request request = new Request(Method.GET, url);
		request.setReferrerRef("http://www.erc-smscom.org"); 
		
		Client client = new Client(Protocol.HTTP); 
        Response response = client.handle(request); 

		getResponse().setEntity(response.getEntity()); 
		
        return response;
    }

    
    
    
    
    
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
