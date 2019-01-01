package org.uci.iot.smarthome.dustSensor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uci.iot.smarthome.dustSensor.dao.UserMapper;
import org.uci.iot.smarthome.dustSensor.vo.User;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;
	
	public User getUser(int id) {
		return userMapper.getUser(id);
	}

}
