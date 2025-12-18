package com.client.mobile.module_order.repository;

import com.client.mobile.module_order.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {
    List<Dish> findByNameContainingIgnoreCase(String name);
    List<Dish> findByIsVegetarian(Boolean isVegetarian);
    List<Dish> findByIsVegan(Boolean isVegan);
    List<Dish> findByIsSpicy(Boolean isSpicy);
    List<Dish> findByActive(Boolean active);
    List<Dish> findByCategoryId(Integer categoryId);

    @Query("SELECT d FROM Dish d WHERE d.price BETWEEN :minPrice AND :maxPrice")
    List<Dish> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT d FROM Dish d JOIN d.allergens a WHERE a.id = :allergenId")
    List<Dish> findByAllergenId(@Param("allergenId") Integer allergenId);

    @Query("SELECT d FROM Dish d JOIN d.ingredients i WHERE i.id = :ingredientId")
    List<Dish> findByIngredientId(@Param("ingredientId") Integer ingredientId);
}