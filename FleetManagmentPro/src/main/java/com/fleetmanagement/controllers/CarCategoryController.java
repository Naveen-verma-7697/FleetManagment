/**
 * 
 */
package com.fleetmanagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fleetmanagement.entity.CarCategory;
import com.fleetmanagement.service.CarCategoryService;

/**
 * 
 */
@RestController
@RequestMapping("/api")
public class CarCategoryController {
@Autowired
CarCategoryService carCategoryService;

@GetMapping("/categories")
public List<CarCategory> getAll(){
		return carCategoryService.getAll();
	}



}
