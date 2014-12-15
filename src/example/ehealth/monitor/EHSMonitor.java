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

package example.ehealth.monitor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.TreeSet;

import org.prime.comm.pastry_impl.PastryGateway;
import org.prime.core.PrimeApplication;
import org.prime.core.comm.NotificationHandler;
import org.prime.core.comm.addressing.AURI;


class Handler extends NotificationHandler{

	private EHSMonitor base;
	
	public Handler(String id, EHSMonitor base) {
		super(id);
		this.base = base;
	}

	@Override
	public void handleNotification(Serializable msg) {
		base.addMonitoringInfo((MonitoringInfo) msg);
	}
}

public class EHSMonitor extends PrimeApplication {

	Collection<MonitoringInfo> rawdata;
	
	public EHSMonitor(String id, int httpPort) throws Exception{
		super(id, httpPort);
		rawdata = new TreeSet<MonitoringInfo>();
	}

	public void addMonitoringInfo(MonitoringInfo info){
		rawdata.add(info);
	}
	
	public void aggregateMonitoringInfo(){
		
		try {
			FileWriter writer = new FileWriter("monitor.txt", false);
			
			//Write monitored data
			//int total = 0;
			for (MonitoringInfo m : this.rawdata){
				System.out.println(m.getTimestamp() + " -> " + m.getCURI() + "\t" + m.get("compaundAvailability"));
				writer.write(m.getCURI() + "\t" + m.get("compaundAvailability") + "\n");
				//total++;
			}
			
			writer.write("\n==============================\n");
			
			//Global Number of Success/Failure
//			int  gs = 0, gf = 0;
//			double rts = 0, rtf = 0;
//			for (MonitoringInfo m : this.rawdata){
//				if (m.get("ack").equals("1")){
//					gs++;
//				    rts += Double.parseDouble((String) m.get("rt"));
//				}
//				if (m.get("ack").equals("-1")){
//					gf++;
//			        rtf += Double.parseDouble((String) m.get("rt"));
//				}
//			}
//			System.out.println("SUCCESS RATE = " + (double) gs/total + " ("+gs+"/"+total+")");
//			writer.write("SUCCESS RATE = " + (double) gs/total + " ("+gs+"/"+total+")\n");
//			
//			System.out.println("FAILURE RATE = " + (double) gf/total + " ("+gf+"/"+total+")");
//			writer.write("FAILURE RATE = " + (double) gf/total + " ("+gf+"/"+total+")\n");
//
//			System.out.println("AVG RT SUCCESS = " + (double) rts/gs );
//			writer.write("AVG RT SUCCESS = " + (double) rts/gs + "\n");
//			
//			System.out.println("AVG RT FAILURE = " + (double) rtf/gf );
//			writer.write("AVG RT FAILURE = " + (double) rtf/gf + "\n");
			
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void startObserving(){
        observe(new AURI("Monitoring"), new Handler("MonitorHandler", this));
	}
	
	public void startSimulation(){
		this.notify(this.applicationId, new AURI("SimulationGroup"), "START");
	}

	public void stopSimulation(){
		this.notify(this.applicationId, new AURI("SimulationGroup"), "STOP");
	}
	
    public static void main(String[] args) throws Exception {
        
    	//Parse input parameters
        String name = args[0];
    	int localDNSPort = Integer.parseInt(args[1]);
    	int httpPort = Integer.parseInt(args[2]);
    	long ut = Long.parseLong(args[3]);
	
    	//Initialize PrimeApplication
    	EHSMonitor eHSMonitor = new EHSMonitor(name, httpPort);
    	
    	//Instantiate the Prime Communication layer
		PastryGateway gateway = new PastryGateway(localDNSPort);
		eHSMonitor.setGateway(gateway);

        //Start the PrimeApplication
        eHSMonitor.startPrimeApplication();
 
        eHSMonitor.startObserving();
        
        //Start the simulation
        eHSMonitor.startSimulation();
        System.out.println("START");

        //Wait
        System.out.println("RUNNING");
        Thread.sleep(ut * 1000);
        
        //Stop the simulation
        eHSMonitor.stopSimulation();
        System.out.println("STOP");

        Thread.sleep(5000);
        
        eHSMonitor.stop();
        
        eHSMonitor.aggregateMonitoringInfo();

    }
}
