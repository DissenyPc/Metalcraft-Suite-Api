package com.carpinteria.api.model;

public enum BudgetStatus {
    DRAFT,             // Borrador
    PENDING_APPROVAL,  // Pendiente de aprobación
    APPROVED,          // Aprobado por el cliente
    REJECTED,          // Rechazado
    INVOICED,          // Facturado
    CANCELLED          // Cancelado
}
