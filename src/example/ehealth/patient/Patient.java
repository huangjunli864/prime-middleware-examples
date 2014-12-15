package example.ehealth.patient;


import java.util.Random;

import org.prime.comm.pastry_impl.PastryGateway;
import org.prime.core.comm.IPrimeConnection;
import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.extensions.goprime.GoPrimeApplication;
import org.prime.extensions.goprime.management.assemblymanagement.AssemblyUtilityMonitor;
import org.prime.extensions.goprime.management.assemblymanagement.Metrics;
import org.prime.extensions.goprime.management.servicemanagement.LocalUtilityMonitor;
import org.restlet.data.Form;

import example.ehealth.common.ServiceInfo;
import example.ehealth.patient.resources.PatientResource;

public class Patient extends GoPrimeApplication {

	private boolean booked = false;
	
	private ServiceInfo myServiceInfo;
	private CURI clienturi;
	
	public Patient(String id, int httpPort) throws Exception{
		super(id, httpPort);
	}

	
	public boolean getInfo(){
		return booked;
	};
	
	public synchronized boolean book(){
		if (!booked){
			try{
				
				if (!getAssemblyManager().isDependencesResolved(this.clienturi)){
					return false;
				}
				
				AssemblyUtilityMonitor ri = getAssemblyManager().getAssemblyBinding(this.clienturi, new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#HealthCenter"));
				log.debug("Type: " + ri.getType() +"-> Instance: " + ri.getInstance());

				Form f = new Form();
				f.set("who", "ambulance");
				f.set("what", "book");
				
				IPrimeConnection conn = getConnection(this.clienturi, ri.getInstance());
				conn.post(f.getWebRepresentation());
				
				booked = true;
				
				return true;

			}catch (Exception e) {
				e.printStackTrace();
			}
	
		}
		
		return false;
		
	}
	
	public synchronized boolean release(){
		if (booked){
			if (!getAssemblyManager().isDependencesResolved(this.clienturi)){
				return false;
			}
			
			try{
				AssemblyUtilityMonitor ri = getAssemblyManager().getAssemblyBinding(this.clienturi, new AURI("http://www.erc-smscom.org/ontologies/2013/4/eHealth#FirstAid"));
				log.debug("Type: " + ri.getType() +"-> Instance: " + ri.getInstance());

				Form f = new Form();
				f.set("who", "ambulance");
				f.set("what", "release");
				
				IPrimeConnection conn = getConnection(this.clienturi, ri.getInstance());
				conn.post(f.getWebRepresentation());
				
				booked = true;
				
				return true;

			}catch (Exception e) {
				e.printStackTrace();
			}
	
		}
		
		return false;
	}
	
	
	
	public void loadResource() throws Exception{
		this.clienturi = this.registerResource(PatientResource.class);
		Description desc = (Description) this.registry.getDescription(this.clienturi);

		
		this.myServiceInfo = new ServiceInfo();
		this.myServiceInfo.setCityName("Milano");
		this.myServiceInfo.setCountryName("Italy");
		this.myServiceInfo.setLatitude(desc.getContext().get("latitude"));
		this.myServiceInfo.setLongitude(desc.getContext().get("longitude"));
		
		this.myServiceInfo.setMaxAvailability(desc.getQoS().getAvailability());
		this.myServiceInfo.setAvailability(desc.getQoS().getAvailability());
		this.myServiceInfo.setResponseTime(desc.getQoS().getResponseTime());
		
		LocalUtilityMonitor qos = new LocalUtilityMonitor(desc);
		qos.setUtility(Metrics.RESPONSE_TIME, this.myServiceInfo.getResponseTime());
		this.startGossipManagerQoS(qos, desc.getDependences(),  10000);
		
	}
	
	public void simulate(long sleep){
		Random r = new Random();
		while(true){
			try {
				Thread.sleep(sleep);
				int x = r.nextInt();
				if (x % 2 == 0)
					this.release();
				else this.book();
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
        
        Patient patient = new Patient(name, httpPort);
        
        PastryGateway gateway = new PastryGateway(myDNSPort);
        patient.setGateway(gateway);
        
        String[] onts = {"data/ontologies/ApplicationOntology-eHealth.owl"};
        patient.initResourceRegistry(onts, "http://www.erc-smscom.org/TSE-CaseStudy#");
        patient.loadResource();
        
        patient.startPrimeApplication();
        
        //patient.simulate(sleep);
                
    }
	
	
	
}
