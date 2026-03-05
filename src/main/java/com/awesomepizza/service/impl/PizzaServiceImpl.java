package com.awesomepizza.service.impl;

import com.awesomepizza.dto.response.PizzaResponse;
import com.awesomepizza.exception.PizzaNotFoundException;
import com.awesomepizza.mapper.OrderMapper;
import com.awesomepizza.repository.PizzaRepository;
import com.awesomepizza.service.PizzaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Pizza service implementation.
 * All operations are read-only (readOnly = true) since the menu is static.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PizzaServiceImpl implements PizzaService {

    private final PizzaRepository pizzaRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<PizzaResponse> getAllPizzas() {
        return pizzaRepository.findAll().stream()
                .map(orderMapper::toPizzaResponse)
                .toList();
    }

    @Override
    public PizzaResponse getPizzaById(UUID id) {
        return pizzaRepository.findById(id)
                .map(orderMapper::toPizzaResponse)
                .orElseThrow(() -> new PizzaNotFoundException(id));
    }
}
