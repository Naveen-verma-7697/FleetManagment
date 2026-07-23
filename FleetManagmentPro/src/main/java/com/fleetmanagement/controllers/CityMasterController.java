package com.fleetmanagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fleetmanagement.entity.*;
import com.fleetmanagement.service.CityMasterService;


@RestController
@RequestMapping("/api")
public class CityMasterController {

@Autowired
CityMasterService cityMasterService;
	
@GetMapping("/getAll")
public List<CityMaster> getAll(){
	
	return cityMasterService.getAll();
}
}
