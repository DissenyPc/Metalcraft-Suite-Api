package com.carpinteria.api.service;

import com.carpinteria.api.exception.ResourceNotFoundException;
import com.carpinteria.api.model.HardwareAccessory;
import com.carpinteria.api.repository.HardwareAccessoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HardwareAccessoryService {

    private final HardwareAccessoryRepository repository;

    public HardwareAccessoryService(HardwareAccessoryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<HardwareAccessory> getAll(String category, String query) {
        if (query != null && !query.isBlank()) {
            return repository.search(query.trim());
        }
        if (category != null && !category.isBlank()) {
            return repository.findByCategoryIgnoreCase(category.trim());
        }
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public HardwareAccessory getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Herraje/Accesorio", "id", id));
    }

    @Transactional(readOnly = true)
    public HardwareAccessory getByReference(String reference) {
        return repository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Herraje/Accesorio", "referencia", reference));
    }

    public HardwareAccessory create(HardwareAccessory item) {
        if (repository.findByReference(item.getReference()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un accesorio con la referencia: " + item.getReference());
        }
        return repository.save(item);
    }

    public HardwareAccessory update(Long id, HardwareAccessory updated) {
        HardwareAccessory existing = getById(id);

        if (!existing.getReference().equalsIgnoreCase(updated.getReference())
                && repository.findByReference(updated.getReference()).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro accesorio con la referencia: " + updated.getReference());
        }

        existing.setReference(updated.getReference());
        existing.setName(updated.getName());
        existing.setCategory(updated.getCategory());
        existing.setBrand(updated.getBrand());
        existing.setSupplier(updated.getSupplier());
        existing.setUnit(updated.getUnit());
        existing.setCostPrice(updated.getCostPrice());
        existing.setSalePrice(updated.getSalePrice());
        existing.setStockQuantity(updated.getStockQuantity());
        existing.setMinStockAlert(updated.getMinStockAlert());
        existing.setNotes(updated.getNotes());
        if (updated.getActive() != null) {
            existing.setActive(updated.getActive());
        }

        return repository.save(existing);
    }

    public void delete(Long id) {
        HardwareAccessory existing = getById(id);
        repository.delete(existing);
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return repository.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public List<HardwareAccessory> getLowStockAlerts() {
        return repository.findLowStock();
    }
}
