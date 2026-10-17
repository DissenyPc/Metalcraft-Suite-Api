package com.carpinteria.api.service;

import com.carpinteria.api.dto.BulkPriceUpdateRequest;
import com.carpinteria.api.dto.BulkPriceUpdateResult;
import com.carpinteria.api.exception.ResourceNotFoundException;
import com.carpinteria.api.model.IronProfile;
import com.carpinteria.api.repository.IronProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class IronProfileService {

    private final IronProfileRepository repository;

    public IronProfileService(IronProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<IronProfile> getAll(String series, String query) {
        if (query != null && !query.isBlank()) {
            return repository.search(query.trim());
        }
        if (series != null && !series.isBlank()) {
            return repository.findBySeriesIgnoreCase(series.trim());
        }
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public IronProfile getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de hierro", "id", id));
    }

    @Transactional(readOnly = true)
    public IronProfile getByReference(String reference) {
        return repository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de hierro", "referencia", reference));
    }

    public IronProfile create(IronProfile profile) {
        if (repository.findByReference(profile.getReference()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un perfil de hierro con la referencia: " + profile.getReference());
        }
        // Auto compute bar price if missing or zero
        if ((profile.getPricePerBar() == null || profile.getPricePerBar().compareTo(BigDecimal.ZERO) == 0)
                && profile.getPricePerMeter() != null && profile.getLengthMeters() != null) {
            profile.setPricePerBar(profile.getPricePerMeter().multiply(profile.getLengthMeters()).setScale(2, RoundingMode.HALF_UP));
        }
        return repository.save(profile);
    }

    public IronProfile update(Long id, IronProfile updated) {
        IronProfile existing = getById(id);

        if (!existing.getReference().equalsIgnoreCase(updated.getReference())
                && repository.findByReference(updated.getReference()).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro perfil de hierro con la referencia: " + updated.getReference());
        }

        existing.setReference(updated.getReference());
        existing.setName(updated.getName());
        existing.setSeries(updated.getSeries());
        existing.setThicknessMm(updated.getThicknessMm());
        existing.setLengthMeters(updated.getLengthMeters());
        existing.setWeightPerMeter(updated.getWeightPerMeter());
        existing.setPricePerKg(updated.getPricePerKg());
        existing.setPricePerMeter(updated.getPricePerMeter());
        existing.setPricePerBar(updated.getPricePerBar());
        existing.setStockBars(updated.getStockBars());
        existing.setDescription(updated.getDescription());
        if (updated.getActive() != null) {
            existing.setActive(updated.getActive());
        }

        return repository.save(existing);
    }

    public void delete(Long id) {
        IronProfile existing = getById(id);
        repository.delete(existing);
    }

    @Transactional(readOnly = true)
    public List<String> getSeriesList() {
        return repository.findDistinctSeries();
    }

    /**
     * Executes bulk price updates for iron/steel profiles.
     * Can target a specific series/profileType or all profiles in the table.
     * Can apply percentage change (+/- %) or fixed delta (+/- €).
     */
    public BulkPriceUpdateResult bulkUpdatePrices(BulkPriceUpdateRequest request) {
        List<IronProfile> targets;
        String targetSeriesDesc = "TODAS";

        if (request.getSeries() != null && !request.getSeries().isBlank()) {
            targets = repository.findBySeriesIgnoreCase(request.getSeries().trim());
            targetSeriesDesc = request.getSeries().trim();
        } else {
            targets = repository.findAll();
        }

        if (targets.isEmpty()) {
            return new BulkPriceUpdateResult(
                    "IRON",
                    targetSeriesDesc,
                    0,
                    request.getPercentage(),
                    request.getFixedAmount(),
                    request.getTargetPriceField(),
                    "No se encontraron perfiles de hierro para actualizar."
            );
        }

        BigDecimal percentage = request.getPercentage();
        BigDecimal fixedAmount = request.getFixedAmount();
        String field = request.getTargetPriceField() != null ? request.getTargetPriceField().toUpperCase() : "ALL";

        BigDecimal multiplier = BigDecimal.ONE;
        if (percentage != null) {
            multiplier = BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        }

        int count = 0;
        for (IronProfile p : targets) {
            boolean updateKg = field.equals("ALL") || field.equals("PRICEPERKG");
            boolean updateMeter = field.equals("ALL") || field.equals("PRICEPERMETER");
            boolean updateBar = field.equals("ALL") || field.equals("PRICEPERBAR");

            if (updateKg && p.getPricePerKg() != null) {
                BigDecimal newKg = p.getPricePerKg().multiply(multiplier);
                if (fixedAmount != null) {
                    newKg = newKg.add(fixedAmount);
                }
                if (newKg.compareTo(BigDecimal.ZERO) < 0) newKg = BigDecimal.ZERO;
                p.setPricePerKg(newKg.setScale(2, RoundingMode.HALF_UP));
            }

            if (updateMeter && p.getPricePerMeter() != null) {
                BigDecimal newMeter = p.getPricePerMeter().multiply(multiplier);
                if (fixedAmount != null) {
                    newMeter = newMeter.add(fixedAmount);
                }
                if (newMeter.compareTo(BigDecimal.ZERO) < 0) newMeter = BigDecimal.ZERO;
                p.setPricePerMeter(newMeter.setScale(2, RoundingMode.HALF_UP));
            }

            if (updateBar && p.getPricePerBar() != null) {
                BigDecimal newBar = p.getPricePerBar().multiply(multiplier);
                if (fixedAmount != null) {
                    newBar = newBar.add(fixedAmount);
                }
                if (newBar.compareTo(BigDecimal.ZERO) < 0) newBar = BigDecimal.ZERO;
                p.setPricePerBar(newBar.setScale(2, RoundingMode.HALF_UP));
            }

            repository.save(p);
            count++;
        }

        String msg = String.format("Se han actualizado exitosamente %d perfiles de hierro en la serie '%s'.", count, targetSeriesDesc);
        return new BulkPriceUpdateResult(
                "IRON",
                targetSeriesDesc,
                count,
                percentage,
                fixedAmount,
                field,
                msg
        );
    }
}
