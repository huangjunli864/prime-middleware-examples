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

package example.ehealth.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ServiceInfoResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6529726237060539271L;

	
	private String name;
	
    private String ipAddress;
	private String countryName;
	private String regionName;
	private String cityName;
	private String latitude;
	private String longitude;
	
	private double availability;
	private double maxAvailability;
	private double responsetime;
    
    
	public ServiceInfoResponse(){
		super();
	}
	
    public ServiceInfoResponse(ServiceInfo g){
    	
    	maxAvailability = g.getMaxAvailability();
    	availability = g.getAvailability();
    	responsetime = g.getResponseTime();	
    	countryName = g.getCountryName();
    	regionName = g.getRegionName();
    	cityName = g.getCityName();
    	latitude = g.getLatitude();
    	longitude = g.getLongitude();
   		
    	
    }
   
    @XmlElement
	public double getMaxAvailability() {
		// TODO Auto-generated method stub
		return this.maxAvailability;
	}
	
	public void setMaxAvailability(double availability) {
			this.maxAvailability = availability;
	}
    
    @XmlElement
	public double getAvailability() {
		return availability;
	}

	public void setAvailability(double availability) {
		//if (availability <= this.maxAvailability)
			this.availability = availability;
	}
    
	
	
	@XmlElement
	public double getResponseTime() {
		return this.responsetime;
	}
	
	public void setResponseTime(double responsetime) {
		this.responsetime = responsetime;
	}
	
	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	@XmlElement
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	
	@XmlElement
	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}


	@XmlElement
	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	@XmlElement
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	

	
	@XmlElement
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@XmlElement
	public String getLongitude() {
		return longitude;
	}


	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	

	

}
