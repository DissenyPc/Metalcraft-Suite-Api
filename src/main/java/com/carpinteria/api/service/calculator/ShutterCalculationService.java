package com.carpinteria.api.service.calculator;

import com.carpinteria.api.dto.calculator.ShutterCalculationRequest;
import com.carpinteria.api.dto.calculator.ShutterCalculationResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service 2: Built-in Shutter (Persiana Compacto / Monoblock) Service
 * Applies width deductions (-30mm for built-in winder / recogedor empotrado)
 * and height adjustments based on opening measurements (subtracting compact box from window frame height).
 */
@Service
public class ShutterCalculationService {

    public ShutterCalculationResult calculate(ShutterCalculationRequest request) {
        ShutterCalculationResult result = new ShutterCalculationResult();

        double openW = request.getOpeningWidthMm();
        double openH = request.getOpeningHeightMm();

        result.setTotalOpeningWidthMm(openW);
        result.setTotalOpeningHeightMm(openH);

        // 1. Determine Box Height (Cajón Compacto)
        double boxH;
        String boxModel;
        if (request.getBoxHeightMm() != null && request.getBoxHeightMm() > 0) {
            boxH = request.getBoxHeightMm();
            boxModel = String.format("Cajón compacto monoblock %.0f mm", boxH);
        } else {
            if (openH <= 1500.0) {
                boxH = 155.0;
                boxModel = "Cajón compacto 155x155 mm (Estándar ventanas)";
            } else if (openH <= 2400.0) {
                boxH = 185.0;
                boxModel = "Cajón compacto 185x185 mm (Estándar balconeras)";
            } else {
                boxH = 200.0;
                boxModel = "Cajón compacto 200x200 mm (Gran capacidad)";
            }
        }
        result.setBoxHeightMm(boxH);
        result.setBoxModel(boxModel);

        // 2. Height Adjustment: Window fabrication height is opening minus the compact box
        double winFabH = Math.max(0, openH - boxH);
        result.setWindowFabricationHeightMm(winFabH);

        // Shutter guides run along the window vertical sides
        double guideLength = winFabH;
        result.setGuideLengthMm(guideLength);
        result.setGuideCount(2);

        // Curtain total height includes drop to window sill plus coiling curve inside box (~70% of box height)
        double curtainH = winFabH + (boxH * 0.70);
        result.setShutterCurtainHeightMm(Math.round(curtainH * 10.0) / 10.0);

        // 3. Width Deductions: -30mm for built-in winder (recogedor empotrado en guía)
        String winderType = request.getWinderType();
        result.setWinderType(winderType);

        double winderDeduction = 0.0;
        if ("BUILT_IN".equalsIgnoreCase(winderType)) {
            winderDeduction = request.getBuiltInWinderDeductionMm(); // Standard 30mm
        }
        result.setWinderDeductionAppliedMm(winderDeduction);

        double guideDeduction = request.getGuideDeductionMm(); // Default 40mm (20mm per guide channel)
        double slatCutW = Math.max(0, openW - guideDeduction - winderDeduction);
        result.setShutterSlatCutWidthMm(Math.round(slatCutW * 10.0) / 10.0);

        // 4. Slat Count and Axle
        double slatCoverH = request.getSlatCoverHeightMm(); // e.g. 39mm or 43mm
        int slatCount = (int) Math.ceil(curtainH / slatCoverH);
        result.setSlatCount(slatCount);
        result.setSlatModel(request.getSlatType() + String.format(" (cobertura %.0fmm)", slatCoverH));

        // Octagonal axle length (eje octogonal de 40mm o 60mm)
        double axleLength = Math.max(0, openW - 40.0);
        result.setAxleLengthMm(Math.round(axleLength * 10.0) / 10.0);

        // Total Shutter Area (m²)
        double totalAreaM2 = (openW * openH) / 1_000_000.0;
        result.setTotalShutterAreaM2(Math.round(totalAreaM2 * 100.0) / 100.0);

        // 5. Component breakdown
        List<String> components = new ArrayList<>();
        components.add(String.format("1x %s acabado con testeros y aislamiento acústico/térmico", boxModel));
        components.add(String.format("2x Guías laterales de persiana de %.1f mm con felpa antiviento", guideLength));
        components.add(String.format("1x Paño de persiana compuesto por %d lamas %s cortadas a %.1f mm", slatCount, request.getSlatType(), slatCutW));
        components.add(String.format("1x Eje octogonal galvanizado de 40mm longitud %.1f mm", axleLength));
        components.add("1x Contera de nylon con espiga metálica y rodamiento");
        components.add("1x Disco/Polea de PVC para cinta de 14mm");

        if ("BUILT_IN".equalsIgnoreCase(winderType)) {
            components.add("1x Recogedor empotrado en guía con placa embellecedora y pasacintas con cepillo (-30mm deducidos)");
        } else if ("MOTORIZED".equalsIgnoreCase(winderType)) {
            components.add("1x Motor tubular electromecánico con finales de carrera y soporte");
        } else {
            components.add("1x Recogedor abatible de superficie con caja y cinta");
        }
        components.add("2x Tirantes de seguridad / flejes de sujeción al eje");
        components.add("2x Topes cónicos para lama terminal");
        result.setComponentsIncluded(components);

        result.setNotes(String.format("Cálculo compacto: Altura ventana fabricada = %.1fmm (descontados %.0fmm cajón). Ancho corte lamas = %.1fmm (descontados %.0fmm guías y %.0fmm recogedor empotrado).",
                winFabH, boxH, slatCutW, guideDeduction, winderDeduction));

        return result;
    }
}
