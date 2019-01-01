package org.uci.iot.smarthome.dustSensor.vo;

import java.text.DecimalFormat;
import java.util.Date;

public class Dust {
	
	
    public Dust() {
		super();
	}

	
	public Dust(Date date, float density, float humidity, float temperature, float wind) {
		super();
		this.date = date;
		this.density = density;
		this.humidity = humidity;
		this.temperature = temperature;
		this.wind = wind;
	}
	
	
	
	public Dust(Date date, float humidity, float temperature, float wind) {
		super();
		this.date = date;
		this.humidity = humidity;
		this.temperature = temperature;
		this.wind = wind;
	}
	
	float roundTwoDecimals(float d) {
	  DecimalFormat twoDForm = new DecimalFormat("#.##");
	  return Float.valueOf(twoDForm.format(d));
	}



	private int id;
	
	private Date date;
	
	private float density;
	
	private float humidity;
	
	private float temperature;
	
	private float wind;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getHumidity() {
		return humidity;
	}

	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}



	public float getWind() {
		return wind;
	}

	public void setWind(float wind) {
		this.wind = wind;
	}
	
}
