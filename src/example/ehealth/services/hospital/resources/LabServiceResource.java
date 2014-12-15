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

package example.ehealth.services.hospital.resources;



import java.io.IOException;

import org.prime.core.PrimeResource;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import example.ehealth.common.ServiceInfoResponse;
import example.ehealth.services.hospital.Hospital;





/**
 * Resource that manages a list of items.
 * 
 */
public class LabServiceResource extends PrimeResource{
    

	@Get("xml")
	public JaxbRepresentation<ServiceInfoResponse> toXml(){
		JaxbRepresentation<ServiceInfoResponse> rep = null;
		try {

			ServiceInfoResponse info = new ServiceInfoResponse(((Hospital) getBase()).getLab());
			info.setName(((Hospital) getBase()).getApplicationID().toString());
			rep = new JaxbRepresentation<ServiceInfoResponse>(MediaType.TEXT_XML, info); 


		} catch (Exception e) {
			e.printStackTrace();
		}

		return rep;
	}


	@Post
	public Representation book(Representation entity) throws IOException {
		// Parse the given representation and retrieve pairs of
		// "name=value" tokens.

//		Form form = new Form(entity);
//		String latitude = form.getFirstValue("latitude");
//		String longitude = form.getFirstValue("longitude");


		if ( ((Hospital) getBase()).bookLab(this.getCURI()) == true){
			return this.toXml();
		}
		else{
			String rep = "<html> \n"
					   + "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
					   + "<title>Niguarda - Nursery Booking</title></head>\n"
					   + "<body>\n<h1>Niguarda</h1>";
			
				// TODO Auto-generated catch block
				setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);	
				rep += "<strong>NO NURSERY AVAILABLE</strong>";
				rep += "</body></html>";
				return new StringRepresentation(rep, MediaType.TEXT_HTML);
			
		}

	}

}