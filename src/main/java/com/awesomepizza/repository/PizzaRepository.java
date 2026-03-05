package com.awesomepizza.repository;

import com.awesomepizza.domain.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/** Repository for menu pizzas. Uses only the standard CRUD operations from JpaRepository. */
@Repository
public interface PizzaRepository extends JpaRepository<Pizza, UUID> {
}
