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
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import example.geolocated.monitor.SensorMonitor;

/**
 * Resource that manages a list of items.
 * 
 */
public class MonitorResource extends PrimeResource{

    @Get("html")
    public Representation toHTML(){
    	
    	String rep = "<html> \n"
				+ "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				+ "<title>Map Sensors within an Area</title></head>\n"
				+ "<body>\n<p>Monitor options:</p>";

		rep += "<p><ul><li>Map all Sensors: <a href=\"/SM/monitor/find/all\">/monitor/find/all</a></li>"; 
		rep += "<li>Map Sensors within a given area: <a href=\"/SM/monitor/find/area\">monitor/find/area/</a></li>"; 
		rep += "<li>Map Observed Sensors: <a href=\"/SM/monitor/observed/map\">monitor/observed/map/</a></li>"; 
		if ( !((SensorMonitor) getBase()).getSensorList().isEmpty() )
			rep += "<li>Show the list of sensors: <a href=\"/SM/monitor/observed/list/0\">/SM/monitor/observed/list/0</a></li>"; 
		
		rep += "</ul></p>";
		

		rep += "</body></html>";
		return new StringRepresentation(rep, MediaType.TEXT_HTML);
    }


}
