package example.citypulse.sensors.utility;

import java.net.InetAddress;
import java.text.DecimalFormat;

import example.citypulse.data.GeoInfo;

public class GPS {

	public GeoInfo getGeoInfo() throws Exception{

		//MILAN COORDINATES
//		double minLat = 45.420;
//		double maxLat = 45.490;
//		double minLon = 9.140;
//		double maxLon = 9.240; 
		
		//WORLD
		double minLat = -80;
		double maxLat = 80;
		double minLon = -170;
		double maxLon = 170; 

		
		double latitude = minLat + (double)(Math.random() * ((maxLat - minLat) + 0.001));
		    
		double longitude = minLon + (double)(Math.random() * ((maxLon - minLon) + 0.001));
		DecimalFormat df = new DecimalFormat("#.#####");        

		//log.debug("latitude:longitude --> " + df.format(latitude) + "," + df.format(longitude));

		GeoInfo info = new GeoInfo();
		info.setIpAddress(InetAddress.getLocalHost().getHostAddress());
		info.setLatitude(df.format(latitude));
		info.setLongitude(df.format(longitude));
        
		
		return info;

	}
	
	
}
