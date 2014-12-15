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

package example.citypulse.sensors.temperature.resources;

import org.restlet.data.MediaType;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.prime.core.PrimeResource;

import example.citypulse.data.SensorInfo;
import example.citypulse.sensors.temperature.TemperatureSensor;


/**
 * Resource that manages a list of items.
 * 
 */
public class TemperatureSensorResource extends PrimeResource{

//	GeoLocationAgentResource(String descriptionFileName){
//		super(descriptionFileName);
//	}

    @Get("xml")
    public Representation toXml(){
    	
    	//System.out.println("I'm XML");
    	
    	JaxbRepresentation<SensorInfo> rep = null;
        try {
        		        	
        	SensorInfo info = ((TemperatureSensor) getBase()).getSensorInfo();
        	info.setName(((TemperatureSensor) getBase()).getName());
        	rep = new JaxbRepresentation<SensorInfo>(MediaType.TEXT_XML, info);
        	
        
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rep;
    }

    @Get("html")
	public Representation toHTML(){
    	
    	System.out.println("I'm HTML");
    	
		String rep = "<html> \n"
				   + "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				   + "<meta http-equiv=\"refresh\" content=\"30\"/>"
				   + "<title>Sensor - temperature</title></head>\n"
				   + "<body>";
 	
			SensorInfo val = ((TemperatureSensor) getBase()).getSensorInfo();
	

			rep += "<p>Temperature Sensor <b>"+ ((TemperatureSensor) getBase()).getApplicationID().toString() + "</b>:<ul>";
			rep += "<li>Position: "+  val.getLatitude() +", "+val.getLongitude() + "</li>"; 
			rep += "<li>Value: " + val.getValue() + "</li></ul></p>";
			rep += "</body></html>";
	    	return new StringRepresentation(rep, MediaType.TEXT_HTML);
			
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
