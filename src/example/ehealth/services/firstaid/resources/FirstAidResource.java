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

package example.ehealth.services.firstaid.resources;



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
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import example.ehealth.common.ServiceInfoResponse;




/**
 * Resource that manages a list of items.
 * 
 */
public class FirstAidResource extends PrimeResource{
    
	
	
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
	

	@Get
	public Representation toHTML(){
    	
    	ServiceInfoResponse val;
    	
    	GoPrimeApplication base = (GoPrimeApplication) getBase();
    	
    	String rep = "<html> \n"
				   + "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
    			   + "<meta http-equiv=\"refresh\" content=\"60\"/>" 
				   + "<title>eHealt - First Aid</title></head>\n"
				   + "<body>\n<h1>First Aid</h1>";
    	
				  
    	
    	//ERROR if Missing Services
    	if (!base.getAssemblyManager().isDependencesResolved(this.getCURI())){
			setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);	
			rep += this.printError();
			return new StringRepresentation(rep, MediaType.TEXT_HTML);
		}
	    	
    	try{
			AssemblyUtilityMonitor ri = base.getAssemblyManager().getAssemblyBinding(this.getCURI(), new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#AmbulanceService"));
			log.debug("Type: " + ri.getType() +"-> Instance: " + ri.getInstance());
	
			IPrimeConnection conn = base.getConnection(this.getCURI(), ri.getInstance());
			val = JAXB.unmarshal(conn.get(MediaType.TEXT_XML).getStream(), ServiceInfoResponse.class);

			rep += "<p>Service Ambulance <b>"+ val.getName() + "</b>:<ul>";
			rep += "<li>Availability: " + val.getAvailability() + "</li>"; 
			rep += "<li>Response Time: " + val.getResponseTime() + "</li></ul>";
			if (val.getAvailability() > 0)
				rep += "<form name=\"input\" action=\"http://"+ this.getCURI() +"\" method=\"post\">"
						+ "<input type=\"hidden\" name=\"who\" value=\"ambulance\">"
						+ "<input type=\"hidden\" name=\"what\" value=\"book\">"
						+ "<input type=\"submit\" value=\"BOOK\"></form>";
			if (val.getAvailability() < val.getMaxAvailability())
				rep += "<form name=\"input\" action=\"http://"+ this.getCURI() +"\" method=\"post\">"
						+ "<input type=\"hidden\" name=\"who\" value=\"ambulance\">"
						+ "<input type=\"hidden\" name=\"what\" value=\"release\">"
						+ "<input type=\"submit\" value=\"RELEASE\"></form>"
						+ "</p>";
			
			
			
			ri = base.getAssemblyManager().getAssemblyBinding(this.getCURI(), new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#AnalysisLab"));
			log.debug("Type: " + ri.getType() +"-> Instance: " + ri.getInstance());
	
			conn = base.getConnection(this.getCURI(), ri.getInstance());
			val = JAXB.unmarshal(conn.get().getStream(), ServiceInfoResponse.class);

			rep += "<p>Analysis Lab <b>"+ val.getName() + "</b>:<ul>";
			rep += "<li>Availability: " + val.getAvailability() + "</li>"; 
			rep += "<li>Response Time: " + val.getResponseTime() + "</li></ul></p>";
			
			
			ri = base.getAssemblyManager().getAssemblyBinding(this.getCURI(), new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#Nursery"));
			log.debug("Type: " + ri.getType() +"-> Instance: " + ri.getInstance());
	
			conn = base.getConnection(this.getCURI(), ri.getInstance());
			val = JAXB.unmarshal(conn.get().getStream(), ServiceInfoResponse.class);

			rep += "<p>Nursery <b>"+ val.getName() + "</b>:<ul>";
			rep += "<li>Availability: " + val.getAvailability() + "</li>"; 
			rep += "<li>Response Time: " + val.getResponseTime() + "</li></ul></p>";
			
			
			ri = base.getAssemblyManager().getAssemblyBinding(this.getCURI(), new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#OperatingRoom"));
			log.debug("Type: " + ri.getType() +"-> Instance: " + ri.getInstance());
	
			conn = base.getConnection(this.getCURI(), ri.getInstance());
			val = JAXB.unmarshal(conn.get().getStream(), ServiceInfoResponse.class);

			rep += "<p>Operating Room <b>"+ val.getName() + "</b>:<ul>";
			rep += "<li>Availability: " + val.getAvailability() + "</li>"; 
			rep += "<li>Response Time: " + val.getResponseTime() + "</li></ul></p>";
			
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
	public Representation book(Representation entity) throws IOException {
		// Parse the given representation and retrieve pairs of
		// "name=value" tokens.

		GoPrimeApplication base = (GoPrimeApplication) getBase();
		
		Form form = new Form(entity);
		String who = form.getFirstValue("who");

		
		if (who.equals("ambulance"))
			try{
				AssemblyUtilityMonitor ri = base.getAssemblyManager().getAssemblyBinding(this.getCURI(), new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#AmbulanceService"));
				log.debug("Type: " + ri.getType() +"-> Instance: " + ri.getInstance());

				IPrimeConnection conn = base.getConnection(this.getCURI(), ri.getInstance());
				conn.post(form.getWebRepresentation());


			}catch (Exception e) {
				String rep = "<html> \n"
						+ "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
						+ "<title>Niguarda - FirstAid</title></head>\n"
						+ "<body>\n<h1>First Aid</h1>";

				// TODO Auto-generated catch block
				setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);	
				rep += "<strong>NO SERVICE AVAILABLE</strong>";
				rep += "</body></html>";
				return new StringRepresentation(rep, MediaType.TEXT_HTML);

			}


		return this.toHTML();
	}
		

}

