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

package example.ehealth.services.assistance.hsc;

import java.util.Random;

import org.prime.comm.pastry_impl.PastryGateway;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.extensions.goprime.GoPrimeApplication;
import org.prime.extensions.goprime.management.assemblymanagement.Metrics;
import org.prime.extensions.goprime.management.servicemanagement.LocalUtilityMonitor;

import example.ehealth.common.ServiceInfo;
import example.ehealth.services.assistance.hsc.resources.HealthServiceCenterResource;




public class HealthServiceCenter extends GoPrimeApplication {

	private ServiceInfo operator;
	private CURI operatorURI;
	
	
	public HealthServiceCenter(String id, int httpPort) throws Exception{
		super(id, httpPort);
	}
	
	public ServiceInfo getOperator(){
		return this.operator;
	}



	public synchronized boolean call(CURI curi){
		if (this.operator.getAvailability() < this.operator.getMaxAvailability()){
			this.operator.setAvailability(this.operator.getAvailability() + 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.operator.getAvailability());
			return true;
		}else{
			return false;
		}
	}
	
	
	public synchronized boolean hangup(CURI curi){
		if (this.operator.getAvailability() < this.operator.getMaxAvailability()){
			this.operator.setAvailability(this.operator.getAvailability() + 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.operator.getAvailability());
			return true;
		}else{
			return false;
		}
	}

	
	
	
	
	public void loadResource() throws Exception{
		this.operatorURI = this.registerResource(HealthServiceCenterResource.class);
		Description desc = (Description) this.registry.getDescription(this.operatorURI);
		
		this.operator = new ServiceInfo();
		this.operator.setCityName("Milano");
		this.operator.setCountryName("Italy");
		this.operator.setLatitude(desc.getContext().get("latitude"));
		this.operator.setLongitude(desc.getContext().get("longitude"));
		this.operator.setAvailability(desc.getQoS().getAvailability());
		this.operator.setResponseTime(desc.getQoS().getResponseTime());
		this.operator.setMaxAvailability(desc.getQoS().getAvailability());
		
		LocalUtilityMonitor qos = new LocalUtilityMonitor(desc);
		qos.setUtility(Metrics.RESPONSE_TIME, this.operator.getResponseTime());
		this.startGossipManagerQoS(qos, desc.getDependences(),  10000);
	}
	
	
	public void simulate(long sleep){
		Random r = new Random();
		while(true){
			try {
				Thread.sleep(sleep);
				int x = r.nextInt();
				if (x % 2 == 0)
					this.call(this.operatorURI);
				else this.hangup(this.operatorURI);
		
				
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
    	long sleep = Long.parseLong(args[3]);
        
        HealthServiceCenter base = new HealthServiceCenter(name, httpPort);
        PastryGateway gateway = new PastryGateway(myDNSPort);
        base.setGateway(gateway);
        
        String[] onts = {"data/ontologies/ApplicationOntology-eHealth.owl"};
        base.initResourceRegistry(onts, "http://www.erc-smscom.org/TSE-CaseStudy#");
        base.loadResource();
        base.startPrimeApplication();
        base.simulate(sleep);
    }
	
	
	
	
	
	
}
