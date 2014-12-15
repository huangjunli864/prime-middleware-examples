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

package example.citypulse.monitor;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.bind.JAXB;

import org.prime.comm.pastry_impl.PastryGateway;
import org.prime.core.PrimeApplication;
import org.prime.core.comm.IPrimeConnection;
import org.prime.core.comm.NotificationHandler;
import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.dns.LookupResult;
import org.prime.dns.QueryResult;
import org.restlet.data.MediaType;

import example.citypulse.data.SensorInfo;
import example.citypulse.monitor.resources.ListItemResource;
import example.citypulse.monitor.resources.MapAllResource;
import example.citypulse.monitor.resources.MapAreaResource;
import example.citypulse.monitor.resources.MapObservedResource;
import example.citypulse.monitor.resources.MonitorResource;


class Handler extends NotificationHandler{

	private SensorMonitor base;

	public Handler(String id, SensorMonitor base) {
		super(id);
		// TODO Auto-generated constructor stub
		this.base = base;
	}

	@Override
	public void handleNotification(Serializable msg) {
		// TODO Auto-generated method stub
		try {
			SensorInfo info = (SensorInfo) msg;
			base.updateSensorsInfo(info);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

public class SensorMonitor extends PrimeApplication {

    /** The list of agents is persisted in memory. */
    HashMap<String, SensorInfo> sensorList;
	

	
	public SensorMonitor(String id, int httpPort) throws Exception{
		super(id, httpPort);
		sensorList = new HashMap<String, SensorInfo>(); 
	}

	public void observeSensors(){
		observe(new AURI("http://www.erc-smscom.org/ontologies/2013/12/Devices#Temperature"), new Handler("basehandler", this));
		observe(new AURI("http://www.erc-smscom.org/ontologies/2013/12/Devices#Humidity"), new Handler("basehandler", this));
	}
	
	
	public HashMap<String, SensorInfo> getAllSensorsInfo(long sleep){
		
		HashMap<String, SensorInfo> sensorlist = new HashMap<String, SensorInfo>();

		log.info("looking for sensors all...");
		Collection<LookupResult> reslist = lookup(new AURI("http://www.erc-smscom.org/ontologies/2013/12/Devices#Sensor"), sleep);

		if (reslist != null){
			for(LookupResult r: reslist){
				//ClientResource resource = new ClientResource("http://"+r.getCURI());
				CURI curi = r.getCURI();
				try {

					IPrimeConnection conn = getHTTPConnection(curi);
					SensorInfo val = JAXB.unmarshal(conn.get(MediaType.TEXT_XML).getStream(), SensorInfo.class);
					synchronized(this){ 
						sensorlist.put(val.getName(), val);	
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}

		return sensorlist;
 
	}
	
	public HashMap<String, SensorInfo> getSensorsInArea(long sleep, String query){
		
		HashMap<String, SensorInfo> sensorlist = new HashMap<String, SensorInfo>();

		log.info("looking for sensors in an area...");
	
		Collection<QueryResult> reslist = this.query(query, sleep);
		
		if (reslist != null){
			for(QueryResult r: reslist){
				CURI curi = new CURI(r.getValue("curi"));
				try {
					IPrimeConnection conn = getHTTPConnection(curi);
					SensorInfo val = JAXB.unmarshal(conn.get(MediaType.TEXT_XML).getStream(), SensorInfo.class);
					synchronized(this){ 
						sensorlist.put(val.getName(), val);	
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}

		return sensorlist;
	}
	

	


	
	public void updateSensorsInfo(SensorInfo info){
		sensorList.put(info.getName(), info);
	}
	
	
    public Collection<SensorInfo> getSensorList(){
    	return  sensorList.values();
    }
    
    public SensorInfo getSensorInfo(int id){
    	if (sensorList.size() == 0)
    		return null;
    	
    	SensorInfo[] v =  (SensorInfo[]) sensorList.values().toArray(new SensorInfo[0]);
    	if (id >= v.length)
    		return null;
    	return (SensorInfo) v[id];
    }
    
    public static void main(String[] args) throws Exception {
        
    	//Parse input parameters
        String name = args[0];
    	int localDNSPort = Integer.parseInt(args[1]);
    	int httpPort = Integer.parseInt(args[2]);
    	
    	

    	
       
    	//Initialize PrimeApplication
    	SensorMonitor monitor = new SensorMonitor(name, httpPort);
    	
    	//Instantiate the Prime Communication layer
    	
    	PastryGateway gateway; 
    	
    	if (args.length > 3){
    		String ringAddress = args[3];
    	    int ringPort = Integer.parseInt(args[4]);
    	    gateway = new PastryGateway(localDNSPort, ringAddress, ringPort);		
    	}else
    		gateway = new PastryGateway(localDNSPort);
    	
    	
		monitor.setGateway(gateway);

        //Initialize Semantic-aware DNS
        String[] onts = {"data/ontologies/ApplicationOntology-SMSComDevices.owl"};
        monitor.initResourceRegistry(onts, "http://www.erc-smscom.org/ontologies/2013/12/Devices#");
        
        //Load the resource to expose
        monitor.registerResource(MonitorResource.class);
        monitor.registerResource(MapAllResource.class);
        monitor.registerResource(MapAreaResource.class);
        monitor.registerResource(MapObservedResource.class);
        monitor.registerResource(ListItemResource.class);
        
        //Start the PrimeApplication
        monitor.startPrimeApplication();
 
        monitor.observeSensors();
                       
    } 
}





//
//int totalSensors = Integer.parseInt(args[5]);
//long intervall = Long.parseLong(args[6]);
//Keep checking the temperature every "interval" millis
//sensormap.performanceTest(intervall, totalSensors);

//public void performanceTest(long sleep, int total){
//
//	long timeout = 0;
//	int count = 0;
//
//	PrintWriter writer = null;
//
//	try {
//		writer = new PrintWriter("FoundSensors.txt", "UTF-8");
//		writer.println("nodes\tLtime\tPtime");
//
//		while(count != total){
//			try {
//				long start = 0;
//				long end = 0;
//				timeout += 1000;
//				count = 0;
//				sensorList.clear();
//
//				log.info("looking for sensors...");
//				Collection<LookupResult> reslist = lookup(new AURI("http://www.erc-smscom.org/ontologies/2013/12/Devices#Sensor"), timeout);
//
//				if (reslist.size() != 0){
//					start = System.currentTimeMillis();
//
//					for(LookupResult r: reslist){
//					
//						CURI curi = r.getCURI();
//						try {
//
//							PrimeConnection conn = getHTTPConnection(curi);
//							SensorInfo val = JAXB.unmarshal(conn.get(MediaType.TEXT_XML).getStream(), SensorInfo.class);
//							synchronized(this){ 
//								this.sensorList.put(val.getName(), val);	
//							}
//							count++;
//
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}	
//					}
//					end = System.currentTimeMillis();
//				}
//				System.out.println("found " + count + " nodes out of " + total + " in " + timeout + "ms -> pulled in " + (end - start));
//				writer.println(((double) count/total) * 100 + "%\t" + timeout + "\t" + (end - start));
//				writer.flush();
//
//				Thread.sleep(sleep);
//
//
//				//this.myinfo.setValue(String.valueOf(t.getValue()));
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//		writer.close();
//	} catch (Exception e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	} 
//}
