package com.client.mobile.module_order.repository;

import com.client.mobile.module_order.entity.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllergenRepository extends JpaRepository<Allergen, Integer> {
    List<Allergen> findByNameContainingIgnoreCase(String name);
}