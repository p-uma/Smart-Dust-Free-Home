package org.uci.iot.smarthome.dustSensor.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uci.iot.smarthome.dustSensor.dao.DustMapper;
import org.uci.iot.smarthome.dustSensor.vo.Dust;

@Component
public class DataServiceImpl implements DataService {

	@Autowired
	private DustMapper dustMapper;
	
	@Override
	public void pushData(Dust dust) {
		//System.out.println("Inserting +"+dust.getDate()+" "+dust.getDensity());
		dustMapper.insertData(dust);
	}
	
	public List<Dust> getData(Date startDate, Date endDate){
		return dustMapper.getData(startDate,endDate);
	}
}
