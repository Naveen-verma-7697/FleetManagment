/**
 * 
 */
package com.fleetmanagement.entity;

/**
 * Car category Entity 
 * 
 * database
 * INSERT INTO car_category (category_name) VALUES
('Intermediate'),
('Sedan'),
('SUV'),
('Hatchback'),
('Compact'),
('Luxury'),
('Premium'),
('MUV'),
('Convertible'),
('Electric');
 */

import jakarta.persistence.*;

@Entity
@Table(name = "car_category")
public class CarCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "category_name")
    private String categoryName;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
