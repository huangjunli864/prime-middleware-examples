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

package example.ehealth.services.ambulance.truck.resources;



import java.io.IOException;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.prime.core.PrimeResource;

import example.ehealth.common.ServiceInfo;
import example.ehealth.common.ServiceInfoResponse;
import example.ehealth.services.ambulance.truck.TAmbulance;




/**
 * Resource that manages a list of items.
 * 
 */
public class TruckAmbulanceResource extends PrimeResource{
    

	@Get("xml")
	public JaxbRepresentation<ServiceInfoResponse> toXml(){
		JaxbRepresentation<ServiceInfoResponse> rep = null;
		try {

			ServiceInfoResponse info = new ServiceInfoResponse(((TAmbulance) getBase()).getInfo());
			info.setName(((TAmbulance) getBase()).getApplicationID().toString());
			rep = new JaxbRepresentation<ServiceInfoResponse>(MediaType.TEXT_XML, info); 


		} catch (Exception e) {
			e.printStackTrace();
		}

		return rep;
	}

	
	@Get("html")
	public Representation toHTML(){
		String rep = "<html> \n"
				   + "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				   + "<meta http-equiv=\"refresh\" content=\"30\"/>"
				   + "<title>eHealth - Tambula Service</title></head>\n"
				   + "<body>\n<h1>Tambula Service</h1>";
 	
			ServiceInfo val = ((TAmbulance) getBase()).getInfo();
	

			
			
			rep += "<p>Service Ambulance <b>"+ ((TAmbulance) getBase()).getApplicationID().toString() + "</b>:<ul>";
			rep += "<li>Position: "+  val.getLatitude() +", "+val.getLongitude() + "</li>";
			rep += "<li>Availability: " + val.getAvailability() + "</li>"; 
			rep += "<li>Response Time: " + val.getResponseTime() + "</li></ul></p>";
			rep += "</body></html>";
	    	return new StringRepresentation(rep, MediaType.TEXT_HTML);
			
	}
	
	
	
	@Post
	public JaxbRepresentation<ServiceInfoResponse> book(Representation entity) throws IOException {
		// Parse the given representation and retrieve pairs of
		// "name=value" tokens.

		Form form = new Form(entity);
		String op = form.getFirstValue("what");

		
		if (op.equals("book")){
			((TAmbulance) getBase()).book(this.getCURI());
			
		}
		if (op.equals("release"))
			((TAmbulance) getBase()).release(this.getCURI());
		

		return this.toXml();
	}

		
		
	

}