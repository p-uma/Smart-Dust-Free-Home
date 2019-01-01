package org.uci.iot.smarthome.dustSensor.service;

public interface EmailService {
	
	public void sendMail(String to, String subject, String text);
}
