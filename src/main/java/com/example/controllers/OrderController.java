package com.example.controllers;

import java.sql.Date;

import com.example.TenantContext;
import com.example.domain.Order;
import com.example.domain.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class OrderController {
    
    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping(path = "/orders", method= RequestMethod.POST)
    public ResponseEntity<?> createSampleOrder(@RequestHeader("X-TenantID") String tenantName) {
        TenantContext.setCurrentTenant(tenantName);

        Order newOrder = new Order(new Date(System.currentTimeMillis()));
        orderRepository.save(newOrder);

        return ResponseEntity.ok(newOrder);
    }

    @RequestMapping(path = "/orders", method= RequestMethod.GET)
    public ResponseEntity<Iterable<Order>> getOrders(@RequestHeader("X-TenantID") String tenantName) {
        TenantContext.setCurrentTenant(tenantName);
        return ResponseEntity.ok(orderRepository.findAll());
    }
}
