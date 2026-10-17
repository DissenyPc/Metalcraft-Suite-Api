package com.carpinteria.api.model;

/**
 * Roles available in the application for RBAC.
 * ADMIN  – Full access: manage users, inventory, budgets, clients, and settings.
 * EMPLOYEE – Restricted access: view inventory, create/edit budgets, view clients.
 */
public enum Role {
    ADMIN,
    EMPLOYEE
}
