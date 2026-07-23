/**
 * 
 */
package com.fleetmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fleetmanagement.entity.CarCategory;
import com.fleetmanagement.repository.CarCategoryRepository;

/**
 * 
 */
@Service
public class CarCategoryServiceImpl implements CarCategoryService{

	@Autowired
CarCategoryRepository carCategoryRepo;
	@Override
	public List<CarCategory> getAll() {
		// TODO Auto-generated method stub
		return carCategoryRepo.findAll();
	}

}
