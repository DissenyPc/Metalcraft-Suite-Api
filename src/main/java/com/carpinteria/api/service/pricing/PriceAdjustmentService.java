package com.carpinteria.api.service.pricing;

import com.carpinteria.api.dto.pricing.BatchPriceAdjustmentRequest;
import com.carpinteria.api.dto.pricing.BatchPriceAdjustmentResult;
import com.carpinteria.api.model.AluminiumProfile;
import com.carpinteria.api.model.FillingMaterial;
import com.carpinteria.api.model.HardwareAccessory;
import com.carpinteria.api.model.IronProfile;
import com.carpinteria.api.repository.AluminiumProfileRepository;
import com.carpinteria.api.repository.FillingMaterialRepository;
import com.carpinteria.api.repository.HardwareAccessoryRepository;
import com.carpinteria.api.repository.IronProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Service 2: Dynamic Price Adjustment Service
 * Performs batch price adjustments (+/- %) across an entire table or filtered by a specific series/category.
 */
@Service
@Transactional
public class PriceAdjustmentService {

    private final AluminiumProfileRepository aluminiumRepo;
    private final IronProfileRepository ironRepo;
    private final HardwareAccessoryRepository hardwareRepo;
    private final FillingMaterialRepository fillingRepo;

    public PriceAdjustmentService(AluminiumProfileRepository aluminiumRepo,
                                  IronProfileRepository ironRepo,
                                  HardwareAccessoryRepository hardwareRepo,
                                  FillingMaterialRepository fillingRepo) {
        this.aluminiumRepo = aluminiumRepo;
        this.ironRepo = ironRepo;
        this.hardwareRepo = hardwareRepo;
        this.fillingRepo = fillingRepo;
    }

    public BatchPriceAdjustmentResult adjustPrices(BatchPriceAdjustmentRequest request) {
        String targetTable = request.getTargetTable();
        String filter = (request.getSeriesFilter() != null && !request.getSeriesFilter().isBlank() && !"ALL".equalsIgnoreCase(request.getSeriesFilter()))
                ? request.getSeriesFilter().trim()
                : null;
        BigDecimal percentage = request.getPercentage();
        String field = request.getPriceField();

        BigDecimal multiplier = BigDecimal.ONE.add(
                percentage.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
        );

        int updatedCount = 0;
        String filterDesc = filter != null ? filter : "TODA LA TABLA";

        switch (targetTable) {
            case "ALUMINIUM_PROFILES":
                updatedCount = adjustAluminiumProfiles(filter, multiplier, field);
                break;

            case "IRON_PROFILES":
                updatedCount = adjustIronProfiles(filter, multiplier, field);
                break;

            case "HARDWARE_ACCESSORIES":
                updatedCount = adjustHardwareAccessories(filter, multiplier, field);
                break;

            case "FILLING_MATERIALS":
                updatedCount = adjustFillingMaterials(filter, multiplier, field);
                break;

            default:
                throw new IllegalArgumentException("Tabla objetivo no válida: " + targetTable +
                        ". Opciones válidas: ALUMINIUM_PROFILES, IRON_PROFILES, HARDWARE_ACCESSORIES, FILLING_MATERIALS.");
        }

        String sign = percentage.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        String msg = String.format("Ajuste dinámico del %s%.2f%% aplicado con éxito a %d registros en '%s' (Filtro: %s).",
                sign, percentage, updatedCount, targetTable, filterDesc);

        return new BatchPriceAdjustmentResult(
                targetTable,
                filterDesc,
                updatedCount,
                percentage,
                field,
                msg
        );
    }

    private int adjustAluminiumProfiles(String seriesFilter, BigDecimal multiplier, String field) {
        List<AluminiumProfile> profiles = (seriesFilter != null)
                ? aluminiumRepo.findBySeriesIgnoreCase(seriesFilter)
                : aluminiumRepo.findAll();

        boolean updateMeter = "ALL".equalsIgnoreCase(field) || "PRICEPERMETER".equalsIgnoreCase(field);
        boolean updateBar = "ALL".equalsIgnoreCase(field) || "PRICEPERBAR".equalsIgnoreCase(field);
        boolean updateFinishes = "ALL".equalsIgnoreCase(field) || "FINISHES".equalsIgnoreCase(field);

        for (AluminiumProfile p : profiles) {
            if (updateMeter && p.getPricePerMeter() != null) {
                BigDecimal newMeter = p.getPricePerMeter().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
                if (newMeter.compareTo(BigDecimal.ZERO) < 0) newMeter = BigDecimal.ZERO;
                p.setPricePerMeter(newMeter);
            }
            if (updateBar && p.getPricePerBar() != null) {
                BigDecimal newBar = p.getPricePerBar().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
                if (newBar.compareTo(BigDecimal.ZERO) < 0) newBar = BigDecimal.ZERO;
                p.setPricePerBar(newBar);
            }
            if (updateFinishes) {
                p.setPricePlata(adjustFinishPrice(p.getPricePlata(), multiplier));
                p.setPriceLBlanco(adjustFinishPrice(p.getPriceLBlanco(), multiplier));
                p.setPriceColStd(adjustFinishPrice(p.getPriceColStd(), multiplier));
                p.setPriceLColor(adjustFinishPrice(p.getPriceLColor(), multiplier));
                p.setPriceBronce(adjustFinishPrice(p.getPriceBronce(), multiplier));
                p.setPriceInox(adjustFinishPrice(p.getPriceInox(), multiplier));
                p.setPriceMadera(adjustFinishPrice(p.getPriceMadera(), multiplier));
                p.setPriceEzzy(adjustFinishPrice(p.getPriceEzzy(), multiplier));
                p.setPriceMetal(adjustFinishPrice(p.getPriceMetal(), multiplier));
                p.setPriceAnodNegro(adjustFinishPrice(p.getPriceAnodNegro(), multiplier));
                p.setPriceBicol01(adjustFinishPrice(p.getPriceBicol01(), multiplier));
                p.setPriceBicol08(adjustFinishPrice(p.getPriceBicol08(), multiplier));
                p.setPriceBicol09(adjustFinishPrice(p.getPriceBicol09(), multiplier));
            }
            aluminiumRepo.save(p);
        }
        return profiles.size();
    }

