package com.carpinteria.api.service;

import com.carpinteria.api.exception.ResourceNotFoundException;
import com.carpinteria.api.model.FillingMaterial;
import com.carpinteria.api.repository.FillingMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FillingMaterialService {

    private final FillingMaterialRepository repository;

    public FillingMaterialService(FillingMaterialRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<FillingMaterial> getAll(String materialType, String query) {
        if (query != null && !query.isBlank()) {
            return repository.search(query.trim());
        }
        if (materialType != null && !materialType.isBlank()) {
            return repository.findByMaterialTypeIgnoreCase(materialType.trim());
        }
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public FillingMaterial getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material de relleno / vidrio", "id", id));
    }

    @Transactional(readOnly = true)
    public FillingMaterial getByReference(String reference) {
        return repository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Material de relleno / vidrio", "referencia", reference));
    }

    public FillingMaterial create(FillingMaterial item) {
        if (repository.findByReference(item.getReference()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un material de relleno con la referencia: " + item.getReference());
        }
        return repository.save(item);
    }

    public FillingMaterial update(Long id, FillingMaterial updated) {
        FillingMaterial existing = getById(id);

        if (!existing.getReference().equalsIgnoreCase(updated.getReference())
                && repository.findByReference(updated.getReference()).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro material con la referencia: " + updated.getReference());
        }

        existing.setReference(updated.getReference());
        existing.setName(updated.getName());
        existing.setMaterialType(updated.getMaterialType());
        existing.setFinishColor(updated.getFinishColor());
        existing.setThicknessMm(updated.getThicknessMm());
        existing.setUnit(updated.getUnit());
        existing.setPricePerSquareMeter(updated.getPricePerSquareMeter());
        existing.setCostPrice(updated.getCostPrice());
        existing.setStockM2(updated.getStockM2());
        existing.setMinWidthMm(updated.getMinWidthMm());
        existing.setMinHeightMm(updated.getMinHeightMm());
        existing.setMaxWidthMm(updated.getMaxWidthMm());
        existing.setMaxHeightMm(updated.getMaxHeightMm());
        existing.setDescription(updated.getDescription());
        if (updated.getActive() != null) {
            existing.setActive(updated.getActive());
        }

        return repository.save(existing);
    }

    public void delete(Long id) {
        FillingMaterial existing = getById(id);
        repository.delete(existing);
    }

    @Transactional(readOnly = true)
    public List<String> getMaterialTypes() {
        return repository.findDistinctMaterialTypes();
    }
}
