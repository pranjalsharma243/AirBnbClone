package com.myprojects.projects.airbnb.repository;

import com.myprojects.projects.airbnb.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
