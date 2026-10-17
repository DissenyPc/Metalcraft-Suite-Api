package com.carpinteria.api.service.calculator;

import com.carpinteria.api.dto.calculator.GlassPaneItem;
import com.carpinteria.api.dto.calculator.GlazingCalculationRequest;
import com.carpinteria.api.dto.calculator.GlazingCalculationResult;
import com.carpinteria.api.dto.calculator.ProfileCutItem;
import com.carpinteria.api.model.FillingMaterial;
import com.carpinteria.api.repository.FillingMaterialRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service 4: Lights / Vanos (Glazing & Filling) Service
 * Calculates exact glass / filling panel dimensions, divisions based on horizontal/vertical split counts,
 * glass clearances (calzos de acristalamiento), rubber gaskets, and glazing bead (junquillos) cut lists.
 */
@Service
public class GlazingCalculationService {

    private final FillingMaterialRepository fillingRepo;

    public GlazingCalculationService(FillingMaterialRepository fillingRepo) {
        this.fillingRepo = fillingRepo;
    }

    public GlazingCalculationResult calculate(GlazingCalculationRequest request) {
        GlazingCalculationResult result = new GlazingCalculationResult();

        double frameW = request.getFrameWidthMm();
        double frameH = request.getFrameHeightMm();
        int hSplits = request.getHorizontalSplits(); // e.g. 2 leaves
        int vSplits = request.getVerticalSplits();   // e.g. 1 row
        String windowType = request.getWindowType();
        double frameProfileW = request.getFrameProfileWidthMm();
        double sashProfileW = request.getSashProfileWidthMm();
        double overlap = request.getSlidingOverlapMm();
        double transomW = request.getTransomWidthMm();
        double glassClearance = request.getGlassClearanceMm(); // e.g. 8mm total (4mm per side)

        result.setFrameWidthMm(frameW);
        result.setFrameHeightMm(frameH);
        result.setHorizontalSplits(hSplits);
        result.setVerticalSplits(vSplits);

        // Frame inner daylight (luz interior de marco)
        double innerFrameW = Math.max(0, frameW - (2.0 * frameProfileW));
        double innerFrameH = Math.max(0, frameH - (2.0 * frameProfileW));

        List<GlassPaneItem> panes = new ArrayList<>();
        int paneIndex = 1;

        if ("SLIDING".equalsIgnoreCase(windowType)) {
            // =========================================================================
            // Sliding Window: Leaves overlap centrally
            // =========================================================================
            // Leaf width with central overlap between leaves
            double leafW = (innerFrameW + ((hSplits - 1) * overlap)) / hSplits;
            double leafH = innerFrameH;

            // Glass cut size within sash profiles with glass clearance deduction
            double glassW = Math.max(0, leafW - (2.0 * sashProfileW) - glassClearance);
            double glassH = Math.max(0, (leafH / vSplits) - (2.0 * sashProfileW) - glassClearance);

            for (int col = 1; col <= hSplits; col++) {
                for (int row = 1; row <= vSplits; row++) {
                    String label = String.format("Vidrio Hoja %d (Fila %d)", col, row);
                    GlassPaneItem item = createGlassPaneItem(paneIndex++, label, glassW, glassH);
                    panes.add(item);
                }
            }

        } else if ("CASEMENT".equalsIgnoreCase(windowType)) {
            // =========================================================================
            // Casement / Practicable Window: Leaves meet at central inverse profile / invérsula
            // =========================================================================
            double centralClearance = (hSplits > 1) ? 6.0 : 0.0; // Central rebate gap
            double leafW = (innerFrameW - ((hSplits - 1) * centralClearance)) / hSplits;
            double leafH = innerFrameH;

            double glassW = Math.max(0, leafW - (2.0 * sashProfileW) - glassClearance);
            double glassH = Math.max(0, (leafH / vSplits) - (2.0 * sashProfileW) - glassClearance);

            for (int col = 1; col <= hSplits; col++) {
                for (int row = 1; row <= vSplits; row++) {
                    String label = String.format("Vidrio Hoja Practicable %d (Fila %d)", col, row);
                    GlassPaneItem item = createGlassPaneItem(paneIndex++, label, glassW, glassH);
                    panes.add(item);
                }
            }

        } else {
            // =========================================================================
            // Fixed Glazing Grid: Direct glass into frame with dividing transoms/mullions
            // =========================================================================
            double daylightW = (innerFrameW - ((hSplits - 1) * transomW)) / hSplits;
            double daylightH = (innerFrameH - ((vSplits - 1) * transomW)) / vSplits;

            // Glass sits in rebate (+ rebate overlap - glass clearance)
            double glassW = Math.max(0, daylightW + 20.0 - glassClearance);
            double glassH = Math.max(0, daylightH + 20.0 - glassClearance);

            for (int row = 1; row <= vSplits; row++) {
                for (int col = 1; col <= hSplits; col++) {
                    String label = String.format("Vidrio Fijo Vano [%d,%d]", row, col);
                    GlassPaneItem item = createGlassPaneItem(paneIndex++, label, glassW, glassH);
                    panes.add(item);
                }
            }
        }

        result.setPanes(panes);
        result.setTotalPanesCount(panes.size());

        // Calculate totals
        double totalAreaM2 = 0.0;
        double totalGasketMeters = 0.0;
        double totalBeadMeters = 0.0;

        for (GlassPaneItem p : panes) {
            totalAreaM2 += p.getAreaM2();
            totalGasketMeters += p.getPerimeterMeters();
            totalBeadMeters += p.getPerimeterMeters();
        }

        result.setTotalGlassAreaM2(Math.round(totalAreaM2 * 100.0) / 100.0);
        result.setTotalGasketPerimeterMeters(Math.round(totalGasketMeters * 100.0) / 100.0);
        result.setTotalGlazingBeadLengthMeters(Math.round(totalBeadMeters * 100.0) / 100.0);

        // Optional database filling material pricing
        if (request.getFillingMaterialId() != null) {
            Optional<FillingMaterial> fm = fillingRepo.findById(request.getFillingMaterialId());
            if (fm.isPresent()) {
                FillingMaterial material = fm.get();
                result.setFillingMaterialName(material.getName() + " (" + material.getReference() + ")");
                if (material.getPricePerSquareMeter() != null) {
                    BigDecimal cost = material.getPricePerSquareMeter()
                            .multiply(BigDecimal.valueOf(result.getTotalGlassAreaM2()))
                            .setScale(2, RoundingMode.HALF_UP);
                    result.setEstimatedGlassCost(cost);
                }
            }
        }

        result.setNotes(String.format("Cálculo de acristalamiento (%s): %d vanos/hojas calculados. Superficie total: %.2f m². Junta y junquillo: %.2f metros lineales.",
                windowType, panes.size(), result.getTotalGlassAreaM2(), result.getTotalGlazingBeadLengthMeters()));

        return result;
    }

    private GlassPaneItem createGlassPaneItem(int index, String label, double glassW, double glassH) {
        GlassPaneItem item = new GlassPaneItem(index, label, Math.round(glassW * 10.0) / 10.0, Math.round(glassH * 10.0) / 10.0);

        // Generate glazing beads (junquillos) for this glass pane
        List<ProfileCutItem> beads = new ArrayList<>();
        beads.add(new ProfileCutItem("Junquillo horizontal superior", item.getCutWidthMm(), 1, "45° INGLETE"));
        beads.add(new ProfileCutItem("Junquillo horizontal inferior", item.getCutWidthMm(), 1, "45° INGLETE"));
        beads.add(new ProfileCutItem("Junquillo vertical izquierdo", item.getCutHeightMm(), 1, "45° INGLETE"));
        beads.add(new ProfileCutItem("Junquillo vertical derecho", item.getCutHeightMm(), 1, "45° INGLETE"));
        item.setGlazingBeads(beads);

        return item;
    }
}
