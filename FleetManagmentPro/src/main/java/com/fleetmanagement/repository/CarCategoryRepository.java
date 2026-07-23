/**
 * 
 */
package com.fleetmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fleetmanagement.entity.CarCategory;

/**
 * 
 */
@Repository
public interface CarCategoryRepository extends JpaRepository<CarCategory,Integer> {

}
