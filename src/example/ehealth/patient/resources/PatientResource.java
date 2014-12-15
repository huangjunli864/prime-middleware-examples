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

package example.ehealth.patient.resources;






import java.util.Iterator;

import javax.xml.bind.JAXB;

import org.prime.core.PrimeResource;
import org.prime.core.comm.IPrimeConnection;
import org.prime.core.comm.addressing.AURI;
import org.prime.extensions.goprime.GoPrimeApplication;
import org.prime.extensions.goprime.management.assemblymanagement.AssemblyUtilityMonitor;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import example.ehealth.common.ServiceInfoResponse;


/**
 * Resource that manages a list of items.
 * 
 */
public class PatientResource extends PrimeResource{
    
	private String printError(){
		String rep = "";
		Iterator<AURI> missed = ((GoPrimeApplication) getBase()).getAssemblyManager().getUnresolvedDependences(this.getCURI()).iterator();
		
		rep = rep + "<p>FAILED: Services not found:<ul>";
		while(missed.hasNext()){
			rep = rep.concat("<li>" + missed.next() + "</li>");
		}
		return rep + "</ul></p>";
	}
	
	
	@Get("html")
	public Representation toHTML(){
    	
    	ServiceInfoResponse val;
    	
    	
    	String rep = "<html> \n"
				   + "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				   + "<title>eHealth - Patiente</title></head>\n"
				   + "<body>\n<h1>Patient</h1>";
    	
    	GoPrimeApplication base = (GoPrimeApplication) getBase();	  
    	
    	//ERROR if Missing Services
    	if (!base.getAssemblyManager().isDependencesResolved(this.getCURI())){
			setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);	
			rep += this.printError();
			return new StringRepresentation(rep, MediaType.TEXT_HTML);
		}
	    	
    	try{
			AssemblyUtilityMonitor ri = base.getAssemblyManager().getAssemblyBinding(this.getCURI(), new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#HealthCenter"));
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

}