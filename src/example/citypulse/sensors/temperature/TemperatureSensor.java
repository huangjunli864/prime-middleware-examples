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

package example.citypulse.sensors.temperature;

import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.comm.pastry_impl.PastryGateway;
import org.prime.core.PrimeApplication;
import org.prime.core.PrimeResource;

import example.citypulse.data.SensorInfo;
import example.citypulse.sensors.temperature.resources.TemperatureSensorResource;
import example.citypulse.sensors.utility.GPS;
import example.citypulse.sensors.utility.Thermometer;
import example.citypulse.sensors.temperature.TemperatureSensor;


public class TemperatureSensor extends PrimeApplication{

	private GPS gps;
	private Thermometer t;
    private SensorInfo myinfo;
    private CURI tempCURI;
    private AURI tempAURI;
    
    
    public TemperatureSensor(String id, int httpPort) throws Exception{
		super(id, httpPort);
		
		gps = new GPS();
		t = new Thermometer();
		
    	myinfo = new SensorInfo(gps.getGeoInfo(),  String.valueOf(t.getValue()), "temperature");
    	
	}
    
    
    
    
//    public void calculateMyGeoInfo(){
//    	
//    	CURI dest = new CURI("api.ipinfodb.com/v3/ip-city/?key=f1add0d3e2e8b2b2ca3b880ba97bf40f35776a877e7c9532c4c37b5ea6871d6e&format=xml");
//    	
//    	try {
//    		
//    		
//    		PrimeHTTPConnection conn = this.getConnection(dest);
//    		
//			GeoInfo newval = JAXB.unmarshal(conn.get().getStream(), GeoInfo.class);
//			if ((this.mygeo == null) || !newval.equals(this.mygeo)){
//				this.mygeo = newval;
//				
//				GeoInfoResponse info = new GeoInfoResponse(this.mygeo);
//				info.setName(this.applicationId.toString());
//				
//				
//				this.notify(this.applicationId, new AURI("GeoLocatedAgentGroup"), info);
//			}
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
    
 
    public void loadResource(Class<? extends PrimeResource> resource){
    	try {
    		
			tempCURI = this.registerResource(resource);
			
			Description d = this.registry.getDescription(tempCURI);
			d.getContext().set("latitude", this.myinfo.getLatitude());
			d.getContext().set("longitude", this.myinfo.getLongitude());
			tempAURI = d.getAURI();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   
    public String getName(){
    	return this.applicationId.toString();
    }
    
    public SensorInfo getSensorInfo(){
    	return this.myinfo;
    }
	
	public void checkSensorValue(long sleep){
		while(true){
			try {
				Thread.sleep(sleep);
				if (this.myinfo.getValue().compareTo(String.valueOf(t.getValue())) != 0 ){
					notify(tempCURI, tempAURI, myinfo);
				    this.myinfo.setValue(String.valueOf(t.getValue()));
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	
	
	public static void main(String[] args) throws Exception {
    	
    	//Parse input parameters
        String name = args[0];
    	int localDNSPort = Integer.parseInt(args[1]);
    	int httpPort = Integer.parseInt(args[2]);
    	long intervall = Long.parseLong(args[3]);
        
	
    	//Initialize PrimeApplication
    	TemperatureSensor sensor = new TemperatureSensor(name, httpPort);
    	
    	//Instantiate the Prime Communication layer
		PastryGateway gateway = new PastryGateway(localDNSPort);
		sensor.setGateway(gateway);

        //Initialize Semantic-aware DNS
        String[] onts = {"data/ontologies/ApplicationOntology-SMSComDevices.owl"};
        sensor.initResourceRegistry(onts, "http://www.erc-smscom.org/ex/early#");
        
        //Load the resource to expose
        sensor.loadResource(TemperatureSensorResource.class);
        
        //Start the PrimeApplication
        sensor.startPrimeApplication();
 
        //Keep checking the temperature every "interval" millis
        sensor.checkSensorValue(intervall);
        
    }
	
    
}
