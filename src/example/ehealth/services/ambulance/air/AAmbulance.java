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

package example.ehealth.services.ambulance.air;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Random;

import org.prime.comm.pastry_impl.PastryGateway;
import org.prime.core.comm.NotificationHandler;
import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.extensions.goprime.GoPrimeApplication;
import org.prime.extensions.goprime.management.assemblymanagement.Metrics;
import org.prime.extensions.goprime.management.servicemanagement.LocalUtilityMonitor;

import example.ehealth.common.ServiceInfo;
import example.ehealth.monitor.MonitoringInfo;
import example.ehealth.services.ambulance.air.resources.AirAmbulanceResource;



class Handler extends NotificationHandler{

	private AAmbulance base;
	

	public Handler(String id, AAmbulance base) {
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

public class AAmbulance extends GoPrimeApplication {

	private ServiceInfo myServiceInfo;
	private CURI airuri;
	private long sleep;
	private boolean simulating; 
	
	public AAmbulance(String id,  int httpPort) throws Exception{
		super(id, httpPort);
	}
	
	public ServiceInfo getInfo(){
		this.generateMyGeoInfo();
		return this.myServiceInfo;
	}
	
	public synchronized boolean book(CURI curi){
		if (this.myServiceInfo.getAvailability() > 0){
			this.myServiceInfo.setAvailability(this.myServiceInfo.getAvailability() - 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.myServiceInfo.getAvailability() / this.myServiceInfo.getMaxAvailability());
			
			
			LocalUtilityMonitor utility = this.getAssemblyManager().getGossipManager(this.airuri).getUtility();
			MonitoringInfo mi = new MonitoringInfo(new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#Ambulance"), this.getApplicationID());
			mi.put("compaundAvailability", utility.getUtility(Metrics.AVAILABILITY).toString());
			notify(this.getApplicationID(), new AURI("Monitoring"), mi);
			
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized boolean release(CURI curi){
		if (this.myServiceInfo.getAvailability() < this.myServiceInfo.getMaxAvailability()){
			this.myServiceInfo.setAvailability(this.myServiceInfo.getAvailability() + 1);
			updateLocalQoS(curi, Metrics.AVAILABILITY, this.myServiceInfo.getAvailability() / this.myServiceInfo.getMaxAvailability());
			
			LocalUtilityMonitor utility = this.getAssemblyManager().getGossipManager(this.airuri).getUtility();
			MonitoringInfo mi = new MonitoringInfo(new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#Ambulance"), this.getApplicationID());
			mi.put("compaundAvailability", utility.getUtility(Metrics.AVAILABILITY).toString());
			notify(this.getApplicationID(), new AURI("Monitoring"), mi);
			
			return true;
		}else{
			return false;
		}
	}
	
	
	private void generateMyGeoInfo(){

		double minLat = 44.00;
	    double maxLat = 46.00;      
	    double latitude = minLat + (double)(Math.random() * ((maxLat - minLat) + 1));
	    double minLon = 8.00;
	    double maxLon = 10.00;     
	    double longitude = minLon + (double)(Math.random() * ((maxLon - minLon) + 1));
	    DecimalFormat df = new DecimalFormat("#.#####");        
	   
	    //log.debug("latitude:longitude --> " + df.format(latitude) + "," + df.format(longitude));
	    
	    this.myServiceInfo.setLatitude(df.format(latitude));
	    this.myServiceInfo.setLongitude(df.format(longitude));
	    
	}
	
	
	public void setProcessingTime(long sleep) {
		this.sleep = sleep;
	}
	
	public void startObserving(){
		this.observe(new AURI("SimulationGroup"), new Handler("myhandler", this));
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
					this.release(this.airuri);
				else this.book(this.airuri);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	public void loadResource() throws Exception{
		this.airuri = this.registerResource(AirAmbulanceResource.class);
		Description desc = (Description) this.registry.getDescription(this.airuri);
		
		this.myServiceInfo = new ServiceInfo();
		this.myServiceInfo.setCityName("Milano");
		this.myServiceInfo.setCountryName("Italy");
		this.myServiceInfo.setLatitude(desc.getContext().get("latitude"));
		this.myServiceInfo.setLongitude(desc.getContext().get("longitude"));
		
		this.myServiceInfo.setMaxAvailability(desc.getQoS().getAvailability());
		this.myServiceInfo.setResponseTime(desc.getQoS().getResponseTime());
		
		
		LocalUtilityMonitor qos = new LocalUtilityMonitor(desc);
		
		qos.setUtility(Metrics.RESPONSE_TIME, this.myServiceInfo.getResponseTime());
		this.startGossipManagerQoS(qos, desc.getDependences(), 10000);
	}
	
	
	
	
	
	public static void main(String[] args) throws Exception {
        	
    	String name = args[0];
    	
    	int localDNSPort = Integer.parseInt(args[1]);
    	
    	int httpPort = Integer.parseInt(args[2]);
    	long sleep = Long.parseLong(args[3]);
        
	
        AAmbulance airone = new AAmbulance(name, httpPort);
        PastryGateway gateway = new PastryGateway(localDNSPort);
        airone.setGateway(gateway);

        
        String[] onts = {"data/ontologies/ApplicationOntology-eHealth.owl"};
        airone.initResourceRegistry(onts, "http://www.erc-smscom.org/TSE-CaseStudy#");
        airone.loadResource();
        airone.setProcessingTime(sleep);
        
        airone.startPrimeApplication();
        airone.startObserving();        
    }
}
