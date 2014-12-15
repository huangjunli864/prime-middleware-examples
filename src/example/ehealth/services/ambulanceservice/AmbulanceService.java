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

package example.ehealth.services.ambulanceservice;

import java.io.Serializable;
import java.util.Random;

import org.prime.comm.pastry_impl.PastryGateway;
import org.prime.core.comm.NotificationHandler;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.extensions.goprime.GoPrimeApplication;
import org.prime.extensions.goprime.management.assemblymanagement.Metrics;
import org.prime.extensions.goprime.management.servicemanagement.LocalUtilityMonitor;

import example.ehealth.common.ServiceInfo;
import example.ehealth.services.ambulanceservice.resources.AmbulanceServiceResource;


class Handler extends NotificationHandler{

	private AmbulanceService base;


	public Handler(String id, AmbulanceService base) {
		super(id);
		this.base = base;
	}

	@Override
	public void handleNotification(Serializable msg) {
		try {
			String info = (String) msg;
			if (info.equals("START"))
				base.startSimulation();
			if (info.equals("STOP"))
				base.stopSimulation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}


public class AmbulanceService extends GoPrimeApplication {

	private ServiceInfo ambulance;
	private CURI asuri;
	private boolean simulating;
	private long sleep;
	
	public AmbulanceService(String id, int httpPort) throws Exception{
		super(id, httpPort);
	}
	

	public ServiceInfo getAmbulance(){
		return this.ambulance;
	}

	public synchronized boolean book(CURI curi){
		if (this.ambulance.getAvailability() > 0){
			this.ambulance.setAvailability(this.ambulance.getAvailability() - 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.ambulance.getAvailability() / this.ambulance.getMaxAvailability());
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized boolean release(CURI curi){
		if (this.ambulance.getAvailability() < this.ambulance.getMaxAvailability()){
			this.ambulance.setAvailability(this.ambulance.getAvailability() + 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.ambulance.getAvailability() / this.ambulance.getMaxAvailability());
			return true;
		}else{
			return false;
		}
	}
	

	public void loadResource() throws Exception{
		
		this.asuri = this.registerResource(AmbulanceServiceResource.class);
		Description desc = (Description) this.registry.getDescription(asuri);
		
		this.ambulance = new ServiceInfo();
		this.ambulance.setCityName("Milano");
		this.ambulance.setCountryName("Italy");
		this.ambulance.setLatitude(desc.getContext().get("latitude"));
		this.ambulance.setLongitude(desc.getContext().get("longitude"));
		
		this.ambulance.setMaxAvailability(desc.getQoS().getAvailability());
		this.ambulance.setAvailability(desc.getQoS().getAvailability() / this.ambulance.getMaxAvailability());
		this.ambulance.setResponseTime(desc.getQoS().getResponseTime());
		
		LocalUtilityMonitor qos = new LocalUtilityMonitor(desc);
		qos.setUtility(Metrics.RESPONSE_TIME, this.ambulance.getResponseTime());
		this.startGossipManagerQoS(qos, desc.getDependences(), 10000);
	}
	
	
	public void startSimulation(){
		System.out.println("SIMULATING....");
		simulating = true;
		this.simulate();
	}
	
	public void stopSimulation(){
		simulating = false;
		System.out.println("SIMULATION STOPPED....");
	}	
	
	public void simulate(){
		Random r = new Random();
		while(simulating){
			try {
				Thread.sleep(sleep);
				int x = r.nextInt();
				if (x % 2 == 0)
					this.release(this.asuri);
				else this.book(this.asuri);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
        	
    	String name = args[0];
    	int myDNSPort = Integer.parseInt(args[1]);
    	int httpPort = Integer.parseInt(args[2]);
    	
        AmbulanceService base = new AmbulanceService(name, httpPort);
        PastryGateway gateway = new PastryGateway(myDNSPort);
        base.setGateway(gateway);
       
        String[] onts = {"data/ontologies/ApplicationOntology-eHealth.owl"};
        base.initResourceRegistry(onts, "http://www.erc-smscom.org/TSE-CaseStudy#");
        
        base.loadResource();
         
        base.startPrimeApplication();

    }	
}
