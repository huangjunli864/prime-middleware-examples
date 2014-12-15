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

package example.ehealth.services.hospital;

import java.util.Random;

import org.prime.comm.pastry_impl.PastryGateway;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.extensions.goprime.GoPrimeApplication;
import org.prime.extensions.goprime.management.assemblymanagement.Metrics;
import org.prime.extensions.goprime.management.servicemanagement.LocalUtilityMonitor;

import example.ehealth.common.ServiceInfo;
import example.ehealth.services.hospital.resources.LabServiceResource;
import example.ehealth.services.hospital.resources.NurseryServiceResource;
import example.ehealth.services.hospital.resources.OperatingRoomResource;




public class Hospital extends GoPrimeApplication {

	private ServiceInfo nursery, lab, or;
	private CURI nurseryURI, labURI, orURI;
	
	
	public Hospital(String id, int httpPort) throws Exception{
		super(id, httpPort);
	}
	
	public ServiceInfo getNursery(){
		return this.nursery;
	}

	public ServiceInfo getLab(){
		return this.lab;
	}

	public ServiceInfo getOperatingRoom(){
		return this.or;
	}
	
	
	
	public synchronized boolean bookNursery(CURI curi){
		if (this.nursery.getAvailability() > 0){
			this.nursery.setAvailability(this.nursery.getAvailability() - 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.nursery.getAvailability());
			return true;
		}else{
			return false;
		}
	}
	

	public synchronized boolean releaseNursery(CURI curi){
		if (this.nursery.getAvailability() < this.nursery.getMaxAvailability()){
			this.nursery.setAvailability(this.nursery.getAvailability() + 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.nursery.getAvailability());
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized boolean bookLab(CURI curi){
		if (this.lab.getAvailability() > 0){
			this.lab.setAvailability(this.lab.getAvailability() - 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.lab.getAvailability());
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized boolean releaseLab(CURI curi){
		if (this.lab.getAvailability() < this.lab.getMaxAvailability()){
			this.lab.setAvailability(this.lab.getAvailability() + 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.lab.getAvailability());
			return true;
		}else{
			return false;
		}
	}

	
	
	public synchronized boolean bookOperatingRoom(CURI curi){
		if (this.or.getAvailability() > 0){
			this.or.setAvailability(this.or.getAvailability() - 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.or.getAvailability());
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized boolean releaseOperatingRoom(CURI curi){
		if (this.or.getAvailability() < this.or.getMaxAvailability()){
			this.or.setAvailability(this.or.getAvailability() + 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.or.getAvailability());
			return true;
		}else{
			return false;
		}
	}
	
	
	public void loadResource() throws Exception{
		this.nurseryURI = this.registerResource(NurseryServiceResource.class);
		Description desc = (Description) this.registry.getDescription(this.nurseryURI);
		
		
		this.nursery = new ServiceInfo();
		this.nursery.setCityName("Milano");
		this.nursery.setCountryName("Italy");
		this.nursery.setLatitude(desc.getContext().get("latitude"));
		this.nursery.setLongitude(desc.getContext().get("longitude"));
		this.nursery.setAvailability(desc.getQoS().getAvailability());
		this.nursery.setResponseTime(desc.getQoS().getResponseTime());
		this.nursery.setMaxAvailability(desc.getQoS().getAvailability());
		
		LocalUtilityMonitor nqos = new LocalUtilityMonitor(desc);
		nqos.setUtility(Metrics.RESPONSE_TIME, this.nursery.getResponseTime());
		this.startGossipManagerQoS(nqos, desc.getDependences(),  10000);
		
		
		this.labURI = this.registerResource(LabServiceResource.class);
		desc = (Description) this.registry.getDescription(this.labURI);
		this.lab = new ServiceInfo();
		this.lab.setCityName("Milano");
		this.lab.setCountryName("Italy");
		this.lab.setLatitude(desc.getContext().get("latitude"));
		this.lab.setLongitude(desc.getContext().get("longitude"));
		this.lab.setAvailability(desc.getQoS().getAvailability());
		this.lab.setResponseTime(desc.getQoS().getResponseTime());
		this.lab.setMaxAvailability(desc.getQoS().getAvailability());
		LocalUtilityMonitor lqos = new LocalUtilityMonitor(desc);
		lqos.setUtility(Metrics.RESPONSE_TIME, this.lab.getResponseTime());
		this.startGossipManagerQoS(lqos, desc.getDependences(),  10000);
		
		
		
		this.orURI = this.registerResource(OperatingRoomResource.class);
		desc = (Description) this.registry.getDescription(this.orURI);
		
		this.or = new ServiceInfo();
		this.or.setCityName("Milano");
		this.or.setCountryName("Italy");
		this.or.setLatitude(desc.getContext().get("latitude"));
		this.or.setLongitude(desc.getContext().get("longitude"));
		this.or.setAvailability(desc.getQoS().getAvailability());
		this.or.setResponseTime(desc.getQoS().getResponseTime());
		this.or.setMaxAvailability(desc.getQoS().getAvailability());
		
		LocalUtilityMonitor oqos = new LocalUtilityMonitor(desc);
		oqos.setUtility(Metrics.RESPONSE_TIME, this.or.getResponseTime());
		this.startGossipManagerQoS(oqos, desc.getDependences(),  10000);
		
	}
	

	
	
	public void simulate(long sleep){
		Random r = new Random();
		while(true){
			try {
				Thread.sleep(sleep);
				int x = r.nextInt();
				if (x % 2 == 0)
					this.releaseNursery(this.nurseryURI);
				else this.bookNursery(this.nurseryURI);
				
				x = r.nextInt();
				if (x % 2 == 0)
					this.releaseLab(this.labURI);
				else this.bookLab(this.labURI);
				
				x = r.nextInt();
				if (x % 2 == 0)
					this.releaseOperatingRoom(this.orURI);
				else this.bookOperatingRoom(this.orURI);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static void main(String[] args) throws Exception {
        	
    	String name = args[0];
    	int myDNSPort = Integer.parseInt(args[1]);
    	int httpPort = Integer.parseInt(args[2]);
    	//long sleep = Long.parseLong(args[3]);
        
        Hospital base = new Hospital(name, httpPort);
        PastryGateway gateway = new PastryGateway(myDNSPort);
        base.setGateway(gateway);
        
        
        String[] onts = {"data/ontologies/ApplicationOntology-eHealth.owl"};
        base.initResourceRegistry(onts, "http://www.erc-smscom.org/TSE-CaseStudy#");
        base.loadResource();
        base.startPrimeApplication(); 
        //base.simulate(sleep);
    }
	
	
	
	
	
	
}
