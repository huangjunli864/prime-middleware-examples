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

package example.ehealth.services.ambulanceservice.resources;



import java.io.IOException;
import java.util.Iterator;

import javax.xml.bind.JAXB;

import org.prime.core.PrimeResource;
import org.prime.core.comm.IPrimeConnection;
import org.prime.core.comm.addressing.AURI;
import org.prime.extensions.goprime.GoPrimeApplication;
import org.prime.extensions.goprime.management.assemblymanagement.AssemblyUtilityMonitor;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import example.ehealth.common.ServiceInfoResponse;




public class AmbulanceServiceResource extends PrimeResource{
    
//	private Representation toForm(ServiceInfoResponse data){
//		// Gathering informations into a Web form.
//		Form form = new Form();
//		if (data != null){
//			form.add("name", data.getName());
//			form.add("latitude", data.getLatitude());
//			form.add("longitude", data.getLongitude());
//					
//		}else{
//			form.add("name", "");
//			form.add("latitude", "");
//			form.add("longitude", "");
//		}
//	
//    return form.getWebRepresentation();
//	}
	
	private String printError(){
		
		GoPrimeApplication base = (GoPrimeApplication) getBase();
		
		String rep = "";
		Iterator<AURI> missed = base.getAssemblyManager().getUnresolvedDependences(this.getCURI()).iterator();
		
		rep = rep + "<p>FAILED: Services not found:<ul>";
		while(missed.hasNext()){
			rep = rep.concat("<li>" + missed.next() + "</li>");
		}
		return rep + "</ul></p>";
	}
	

	@Get("xml")
	public JaxbRepresentation<ServiceInfoResponse> toXml(){
		ServiceInfoResponse rep = null;
		
		GoPrimeApplication base = (GoPrimeApplication) getBase();
		
		if (!base.getAssemblyManager().isDependencesResolved(this.getCURI())){
			setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);	
			return new JaxbRepresentation<ServiceInfoResponse>(MediaType.TEXT_XML, new ServiceInfoResponse());
		}
		
		try{
			AssemblyUtilityMonitor ri = base.getAssemblyManager().getAssemblyBinding(this.getCURI(), new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#Ambulance"));
			log.debug("Type: " + ri.getType() +"-> Instance: " + ri.getInstance());
	
			IPrimeConnection conn = base.getConnection(this.getCURI(), ri.getInstance());
			rep = JAXB.unmarshal(conn.get(MediaType.TEXT_XML).getStream(), ServiceInfoResponse.class);

	

			//ServiceInfoResponse info = new ServiceInfoResponse(((AmbulanceService) getBase()).getAmbulance());
			//info.setName(((AmbulanceService) getBase()).getApplicationID().toString());
			return new JaxbRepresentation<ServiceInfoResponse>(MediaType.TEXT_XML, rep); 


		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}


	@Get("html")
	public Representation toHTML(){
    	
		GoPrimeApplication base = (GoPrimeApplication) getBase();
		
    	ServiceInfoResponse val;
    	
    	
    	String rep = "<html> \n"
				   + "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				   + "<title>eHealt - Ambulance Service</title></head>\n"
				   + "<body>\n<h1>Ambulance Service</h1>";
    	
				  
    	
    	//ERROR if Missing Services
    	if (!base.getAssemblyManager().isDependencesResolved(this.getCURI())){
			setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);	
			rep += this.printError();
			return new StringRepresentation(rep, MediaType.TEXT_HTML);
		}
	    	
    	try{
			AssemblyUtilityMonitor ri = base.getAssemblyManager().getAssemblyBinding(this.getCURI(), new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#Ambulance"));
			log.debug("Type: " + ri.getType() +"-> Instance: " + ri.getInstance());
	
			IPrimeConnection conn = base.getConnection(this.getCURI(), ri.getInstance());
			val = JAXB.unmarshal(conn.get().getStream(), ServiceInfoResponse.class);

			rep += "<p>Ambulance <b>"+ val.getName() +"</b> is at " + val.getLatitude() +", "+val.getLongitude() + "</p>";
			rep += "<p>Availability: " + val.getAvailability() + "</p>"; 
			rep += "<p>Response Time: " + val.getResponseTime() + "</p>";
			rep += "</body></html>";
	    	return new StringRepresentation(rep, MediaType.TEXT_HTML);
			
			
		
    	}catch (Exception e) {
			// TODO Auto-generated catch block
			
    		setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);	
			rep += this.printError();
			return new StringRepresentation(rep, MediaType.TEXT_HTML);
		}
    	
		
    	
	}

	@Post
	public JaxbRepresentation<ServiceInfoResponse> book(Representation entity) throws IOException {
		// Parse the given representation and retrieve pairs of
		// "name=value" tokens.

		GoPrimeApplication base = (GoPrimeApplication) getBase();
		
		Form form = new Form(entity);
		String who = form.getFirstValue("who");

		ServiceInfoResponse rep = null;
		
		if (who.equals("ambulance"))
			try{
				AssemblyUtilityMonitor ri = base.getAssemblyManager().getAssemblyBinding(this.getCURI(), new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#Ambulance"));
				log.debug("Type: " + ri.getType() +"-> Instance: " + ri.getInstance());

				IPrimeConnection conn = base.getConnection(this.getCURI(), ri.getInstance());
				
				rep = JAXB.unmarshal(conn.post(form.getWebRepresentation()).getStream(), ServiceInfoResponse.class);
				return new JaxbRepresentation<ServiceInfoResponse>(MediaType.TEXT_XML, rep); 
				
			}catch (Exception e) {
				setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);	
				return new JaxbRepresentation<ServiceInfoResponse>(MediaType.TEXT_XML, new ServiceInfoResponse());

			}


		return null;
	}
	
//	@Post
//	public Representation book(Representation entity) throws IOException {
//		// Parse the given representation and retrieve pairs of
//		// "name=value" tokens.
//
//		Form form = new Form(entity);
//		String op = form.getFirstValue("what");
//
//		
//		if (op.equals("book"))
//			((AmbulanceService) getBase()).bookAmbulance(this.getCURI());
//				
//		if (op.equals("release"))
//			((AmbulanceService) getBase()).releaseAmbulance(this.getCURI());
//		
//
//		return this.toXml();
//	}
	
	
	
	
	

}