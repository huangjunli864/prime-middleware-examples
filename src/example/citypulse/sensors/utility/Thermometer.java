package example.citypulse.sensors.utility;


public class Thermometer {


	public double getValue() {
		return  (Math.round(Math.random() * 10000) / 100) % 40;
	}
}
