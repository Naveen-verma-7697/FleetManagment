/**
 * 
 */
package com.fleetmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fleetmanagement.entity.CityMaster;

/**
 * this is repo for access data to city table
 */
@Repository
public interface CityMasterRepository extends JpaRepository<CityMaster,Integer>{

}
