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

package example.citypulse.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import example.citypulse.data.GeoInfo;


@XmlRootElement
public class SensorInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1425241688189378803L;

	private String name;
	
    private String ipAddress;
	private String countryName;
	private String regionName;
	private String cityName;
	private String latitude;
	private String longitude;
	private String timeZone;
	private String value;
    private String type;
    
	public SensorInfo(){
		super();
	}
	
    public SensorInfo(GeoInfo g, String value, String type){
    	
    	ipAddress = g.getIpAddress();
    	countryName = g.getCountryName();
    	regionName = g.getRegionName();
    	cityName = g.getCityName();
    	latitude = g.getLatitude();
    	longitude = g.getLongitude();
        timeZone = g.getTimeZone();    	
        this.value = value;
        this.type = type;
    	
    }
   
	@XmlAttribute
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


	@XmlElement
	public String getTimeZone() {
		return timeZone;
	}


	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	@XmlElement
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlElement
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
