package example.citypulse.monitor.resources;

import java.util.Iterator;

import org.prime.core.PrimeResource;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import example.citypulse.data.SensorInfo;
import example.citypulse.monitor.SensorMonitor;

public class MapAreaResource extends PrimeResource{

	@Get()
	public Representation form(){
		String rep = "<html> \n"
				+ "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				+ "<title>Map Sensors within an Area</title></head>\n"
				+ "<body>\n<p>Area to map</p>";

		rep += "<form name=\"input\" action=\"http://"+ getCURI() +"\" method=\"post\">"
				+ "<input type=\"hidden\" name=\"who\" value=\"query\">"
				+ "BL Point: lat<input type=\"text\" name=\"bl-lat\"> lon<input type=\"text\" name=\"bl-lon\"><br/>"
				+ "UR Point: lat<input type=\"text\" name=\"ur-lat\"> lon<input type=\"text\" name=\"ur-lon\">" 
				+ "<input type=\"submit\" value=\"SEARCH\"></form>";

		rep += "</body></html>";
		return new StringRepresentation(rep, MediaType.TEXT_HTML);
	}
	
	
	@Post()
    public Response toImage(Representation entity){
    	
		Form form = new Form(entity);
		String bl_lat = form.getFirstValue("bl-lat");
		String bl_lon = form.getFirstValue("bl-lon");
		String ur_lat = form.getFirstValue("ur-lat");
		String ur_lon = form.getFirstValue("ur-lon");

			
		String querystr = "PREFIX ex: <http://www.erc-smscom.org/ontologies/2013/12/Devices#> ";
		querystr += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
		querystr += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
		querystr += "PREFIX rdl: <http://www.erc-smscom.org/ontologies/2013/4/ResourceDescription#> ";
		querystr += "PREFIX owl: <http://www.w3.org/2002/07/owl#> ";
		querystr += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n";
		
		querystr += "SELECT DISTINCT ?curi ?lat ?lon \n"
				+ "WHERE {"
				+ "?res a rdl:Resource . \n" 
			    + "?res rdl:cURI ?curi . \n"
				+ "?res rdl:hasContext ?location . \n"
			    + "?location rdl:latitude ?lat . \n"
				+ "FILTER ( xsd:float(?lat) > " + bl_lat + " && xsd:float(?lat) < " + ur_lat + ") \n"
				+ "?location rdl:longitude ?lon . \n"
				+ "FILTER ( xsd:float(?lon) > " + bl_lon + " && xsd:float(?lon) < " + ur_lon +" ) \n"
				+ "} ";  
		
				
    	String url = "http://maps.googleapis.com/maps/api/staticmap?zoom=1&size=1000x1000&markers=color:red";
    	for (Iterator<SensorInfo> i = ((SensorMonitor) getBase()).getSensorsInArea(5000, querystr).values().iterator(); i.hasNext();){
    		SensorInfo info = i.next();
    		url = url.concat("%7C" + info.getLatitude()+","+info.getLongitude());
    	}
    	url = url.concat("&sensor=false");
    	
    	log.info("Sensors map url: "+url);
    	
		Request request = new Request(Method.GET, url);
		request.setReferrerRef("http://www.erc-smscom.org"); 
		
		Client client = new Client(Protocol.HTTP); 
        Response response = client.handle(request); 

		getResponse().setEntity(response.getEntity()); 
		
        return response;
    }
	
}
