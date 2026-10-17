package com.carpinteria.api.service;

import com.carpinteria.api.dto.BulkPriceUpdateRequest;
import com.carpinteria.api.dto.BulkPriceUpdateResult;
import com.carpinteria.api.exception.ResourceNotFoundException;
import com.carpinteria.api.model.AluminiumProfile;
import com.carpinteria.api.repository.AluminiumProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class AluminiumProfileService {

    private final AluminiumProfileRepository repository;

    public AluminiumProfileService(AluminiumProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AluminiumProfile> getAll(String series, String query) {
        if (query != null && !query.isBlank()) {
            return repository.search(query.trim());
        }
        if (series != null && !series.isBlank()) {
            return repository.findBySeriesIgnoreCase(series.trim());
        }
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public AluminiumProfile getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de aluminio", "id", id));
    }

    @Transactional(readOnly = true)
    public AluminiumProfile getByReference(String reference) {
        return repository.findFirstByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de aluminio", "referencia", reference));
    }

    public AluminiumProfile create(AluminiumProfile profile) {
        if (repository.findFirstByReference(profile.getReference()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un perfil de aluminio con la referencia: " + profile.getReference());
        }
        // Auto compute bar price if missing or zero
        if ((profile.getPricePerBar() == null || profile.getPricePerBar().compareTo(BigDecimal.ZERO) == 0)
                && profile.getPricePerMeter() != null && profile.getLengthMeters() != null) {
            profile.setPricePerBar(profile.getPricePerMeter().multiply(profile.getLengthMeters()).setScale(2, RoundingMode.HALF_UP));
        }
        return repository.save(profile);
    }

    public AluminiumProfile update(Long id, AluminiumProfile updated) {
        AluminiumProfile existing = getById(id);

        if (!existing.getReference().equalsIgnoreCase(updated.getReference())
                && repository.findFirstByReference(updated.getReference()).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro perfil con la referencia: " + updated.getReference());
        }

        existing.setReference(updated.getReference());
        existing.setName(updated.getName());
        existing.setSeries(updated.getSeries());
        existing.setFinish(updated.getFinish());
        existing.setLengthMeters(updated.getLengthMeters());
        existing.setWeightPerMeter(updated.getWeightPerMeter());
        existing.setPricePerMeter(updated.getPricePerMeter());
        existing.setPricePerBar(updated.getPricePerBar());
        existing.setStockBars(updated.getStockBars());
        existing.setDescription(updated.getDescription());
        if (updated.getActive() != null) {
            existing.setActive(updated.getActive());
        }

        // Finishes
        existing.setPricePlata(updated.getPricePlata());
        existing.setPriceLBlanco(updated.getPriceLBlanco());
        existing.setPriceColStd(updated.getPriceColStd());
        existing.setPriceLColor(updated.getPriceLColor());
        existing.setPriceBronce(updated.getPriceBronce());
        existing.setPriceInox(updated.getPriceInox());
        existing.setPriceMadera(updated.getPriceMadera());
        existing.setPriceEzzy(updated.getPriceEzzy());
        existing.setPriceMetal(updated.getPriceMetal());
        existing.setPriceAnodNegro(updated.getPriceAnodNegro());
        existing.setPriceBicol01(updated.getPriceBicol01());
        existing.setPriceBicol08(updated.getPriceBicol08());
        existing.setPriceBicol09(updated.getPriceBicol09());

        return repository.save(existing);
    }

    public void delete(Long id) {
        AluminiumProfile existing = getById(id);
        repository.delete(existing);
    }

    @Transactional(readOnly = true)
    public List<String> getSeriesList() {
        return repository.findDistinctSeries();
    }

    /**
     * Executes bulk price updates for aluminium profiles.
     * Can target a specific series or all profiles in the table.
     * Can apply percentage change (+/- %) or fixed delta (+/- €).
     */
    public BulkPriceUpdateResult bulkUpdatePrices(BulkPriceUpdateRequest request) {
        List<AluminiumProfile> targets;
        String targetSeriesDesc = "TODAS";

        if (request.getSeries() != null && !request.getSeries().isBlank()) {
            targets = repository.findBySeriesIgnoreCase(request.getSeries().trim());
            targetSeriesDesc = request.getSeries().trim();
        } else {
            targets = repository.findAll();
        }

        if (targets.isEmpty()) {
            return new BulkPriceUpdateResult(
                    "ALUMINIUM",
                    targetSeriesDesc,
                    0,
                    request.getPercentage(),
                    request.getFixedAmount(),
                    request.getTargetPriceField(),
                    "No se encontraron perfiles de aluminio para actualizar."
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
        for (AluminiumProfile p : targets) {
            boolean updateMeter = field.equals("ALL") || field.equals("PRICEPERMETER");
            boolean updateBar = field.equals("ALL") || field.equals("PRICEPERBAR");
            boolean updateFinishes = field.equals("ALL") || field.equals("FINISHES");

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

            if (updateFinishes) {
                p.setPricePlata(adjustFinishPrice(p.getPricePlata(), multiplier, fixedAmount));
                p.setPriceLBlanco(adjustFinishPrice(p.getPriceLBlanco(), multiplier, fixedAmount));
                p.setPriceColStd(adjustFinishPrice(p.getPriceColStd(), multiplier, fixedAmount));
                p.setPriceLColor(adjustFinishPrice(p.getPriceLColor(), multiplier, fixedAmount));
                p.setPriceBronce(adjustFinishPrice(p.getPriceBronce(), multiplier, fixedAmount));
                p.setPriceInox(adjustFinishPrice(p.getPriceInox(), multiplier, fixedAmount));
                p.setPriceMadera(adjustFinishPrice(p.getPriceMadera(), multiplier, fixedAmount));
                p.setPriceEzzy(adjustFinishPrice(p.getPriceEzzy(), multiplier, fixedAmount));
                p.setPriceMetal(adjustFinishPrice(p.getPriceMetal(), multiplier, fixedAmount));
                p.setPriceAnodNegro(adjustFinishPrice(p.getPriceAnodNegro(), multiplier, fixedAmount));
                p.setPriceBicol01(adjustFinishPrice(p.getPriceBicol01(), multiplier, fixedAmount));
                p.setPriceBicol08(adjustFinishPrice(p.getPriceBicol08(), multiplier, fixedAmount));
                p.setPriceBicol09(adjustFinishPrice(p.getPriceBicol09(), multiplier, fixedAmount));
            }

            repository.save(p);
            count++;
        }

        String msg = String.format("Se han actualizado exitosamente %d perfiles de aluminio en la serie '%s'.", count, targetSeriesDesc);
        return new BulkPriceUpdateResult(
                "ALUMINIUM",
                targetSeriesDesc,
                count,
                percentage,
                fixedAmount,
                field,
                msg
        );
    }

    private BigDecimal adjustFinishPrice(BigDecimal current, BigDecimal multiplier, BigDecimal fixedAmount) {
        if (current == null || current.compareTo(new BigDecimal("9000")) >= 0) {
            return current;
        }
        BigDecimal res = current.multiply(multiplier);
        if (fixedAmount != null) {
            res = res.add(fixedAmount);
        }
        if (res.compareTo(BigDecimal.ZERO) < 0) res = BigDecimal.ZERO;
        return res.setScale(2, RoundingMode.HALF_UP);
    }
}
