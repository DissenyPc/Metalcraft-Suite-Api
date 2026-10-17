package com.carpinteria.api.service.calculator;

import com.carpinteria.api.dto.calculator.FixedModuleCalculationRequest;
import com.carpinteria.api.dto.calculator.FixedModuleCalculationResult;
import com.carpinteria.api.dto.calculator.ProfileCutItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service 3: Fixed Modules (Módulos Fijos) Service
 * Handles perimeter series (shared outer frames with dividing transom/mullion profile 'T')
 * and non-perimeter series (top/bottom additions or independent lateral frame deductions with 'H' coupling profiles).
 */
@Service
public class FixedModuleCalculationService {

    public FixedModuleCalculationResult calculate(FixedModuleCalculationRequest request) {
        FixedModuleCalculationResult result = new FixedModuleCalculationResult();

        double totalW = request.getTotalOpeningWidthMm();
        double totalH = request.getTotalOpeningHeightMm();
        String position = request.getModulePosition();
        double fixedDim = request.getFixedDimensionMm();
        String seriesType = request.getSeriesType();
        double frameWidth = request.getFrameProfileWidthMm();
        double transomWidth = request.getTransomProfileWidthMm();
        double couplingWidth = request.getCouplingProfileWidthMm();

        result.setTotalOpeningWidthMm(totalW);
        result.setTotalOpeningHeightMm(totalH);
        result.setModulePosition(position);
        result.setSeriesType(seriesType);

        List<ProfileCutItem> cuts = new ArrayList<>();
        boolean isTopOrBottom = "TOP".equals(position) || "BOTTOM".equals(position);

        if ("PERIMETER".equalsIgnoreCase(seriesType)) {
            // =========================================================================
            // 1. Perimeter Series: Shared outer frame with intermediate transom (perfil T)
            // =========================================================================
            // Outer frame encompasses the whole total dimensions
            cuts.add(new ProfileCutItem("Marco exterior perimetral horizontal", totalW, 2, "45° INGLETE"));
            cuts.add(new ProfileCutItem("Marco exterior perimetral vertical", totalH, 2, "45° INGLETE"));

            if (isTopOrBottom) {
                // Horizontal dividing transom
                double transomLength = Math.max(0, totalW - (2.0 * frameWidth));
                result.setTransomLengthMm(transomLength);
                result.setTransomDescription(String.format("1x Perfil travesaño T intermedio horizontal de %.1f mm (corte testa 90° con mecanizado)", transomLength));
                cuts.add(new ProfileCutItem("Perfil travesaño / parteluz T horizontal", transomLength, 1, "90° RECTO"));

                double mainH = Math.max(0, totalH - fixedDim - (transomWidth / 2.0));
                double fixH = Math.max(0, fixedDim - (transomWidth / 2.0));

                result.setMainWindowWidthMm(totalW);
                result.setMainWindowHeightMm(mainH);
                result.setFixedModuleWidthMm(totalW);
                result.setFixedModuleHeightMm(fixH);
                result.setNotes(String.format("Serie Perimetral: Marco único compartido de %.0fx%.0f mm con travesaño T horizontal de %.1f mm. Luz ventana: %.1f mm, Luz fijo: %.1f mm.",
                        totalW, totalH, transomLength, mainH, fixH));
            } else {
                // Lateral fixed module (vertical dividing mullion/parteluz)
                double transomLength = Math.max(0, totalH - (2.0 * frameWidth));
                result.setTransomLengthMm(transomLength);
                result.setTransomDescription(String.format("1x Perfil parteluz T intermedio vertical de %.1f mm (corte testa 90° con mecanizado)", transomLength));
                cuts.add(new ProfileCutItem("Perfil parteluz / travesaño T vertical", transomLength, 1, "90° RECTO"));

                double mainW = Math.max(0, totalW - fixedDim - (transomWidth / 2.0));
                double fixW = Math.max(0, fixedDim - (transomWidth / 2.0));

                result.setMainWindowWidthMm(mainW);
                result.setMainWindowHeightMm(totalH);
                result.setFixedModuleWidthMm(fixW);
                result.setFixedModuleHeightMm(totalH);
                result.setNotes(String.format("Serie Perimetral: Marco único compartido de %.0fx%.0f mm con parteluz T vertical de %.1f mm. Luz ventana: %.1f mm, Luz fijo: %.1f mm.",
                        totalW, totalH, transomLength, mainW, fixW));
            }

        } else {
            // =========================================================================
            // 2. Non-Perimeter Series: Independent frames joined with H coupling profile
            // =========================================================================
            if (isTopOrBottom) {
                // Top or bottom addition:
                // Fixed frame height = fixedDim
                // Window frame height = totalH - fixedDim - couplingWidth
                double winH = Math.max(0, totalH - fixedDim - couplingWidth);
                double fixH = fixedDim;

                result.setMainWindowWidthMm(totalW);
                result.setMainWindowHeightMm(winH);
                result.setFixedModuleWidthMm(totalW);
                result.setFixedModuleHeightMm(fixH);

                double hProfileLength = totalW;
                result.setCouplingHProfileLengthMm(hProfileLength);
                result.setCouplingProfileDescription(String.format("1x Perfil de unión H horizontal de %.1f mm (deducción junta %.1f mm)", hProfileLength, couplingWidth));

                // Frame cuts for Window
                cuts.add(new ProfileCutItem("Marco ventana operativa horizontal", totalW, 2, "45° INGLETE"));
                cuts.add(new ProfileCutItem("Marco ventana operativa vertical", winH, 2, "45° INGLETE"));

                // Frame cuts for Fixed Module
                cuts.add(new ProfileCutItem("Marco módulo fijo horizontal", totalW, 2, "45° INGLETE"));
                cuts.add(new ProfileCutItem("Marco módulo fijo vertical", fixH, 2, "45° INGLETE"));

                // Coupling profile cut
                cuts.add(new ProfileCutItem("Perfil de unión en H horizontal", hProfileLength, 1, "90° RECTO"));

                result.setNotes(String.format("Serie No Perimetral: 2 marcos independientes acoplados con perfil H horizontal de %.1f mm (descuento junta %.1f mm). Marco ventana: %.0fx%.1f mm, Marco fijo: %.0fx%.1f mm.",
                        hProfileLength, couplingWidth, totalW, winH, totalW, fixH));

            } else {
                // Lateral independent frame:
                // Fixed frame width = fixedDim
                // Window frame width = totalW - fixedDim - couplingWidth
                double winW = Math.max(0, totalW - fixedDim - couplingWidth);
                double fixW = fixedDim;

                result.setMainWindowWidthMm(winW);
                result.setMainWindowHeightMm(totalH);
                result.setFixedModuleWidthMm(fixW);
                result.setFixedModuleHeightMm(totalH);

                double hProfileLength = totalH;
                result.setCouplingHProfileLengthMm(hProfileLength);
                result.setCouplingProfileDescription(String.format("1x Perfil de unión H vertical de %.1f mm (deducción junta lateral %.1f mm)", hProfileLength, couplingWidth));

                // Frame cuts for Window
                cuts.add(new ProfileCutItem("Marco ventana operativa horizontal", winW, 2, "45° INGLETE"));
                cuts.add(new ProfileCutItem("Marco ventana operativa vertical", totalH, 2, "45° INGLETE"));

                // Frame cuts for Lateral Fixed Module
                cuts.add(new ProfileCutItem("Marco módulo fijo lateral horizontal", fixW, 2, "45° INGLETE"));
                cuts.add(new ProfileCutItem("Marco módulo fijo lateral vertical", totalH, 2, "45° INGLETE"));

                // Coupling profile cut
                cuts.add(new ProfileCutItem("Perfil de unión en H vertical", hProfileLength, 1, "90° RECTO"));

                result.setNotes(String.format("Serie No Perimetral: 2 marcos independientes acoplados con perfil H vertical de %.1f mm (descuento junta lateral %.1f mm). Marco ventana: %.1fx%.0f mm, Marco fijo: %.1fx%.0f mm.",
                        hProfileLength, couplingWidth, winW, totalH, fixW, totalH));
            }
        }

        result.setFrameCuts(cuts);
        return result;
    }
}
