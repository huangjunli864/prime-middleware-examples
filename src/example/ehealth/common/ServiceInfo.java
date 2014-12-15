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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServiceInfo{
    
	private String countryName;
	private String regionName;
	private String cityName;
	private String zipCode;
	private String latitude;
	private String longitude;
	private double availability;
	private double maxAvailability;
	private double responsetime;
    
   
//	public ServiceInfo(double a){
//		this.availability = a;
//	}

	@XmlElement
	public double getAvailability() {
		return availability;
	}

	
	public void setAvailability(double availability) {
		//if (availability <= this.maxAvailability)
			this.availability = availability;
	}
	
	

	@XmlElement
	public double getMaxAvailability() {
		return this.maxAvailability;
	}
	
	public void setMaxAvailability(double availability) {
			this.maxAvailability = availability;
	}
	
	@XmlElement
	public double getResponseTime() {
		// TODO Auto-generated method stub
		return this.responsetime;
	}
	
	public void setResponseTime(double responsetime) {
		this.responsetime = responsetime;
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
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
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
