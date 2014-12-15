package example.citypulse.sensors.allinone;

import java.util.HashMap;

import org.prime.comm.pastry_impl.PastryGateway;
import org.prime.core.PrimeApplication;
import org.prime.core.PrimeResource;
import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;

import example.citypulse.data.SensorInfo;
import example.citypulse.sensors.allinone.resources.HumiditySensorResource;
import example.citypulse.sensors.allinone.resources.TemperatureSensorResource;
import example.citypulse.sensors.utility.GPS;
import example.citypulse.sensors.utility.Thermometer;

public class AllInOne extends PrimeApplication{

	private GPS gps;
	private Thermometer t;
    //private SensorInfo myinfo;
    
    
    private HashMap<CURI, SensorInfo> app_state;
    
    
    
    public AllInOne(String id, int httpPort) throws Exception{
		super(id, httpPort);
		
		gps = new GPS();
		t = new Thermometer();
		
		app_state = new HashMap<CURI, SensorInfo>();
		
    	
	}
    

    public void loadResource(Class<? extends PrimeResource> resource, int num, String type){
    	try {
    		for (int n=0; n<num; n++){
    			String cname = resource.getCanonicalName();
    			int i = cname.lastIndexOf(".");
    			cname = cname.substring(i+1);
    			String fname = this.repo_path + cname + ".rdf";
    			Description desc = Description.descriptionFactory(fname, Description.RDF);
    			desc.setCURI(new CURI(desc.getCURI().toString() + n));
    			
    			SensorInfo sinfo = new SensorInfo(gps.getGeoInfo(),  String.valueOf(t.getValue()), type);    			
    			desc.getContext().set("latitude", sinfo.getLatitude());
    			desc.getContext().set("longitude", sinfo.getLongitude());
    			
    			AURI tempAURI = desc.getAURI();    		
    			CURI tempCURI = this.registerResource(desc, resource);
    			
    			sinfo.setName(tempCURI.toString());
    			this.app_state.put(tempCURI, sinfo);
    			notify(tempCURI, tempAURI, sinfo);
    			
    			//Thread.sleep(2000);
    			
    			
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   
    public String getName(){
    	return this.applicationId.toString();
    }
    
    public SensorInfo getSensorInfo(CURI curi){
    	return this.app_state.get(curi);
    }
	
	public void checkSensorValue(long sleep){
		while(true){
			try {
				Thread.sleep(sleep);
//				if (this.myinfo.getValue().compareTo(String.valueOf(t.getValue())) != 0 ){
//					notify(tempCURI, tempAURI, myinfo);
//				    this.myinfo.setValue(String.valueOf(t.getValue()));
//				}
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
        int num = Integer.parseInt(args[4]);
	
    	//Initialize PrimeApplication
    	AllInOne all = new AllInOne(name, httpPort);
    	
    	//Instantiate the Prime Communication layer
		PastryGateway gateway = new PastryGateway(localDNSPort);
		all.setGateway(gateway);

        //Initialize Semantic-aware DNS
        String[] onts = {"data/ontologies/ApplicationOntology-SMSComDevices.owl"};
        all.initResourceRegistry(onts, "http://www.erc-smscom.org/ontologies/2013/12/Devices#");
        //all.setRepositoryPath("data/registry/geolocated/temperature/");
        
        //Load the resource to expose
        all.loadResource(TemperatureSensorResource.class, num / 2, "temperature");
        all.loadResource(HumiditySensorResource.class, num / 2, "humidity");
        
        
        //Start the PrimeApplication
        all.startPrimeApplication();
 
        //Keep checking the temperature every "interval" millis
        all.checkSensorValue(intervall);
        
    }
	
	
}