    private BigDecimal adjustFinishPrice(BigDecimal current, BigDecimal multiplier) {
        if (current == null || current.compareTo(new BigDecimal("9000")) >= 0) {
            return current;
        }
        BigDecimal res = current.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        return res.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : res;
    }

    private int adjustIronProfiles(String seriesFilter, BigDecimal multiplier, String field) {
        List<IronProfile> profiles = (seriesFilter != null)
                ? ironRepo.findBySeriesIgnoreCase(seriesFilter)
                : ironRepo.findAll();

        boolean updateKg = "ALL".equalsIgnoreCase(field) || "PRICEPERKG".equalsIgnoreCase(field);
        boolean updateMeter = "ALL".equalsIgnoreCase(field) || "PRICEPERMETER".equalsIgnoreCase(field);
        boolean updateBar = "ALL".equalsIgnoreCase(field) || "PRICEPERBAR".equalsIgnoreCase(field);

        for (IronProfile p : profiles) {
            if (updateKg && p.getPricePerKg() != null) {
                BigDecimal newKg = p.getPricePerKg().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
                if (newKg.compareTo(BigDecimal.ZERO) < 0) newKg = BigDecimal.ZERO;
                p.setPricePerKg(newKg);
            }
            if (updateMeter && p.getPricePerMeter() != null) {
                BigDecimal newMeter = p.getPricePerMeter().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
                if (newMeter.compareTo(BigDecimal.ZERO) < 0) newMeter = BigDecimal.ZERO;
                p.setPricePerMeter(newMeter);
            }
            if (updateBar && p.getPricePerBar() != null) {
                BigDecimal newBar = p.getPricePerBar().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
                if (newBar.compareTo(BigDecimal.ZERO) < 0) newBar = BigDecimal.ZERO;
                p.setPricePerBar(newBar);
            }
            ironRepo.save(p);
        }
        return profiles.size();
    }

    private int adjustHardwareAccessories(String categoryFilter, BigDecimal multiplier, String field) {
        List<HardwareAccessory> items = (categoryFilter != null)
                ? hardwareRepo.findByCategoryIgnoreCase(categoryFilter)
                : hardwareRepo.findAll();

        boolean updateSale = "ALL".equalsIgnoreCase(field) || "SALEPRICE".equalsIgnoreCase(field);
        boolean updateCost = "ALL".equalsIgnoreCase(field) || "COSTPRICE".equalsIgnoreCase(field);

        for (HardwareAccessory h : items) {
            if (updateSale && h.getSalePrice() != null) {
                BigDecimal newSale = h.getSalePrice().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
                if (newSale.compareTo(BigDecimal.ZERO) < 0) newSale = BigDecimal.ZERO;
                h.setSalePrice(newSale);
            }
            if (updateCost && h.getCostPrice() != null) {
                BigDecimal newCost = h.getCostPrice().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
                if (newCost.compareTo(BigDecimal.ZERO) < 0) newCost = BigDecimal.ZERO;
                h.setCostPrice(newCost);
            }
            hardwareRepo.save(h);
        }
        return items.size();
    }

    private int adjustFillingMaterials(String typeFilter, BigDecimal multiplier, String field) {
        List<FillingMaterial> items = (typeFilter != null)
                ? fillingRepo.findByMaterialTypeIgnoreCase(typeFilter)
                : fillingRepo.findAll();

        boolean updateSale = "ALL".equalsIgnoreCase(field) || "PRICEPERSQUAREMETER".equalsIgnoreCase(field) || "PRICE".equalsIgnoreCase(field);
        boolean updateCost = "ALL".equalsIgnoreCase(field) || "COSTPRICE".equalsIgnoreCase(field);

        for (FillingMaterial fm : items) {
            if (updateSale && fm.getPricePerSquareMeter() != null) {
                BigDecimal newPrice = fm.getPricePerSquareMeter().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
                if (newPrice.compareTo(BigDecimal.ZERO) < 0) newPrice = BigDecimal.ZERO;
                fm.setPricePerSquareMeter(newPrice);
            }
            if (updateCost && fm.getCostPrice() != null) {
                BigDecimal newCost = fm.getCostPrice().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
                if (newCost.compareTo(BigDecimal.ZERO) < 0) newCost = BigDecimal.ZERO;
                fm.setCostPrice(newCost);
            }
            fillingRepo.save(fm);
        }
        return items.size();
    }
}
