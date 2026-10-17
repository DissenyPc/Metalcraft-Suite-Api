package com.carpinteria.api.service.calculator;

import com.carpinteria.api.dto.calculator.ProfileCutItem;
import com.carpinteria.api.dto.calculator.StructureCalculationRequest;
import com.carpinteria.api.dto.calculator.StructureCalculationResult;
import com.carpinteria.api.model.AluminiumProfile;
import com.carpinteria.api.model.IronProfile;
import com.carpinteria.api.repository.AluminiumProfileRepository;
import com.carpinteria.api.repository.IronProfileRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service 1: Structure Calculation Service
 * Handles total opening measurements (hueco obra), applies overlap (solape) deductions
 * (-2.5mm per side to height/width, resulting in -5.0mm total), computes exact profile cut dimensions,
 * and selects the closest superior commercial profile stock (6000mm / 6500mm bars) with cutting waste analysis.
 */
@Service
public class StructureCalculationService {

    private final AluminiumProfileRepository aluminiumRepo;
    private final IronProfileRepository ironRepo;

    public StructureCalculationService(AluminiumProfileRepository aluminiumRepo,
                                       IronProfileRepository ironRepo) {
        this.aluminiumRepo = aluminiumRepo;
        this.ironRepo = ironRepo;
    }

    public StructureCalculationResult calculate(StructureCalculationRequest request) {
        StructureCalculationResult result = new StructureCalculationResult();

        double openingW = request.getOpeningWidthMm();
        double openingH = request.getOpeningHeightMm();
        double solapePerSide = request.getSolapePerSideMm();

        result.setOpeningWidthMm(openingW);
        result.setOpeningHeightMm(openingH);
        result.setSolapePerSideMm(solapePerSide);

        // Deductions: 2.5mm per side -> total 5.0mm deduction on width and height
        double totalWidthDeduction = solapePerSide * 2.0;
        double totalHeightDeduction = solapePerSide * 2.0;
        result.setTotalSolapeWidthDeductionMm(totalWidthDeduction);
        result.setTotalSolapeHeightDeductionMm(totalHeightDeduction);

        double fabW = Math.max(0, openingW - totalWidthDeduction);
        double fabH = Math.max(0, openingH - totalHeightDeduction);
        result.setFabricationWidthMm(fabW);
        result.setFabricationHeightMm(fabH);

        // Generate cuts for standard perimeter frame
        List<ProfileCutItem> cuts = new ArrayList<>();
        cuts.add(new ProfileCutItem("Marco horizontal superior", fabW, 1, "45° INGLETE"));
        cuts.add(new ProfileCutItem("Marco horizontal inferior", fabW, 1, "45° INGLETE"));
        cuts.add(new ProfileCutItem("Marco vertical izquierdo", fabH, 1, "45° INGLETE"));
        cuts.add(new ProfileCutItem("Marco vertical derecho", fabH, 1, "45° INGLETE"));
        result.setCuts(cuts);

        double totalLengthMm = (fabW * 2.0) + (fabH * 2.0);
        result.setTotalProfileLengthMm(totalLengthMm);
        result.setTotalProfileLengthMeters(Math.round((totalLengthMm / 1000.0) * 100.0) / 100.0);

        // Determine commercial bar length
        double commercialBarLength = 6000.0; // default standard 6m
        BigDecimal unitPricePerMeter = null;
        BigDecimal unitPricePerBar = null;
        String profileName = null;

        if (request.getCommercialBarLengthMm() != null && request.getCommercialBarLengthMm() > 0) {
            commercialBarLength = request.getCommercialBarLengthMm();
        } else if (request.getProfileId() != null) {
            if ("IRON".equalsIgnoreCase(request.getProfileMaterial())) {
                Optional<IronProfile> ip = ironRepo.findById(request.getProfileId());
                if (ip.isPresent()) {
                    IronProfile profile = ip.get();
                    if (profile.getLengthMeters() != null) {
                        commercialBarLength = profile.getLengthMeters().doubleValue() * 1000.0;
                    }
                    unitPricePerMeter = profile.getPricePerMeter();
                    unitPricePerBar = profile.getPricePerBar();
                    profileName = profile.getName() + " (" + profile.getReference() + ")";
                }
            } else {
                Optional<AluminiumProfile> ap = aluminiumRepo.findById(request.getProfileId());
                if (ap.isPresent()) {
                    AluminiumProfile profile = ap.get();
                    if (profile.getLengthMeters() != null) {
                        commercialBarLength = profile.getLengthMeters().doubleValue() * 1000.0;
                    }
                    unitPricePerMeter = profile.getPricePerMeter();
                    unitPricePerBar = profile.getPricePerBar();
                    profileName = profile.getName() + " (" + profile.getReference() + ")";
                }
            }
        }

        // Closest superior commercial stock optimization (First Fit Decreasing Bin Packing)
        List<Double> pieceLengths = new ArrayList<>(List.of(fabW, fabW, fabH, fabH));
        pieceLengths.sort(Collections.reverseOrder());

        int barsNeeded = computeBarsNeeded(pieceLengths, commercialBarLength);
        result.setSelectedCommercialBarLengthMm(commercialBarLength);
        result.setCommercialBarsNeeded(barsNeeded);

        double totalCommercialStockMm = barsNeeded * commercialBarLength;
        result.setTotalCommercialStockLengthMm(totalCommercialStockMm);

        double wasteMm = totalCommercialStockMm - totalLengthMm;
        double wasteM = Math.max(0, wasteMm / 1000.0);
        result.setWasteMeters(Math.round(wasteM * 100.0) / 100.0);

        double wastePct = totalCommercialStockMm > 0 ? (wasteMm / totalCommercialStockMm) * 100.0 : 0.0;
        result.setWastePercentage(Math.round(wastePct * 10.0) / 10.0);

        if (profileName != null) {
            result.setProfileInfo(profileName);
        }

        if (unitPricePerBar != null) {
            result.setEstimatedCost(unitPricePerBar.multiply(BigDecimal.valueOf(barsNeeded)).setScale(2, RoundingMode.HALF_UP));
        } else if (unitPricePerMeter != null) {
            result.setEstimatedCost(unitPricePerMeter.multiply(BigDecimal.valueOf(result.getTotalProfileLengthMeters())).setScale(2, RoundingMode.HALF_UP));
        }

        result.setNotes(String.format("Deducción de solape aplicada: -%.1fmm por lado (Total ancho: -%.1fmm, Total alto: -%.1fmm). Barras comerciales requeridas: %d de %.0fmm.",
                solapePerSide, totalWidthDeduction, totalHeightDeduction, barsNeeded, commercialBarLength));

        return result;
    }

    /**
     * First-Fit Decreasing algorithm to determine minimum number of commercial stock bars needed.
     */
    private int computeBarsNeeded(List<Double> pieceLengths, double barCapacity) {
        List<Double> barsRemainingCapacity = new ArrayList<>();

        for (double piece : pieceLengths) {
            if (piece > barCapacity) {
                // If a piece is larger than standard commercial length, it still requires at least 1 bar
                barsRemainingCapacity.add(0.0);
                continue;
            }

            boolean placed = false;
            for (int i = 0; i < barsRemainingCapacity.size(); i++) {
                if (barsRemainingCapacity.get(i) >= piece) {
                    barsRemainingCapacity.set(i, barsRemainingCapacity.get(i) - piece);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                barsRemainingCapacity.add(barCapacity - piece);
            }
        }

        return Math.max(1, barsRemainingCapacity.size());
    }
}
