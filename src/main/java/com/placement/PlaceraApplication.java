package com.placement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PlaceraApplication - Core entry point for the College Placement Management System.
 *
 * This Spring Boot application serves three distinct portals:
 * 1. Student Portal
 * 2. Company Portal
 * 3. Admin Portal
 *
 * Designed with clean Object-Oriented Programming (OOP) principles,
 * strict server-side role separation, and a layered enterprise architecture.
 */
@SpringBootApplication
public class PlaceraApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaceraApplication.class, args);
    }
}
