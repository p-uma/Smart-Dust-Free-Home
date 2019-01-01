package org.uci.iot.smarthome.dustSensor.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.uci.iot.smarthome.dustSensor.vo.Dust;

public interface DustMapper {
	
	public void insertData(Dust dust);

	public List<Dust> getData(@Param("start")Date startDate, @Param("end")Date endDate);
}
