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

package example.ehealth.services.firstaid;

import org.prime.comm.pastry_impl.PastryGateway;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.extensions.goprime.GoPrimeApplication;
import org.prime.extensions.goprime.management.assemblymanagement.Metrics;
import org.prime.extensions.goprime.management.servicemanagement.LocalUtilityMonitor;

import example.ehealth.common.ServiceInfo;
import example.ehealth.services.firstaid.resources.FirstAidResource;



public class FirstAid extends GoPrimeApplication {

	private ServiceInfo myinfo;
	
	public FirstAid(String id, int httpPort) throws Exception{
		super(id, httpPort);
	}
	
	public ServiceInfo getAvailability(){
		return this.myinfo;
	}
	
	public synchronized boolean book(){
		if (this.myinfo.getAvailability() > 0){
			this.myinfo.setAvailability(this.myinfo.getAvailability() - 1);
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized boolean release(){
		if (this.myinfo.getAvailability() < 10){
			this.myinfo.setAvailability(this.myinfo.getAvailability() + 1);
			return true;
		}else{
			return false;
		}
	}
	
	
	public void loadResource() throws Exception{
		CURI c = this.registerResource(FirstAidResource.class);
		Description desc = (Description) this.registry.getDescription(c);
		
		this.myinfo = new ServiceInfo();
		this.myinfo.setCityName("Milano");
		this.myinfo.setCountryName("Italy");
		this.myinfo.setLatitude(desc.getContext().get("latitude"));
		this.myinfo.setLongitude(desc.getContext().get("longitude"));
		this.myinfo.setAvailability(desc.getQoS().getAvailability());
		this.myinfo.setResponseTime(desc.getQoS().getResponseTime());
		
		LocalUtilityMonitor qos = new LocalUtilityMonitor(desc);
		qos.setUtility(Metrics.RESPONSE_TIME, this.myinfo.getResponseTime());
		this.startGossipManagerQoS(qos, desc.getDependences(),  10000);
		
	}
	
	
	public static void main(String[] args) throws Exception {
        	
    	String name = args[0];
    	int myDNSPort = Integer.parseInt(args[1]);
    	int httpPort = Integer.parseInt(args[2]);
        
        FirstAid base = new FirstAid(name, httpPort);
        PastryGateway gateway = new PastryGateway(myDNSPort);
        base.setGateway(gateway);
        
        String[] onts = {"data/ontologies/ApplicationOntology-eHealth.owl"};
        base.initResourceRegistry(onts, "http://www.erc-smscom.org/TSE-CaseStudy#");
        base.loadResource();
        
        base.startPrimeApplication();
                
    }
	
	
	
	
	
	
}
