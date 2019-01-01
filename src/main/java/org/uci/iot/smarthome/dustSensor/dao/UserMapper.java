package org.uci.iot.smarthome.dustSensor.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.uci.iot.smarthome.dustSensor.vo.User;

@Mapper
public interface UserMapper {

	public User getUser(@Param("id") int id);
}
