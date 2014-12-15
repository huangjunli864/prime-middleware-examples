package example.citypulse.monitor.resources;

import java.util.Iterator;

import org.prime.core.PrimeResource;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import example.citypulse.data.SensorInfo;
import example.citypulse.monitor.SensorMonitor;

public class MapObservedResource extends PrimeResource{


	@Get()
    public Representation toImage(Representation entity){
				
		String imgurl = "http://maps.googleapis.com/maps/api/staticmap?zoom=1&size=1000x1000&markers=color:red";
		for (Iterator<SensorInfo> i = ((SensorMonitor) getBase()).getSensorList().iterator(); i.hasNext();){
			SensorInfo info = i.next();
			imgurl = imgurl.concat("%7C" + info.getLatitude()+","+info.getLongitude());
		}
		imgurl = imgurl.concat("&sensor=false");

		String rep = "<html> \n"
				+ "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				+ "<meta http-equiv=\"refresh\" content=\"2\"/>"
				+ "<title>Map Observed Sensors</title></head>\n"
				+ "<body>";

		rep += "<img src=\"" + imgurl +"\">"; 		
		rep += "</body></html>";

		setStatus(Status.SUCCESS_OK);
		return new StringRepresentation(rep, MediaType.TEXT_HTML);
    	
    	
		
    }
	
}
