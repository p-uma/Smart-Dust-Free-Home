package org.uci.iot.smarthome.dustSensor.service;

import java.util.Date;
import java.util.List;

import org.uci.iot.smarthome.dustSensor.vo.Dust;

public interface DataService {

	public void pushData(Dust dust);
	
	public List<Dust> getData(Date startDate, Date endDate);
}
