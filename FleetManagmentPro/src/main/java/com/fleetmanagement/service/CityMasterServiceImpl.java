/**
 * 
 */
package com.fleetmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fleetmanagement.entity.CityMaster;
import com.fleetmanagement.repository.CityMasterRepository;

/**
 * this is implementation of city service 
 */
@Service
public class CityMasterServiceImpl implements CityMasterService {
@Autowired
CityMasterRepository cityMasterRepository;
	@Override
	public List<CityMaster> getAll() {
		// TODO Auto-generated method stub
		return cityMasterRepository.findAll();
	}

}
