package example.ehealth.monitor;

import java.io.Serializable;
import java.util.Properties;

import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;

public class MonitoringInfo extends Properties implements Comparable<MonitoringInfo>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1226275003365604850L;
	
	private CURI curi;
	private AURI auri;
	private long timestamp;
	
	public MonitoringInfo(AURI auri, CURI curi){
		this.curi = curi;
		this.timestamp = System.currentTimeMillis();
	}
	
	public long getTimestamp(){
		return this.timestamp;
	}
	
	
	
	public CURI getCURI() {
		return curi;
	}

	public void setCURI(CURI curi) {
		this.curi = curi;
	}

	public AURI getAURI() {
		return auri;
	}

	public void setAURI(AURI auri) {
		this.auri = auri;
	}


	@Override
	public int compareTo(MonitoringInfo o) {
		if (this.timestamp < o.timestamp)
			return -1;
		
		if (this.timestamp == o.timestamp)
			return 0;
			
		return 1;
	}

	
	

	
}
