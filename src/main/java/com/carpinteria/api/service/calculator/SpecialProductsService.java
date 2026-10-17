package com.carpinteria.api.service.calculator;

import com.carpinteria.api.dto.calculator.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service 5: Special Products Services
 * Handles specialized calculations for:
 * 1. Grilles (Rejas - Hierro / Aluminio) with dynamic bar count (spacing <= 130mm) and fixing types (embedded +100mm per side, screwed with 4 angles).
 * 2. Railings (Barandillas) with handrail types and safety infill spacing (<= 100mm / 130mm per CTE).
 * 3. Mosquito Nets (Mosquiteras) including sliding nets (derived from leaf dimensions), fixed nets, and commercial resale kits (roll-up, lateral pleated, practicable).
 */
@Service
public class SpecialProductsService {

    // =========================================================================
    // 1. Grilles (Rejas de Hierro y Aluminio)
    // =========================================================================
    public GrilleCalculationResult calculateGrille(GrilleCalculationRequest request) {
        GrilleCalculationResult result = new GrilleCalculationResult();

        double openW = request.getOpeningWidthMm();
        double openH = request.getOpeningHeightMm();
        String mat = request.getMaterial();
        String fixType = request.getFixingType();
        double barThick = request.getBarThicknessMm();
        double maxSpacing = request.getMaxSpacingMm(); // <= 130mm
        double frameW = request.getFrameProfileWidthMm();

        result.setMaterial(mat);
        result.setFixingType(fixType);
        result.setOpeningWidthMm(openW);
        result.setOpeningHeightMm(openH);

        double fabW = openW;
        double fabH = openH;
        double addedW = 0.0;
        double addedH = 0.0;
        int fixingAngles = 0;
        String fixDesc;

        if ("EMBEDDED".equalsIgnoreCase(fixType)) {
            // Embedded into wall: adds +100mm per side (= +200mm to width and/or height)
            double addPerSide = request.getEmbeddedAdditionPerSideMm();
            addedW = addPerSide * 2.0;
            addedH = addPerSide * 2.0;
            fabW += addedW;
            fabH += addedH;
            fixDesc = String.format("Garrochas/garras de anclaje para embutir y recibir de obra (+%.0fmm por lado / +%.0fmm total)", addPerSide, addedW);
        } else if ("SCREWED".equalsIgnoreCase(fixType)) {
            // Screwed with mounting brackets: adds 4 fixing angles
            fixingAngles = 4;
            fixDesc = "4 escuadras/pletinas de fijación con tornillos de seguridad inviolables y tacos";
        } else {
            fixDesc = "Fijación frontal o a testa en cerco de hueco mediante tacos químicos / tornillos directos a obra";
        }

        result.setFabricationWidthMm(fabW);
        result.setFabricationHeightMm(fabH);
        result.setEmbeddedAddedWidthMm(addedW);
        result.setEmbeddedAddedHeightMm(addedH);
        result.setFixingAnglesCount(fixingAngles);
        result.setFixingDescription(fixDesc);

        // Effective inner dimensions between perimeter frame profiles
        double innerW = Math.max(0, fabW - (2.0 * frameW));
        double innerH = Math.max(0, fabH - (2.0 * frameW));
        result.setInnerWidthMm(innerW);
        result.setInnerHeightMm(innerH);

        // Dynamic vertical bar count: guaranteed spacing <= maxSpacing (130mm)
        // spaces * s + (spaces - 1) * barThick = innerW  => spaces * (s + barThick) - barThick = innerW
        int spaces = (int) Math.ceil((innerW - barThick) / (maxSpacing + barThick));
        if (spaces < 1) spaces = 1;
        int barCount = Math.max(0, spaces - 1);
        double exactSpacing = spaces > 0 ? (innerW - (barCount * barThick)) / spaces : 0.0;

        result.setBarCount(barCount);
        result.setSpaceCount(spaces);
        result.setExactSpacingMm(Math.round(exactSpacing * 10.0) / 10.0);

        double barLength = innerH;
        result.setSingleBarLengthMm(barLength);

        double totalBarsM = (barCount * barLength) / 1000.0;
        result.setTotalBarsLengthMeters(Math.round(totalBarsM * 100.0) / 100.0);

        // Frame perimeter meters
        double framePerimeterM = ((2.0 * fabW) + (2.0 * fabH)) / 1000.0;
        result.setTotalFrameProfileMeters(Math.round(framePerimeterM * 100.0) / 100.0);

        // Reinforcing transom (parteluz/travesaño de refuerzo si altura > 1500mm o solicitado)
        boolean hasTransom = (request.getIncludeReinforcingTransom() != null)
                ? request.getIncludeReinforcingTransom()
                : (openH > 1500.0);

        int transoms = hasTransom ? (openH > 2200.0 ? 2 : 1) : 0;
        result.setReinforcingTransomCount(transoms);
        result.setReinforcingTransomLengthMm(innerW);

        // Weight estimation (Iron: ~1.58 kg/m for 16mm round bar, ~1.35 kg/m for tube frame)
        if ("IRON".equalsIgnoreCase(mat)) {
            double barWeightKg = totalBarsM * 1.58;
            double frameWeightKg = framePerimeterM * 1.35;
            double transomWeightKg = (transoms * innerW / 1000.0) * 1.35;
            result.setEstimatedWeightKg(Math.round((barWeightKg + frameWeightKg + transomWeightKg) * 10.0) / 10.0);
        }

        // Cut list
        List<ProfileCutItem> cuts = new ArrayList<>();
        cuts.add(new ProfileCutItem("Tubo cerco perimetral horizontal", fabW, 2, "45° INGLETE"));
        cuts.add(new ProfileCutItem("Tubo cerco perimetral vertical", fabH, 2, "45° INGLETE"));
        if (barCount > 0) {
            cuts.add(new ProfileCutItem(String.format("Barrote vertical (Ø/sección %.0fmm)", barThick), barLength, barCount, "90° RECTO"));
        }
        if (transoms > 0) {
            cuts.add(new ProfileCutItem("Pletina / travesaño horizontal de refuerzo perforado", innerW, transoms, "90° RECTO"));
        }
        result.setCutList(cuts);

        result.setNotes(String.format("Reja %s (%s): %d barrotes con separación exacta de %.1fmm (máx. %.0fmm). Fijación: %s.",
                mat, fixType, barCount, result.getExactSpacingMm(), maxSpacing, fixDesc));

        return result;
    }

    // =========================================================================
    // 2. Railings (Barandillas)
    // =========================================================================
    public RailingCalculationResult calculateRailing(RailingCalculationRequest request) {
        RailingCalculationResult result = new RailingCalculationResult();

        double len = request.getLengthMm();
        double h = request.getHeightMm();
        String handrailType = request.getHandrailType();
        double barThick = request.getInfillBarThicknessMm();
        double maxBarSpacing = request.getMaxBarSpacingMm(); // <= 100mm per CTE
        double maxPostSpacing = request.getMaxPostSpacingMm(); // e.g. 1000mm
        double postSize = request.getPostProfileSizeMm();
        String fixType = request.getFixingType();

        result.setTotalLengthMm(len);
        result.setTotalHeightMm(h);
        result.setHandrailType(handrailType);

        // Handrail description
        String handrailDesc;
        double handrailProfileHeight = 40.0;
        switch (handrailType) {
            case "ROUND_TUBE_50":
                handrailDesc = "Tubo redondo Ø50mm ergonómico con tapones terminales";
                handrailProfileHeight = 50.0;
                break;
            case "MOLDED_60X20":
                handrailDesc = "Pasamanos moldurado 60x20mm estilo forja tradicional";
                handrailProfileHeight = 20.0;
                break;
            case "RECTANGULAR_60X30":
                handrailDesc = "Tubo rectangular 60x30mm diseño moderno";
                handrailProfileHeight = 30.0;
                break;
            default:
                handrailDesc = "Pasamanos estándar perfil tubular";
                handrailProfileHeight = 40.0;
                break;
        }
        result.setHandrailDescription(handrailDesc);
        result.setHandrailLengthMeters(Math.round((len / 1000.0) * 100.0) / 100.0);

        // Posts calculation (pilares)
        int postSpaces = (int) Math.ceil(len / maxPostSpacing);
        if (postSpaces < 1) postSpaces = 1;
        int postCount = postSpaces + 1; // start, intermediate, end
        result.setPostCount(postCount);

        double postH = Math.max(0, h - handrailProfileHeight);
        result.setPostLengthMm(postH);
        result.setTotalPostsLengthMeters(Math.round((postCount * postH / 1000.0) * 100.0) / 100.0);

        // Infill vertical bars calculation (spacing <= 100mm between bars according to CTE)
        double netInfillLength = Math.max(0, len - (postCount * postSize));
        int barSpaces = (int) Math.ceil((netInfillLength - barThick) / (maxBarSpacing + barThick));
        if (barSpaces < 1) barSpaces = 1;
        int infillCount = Math.max(0, barSpaces - 1);
        double exactBarSpacing = barSpaces > 0 ? (netInfillLength - (infillCount * barThick)) / barSpaces : 0.0;

        result.setInfillBarCount(infillCount);
        result.setExactInfillSpacingMm(Math.round(exactBarSpacing * 10.0) / 10.0);

        double bottomGroundClearance = 80.0; // Standard 80mm clearance to ground/deck
        double infillBarH = Math.max(0, postH - bottomGroundClearance - 20.0);
        result.setInfillBarLengthMm(infillBarH);
        result.setTotalInfillBarsLengthMeters(Math.round((infillCount * infillBarH / 1000.0) * 100.0) / 100.0);

        // Bottom rail (zócalo o pletina base)
        result.setBottomRailLengthMeters(Math.round((len / 1000.0) * 100.0) / 100.0);

        // Fixings
        int basePlates = 0;
        String fixDesc;
        if ("SURFACE_PLATE".equalsIgnoreCase(fixType)) {
            basePlates = postCount;
            fixDesc = String.format("%d placas de anclaje base (100x100mm) con 4 taladros avellanados para taco químico o expansivo", basePlates);
        } else if ("EMBEDDED".equalsIgnoreCase(fixType)) {
            fixDesc = String.format("%d postes con espiga embutida (+150mm) recibida con mortero de resina epoxi", postCount);
        } else {
            basePlates = postCount;
            fixDesc = String.format("%d pletinas de anclaje lateral a zanco/forjado con tornillería pasante", basePlates);
        }
        result.setBasePlatesCount(basePlates);
        result.setFixingDescription(fixDesc);

        // Cut list
        List<ProfileCutItem> cuts = new ArrayList<>();
        cuts.add(new ProfileCutItem("Pasamanos superior: " + handrailDesc, len, 1, "90° RECTO"));
        cuts.add(new ProfileCutItem("Perfil zócalo / pletina inferior", len, 1, "90° RECTO"));
        cuts.add(new ProfileCutItem(String.format("Poste estructural (tubo %.0fmm)", postSize), postH, postCount, "90° RECTO"));
        if (infillCount > 0) {
            cuts.add(new ProfileCutItem(String.format("Barrote vertical de relleno (%.0fmm)", barThick), infillBarH, infillCount, "90° RECTO"));
        }
        result.setCutList(cuts);

        result.setNotes(String.format("Barandilla L=%.0fmm H=%.0fmm: %s. %d postes cada <=%.0fmm. %d barrotes con separación de %.1fmm (CTE <=%.0fmm). Fijación: %s.",
                len, h, handrailDesc, postCount, maxPostSpacing, infillCount, result.getExactInfillSpacingMm(), maxBarSpacing, fixDesc));

        return result;
    }

    // =========================================================================
    // 3. Mosquito Nets (Mosquiteras)
    // =========================================================================
    public MosquitoNetCalculationResult calculateMosquitoNet(MosquitoNetCalculationRequest request) {
        MosquitoNetCalculationResult result = new MosquitoNetCalculationResult();

        String type = request.getType();
        double openW = request.getOpeningWidthMm();
        double openH = request.getOpeningHeightMm();
        int leaves = request.getWindowLeafCount();
        double overlap = request.getSlidingOverlapMm();
        double runnerDeduction = request.getRunnerDeductionMm();
        String color = request.getProfileColor();

        result.setType(type);
        result.setOpeningWidthMm(openW);
        result.setOpeningHeightMm(openH);
        result.setProfileColor(color);

        List<ProfileCutItem> cuts = new ArrayList<>();
        List<String> accessories = new ArrayList<>();

        double netW;
        double netH;
        String desc;
        boolean hasTransom = false;
        double transomLen = 0.0;

        switch (type) {
            case "SLIDING":
                // Sliding net: derived from window leaf dimensions
                // Width = (windowOpeningWidth + overlap) / leaves
                netW = Math.round(((openW + overlap) / leaves) * 10.0) / 10.0;
                // Height = windowOpeningHeight - runnerDeduction (tracks clearance, e.g. -10mm)
                netH = Math.max(0, openH - runnerDeduction);
                desc = String.format("Mosquitera Corredera para 1 hoja (de ventana de %d hojas)", leaves);

                cuts.add(new ProfileCutItem("Perfil corredera horizontal", netW, 2, "45° INGLETE"));
                cuts.add(new ProfileCutItem("Perfil corredera vertical", netH, 2, "45° INGLETE"));

                // Intermediate transom if height > 1400mm to prevent profile deflection from mesh tension
                if (netH > 1400.0) {
                    hasTransom = true;
                    transomLen = Math.max(0, netW - 60.0);
                    cuts.add(new ProfileCutItem("Perfil parteluz intermedio mosquitera", transomLen, 1, "90° RECTO"));
                    accessories.add("2x Soportes metálicos para parteluz central");
                }

                accessories.add("4x Escuadras de unión de nylon");
                accessories.add("2x Ruedas/rodamientos inferiores regulables de nylon");
                accessories.add("2x Guías superiores de deslizamiento");
                accessories.add(String.format("Felpa cortavientos perimetral (%.2f metros lineales)", (2 * netW + 2 * netH) / 1000.0));
                accessories.add("Goma espiga retenedora de fibra de vidrio");
                accessories.add("Tela/malla de fibra de vidrio gris ignífuga y antidesgarro");
                break;

            case "FIXED":
                // Fixed net: matches opening or daylight frame
                netW = openW;
                netH = openH;
                desc = "Mosquitera Fija perimetral con clips de anclaje";

                cuts.add(new ProfileCutItem("Perfil marco fijo horizontal", netW, 2, "45° INGLETE"));
                cuts.add(new ProfileCutItem("Perfil marco fijo vertical", netH, 2, "45° INGLETE"));

                if (netH > 1500.0) {
                    hasTransom = true;
                    transomLen = Math.max(0, netW - 50.0);
                    cuts.add(new ProfileCutItem("Perfil travesaño estabilizador", transomLen, 1, "90° RECTO"));
                }

                accessories.add("4x Escuadras de esquina con inglete");
                accessories.add("Goma retenedora perimetral");
                accessories.add("Tela de fibra de vidrio de alta transparencia");
                accessories.add("4-6x Grapas giratorias o clips Z de fijación a carpintería");
                break;

            case "ROLL_UP":
                // Commercial roll-up cassette kit
                netW = openW;
                netH = openH;
                desc = "Mosquitera Enrollable vertical (Kit comercial con cajón 43mm y muelle tensor)";

                cuts.add(new ProfileCutItem("Cajón superior con eje y muelle", netW, 1, "90° RECTO"));
                cuts.add(new ProfileCutItem("Perfil terminal inferior con tiradores", netW, 1, "90° RECTO"));
                cuts.add(new ProfileCutItem("Guías laterales con felpa antiviento", Math.max(0, openH - 45.0), 2, "90° RECTO"));

                accessories.add("1x Cajón 43mm de aluminio lacado con testeros y muelle tensor recuperador");
                accessories.add("2x Guías laterales de 35x20mm con felpa antiviento y freno clic-clac");
                accessories.add("1x Terminal inferior con goma de estanqueidad y pestillos de cierre");
                accessories.add("2x Tiradores de accionamiento y cordón central");
                break;

            case "LATERAL_PLISADA":
                netW = openW;
                netH = openH;
                desc = "Mosquitera Plisada lateral para balconeras y puertas (Guía inferior accesible 3mm)";

                cuts.add(new ProfileCutItem("Perfil cajón lateral vertical", openH, 1, "90° RECTO"));
                cuts.add(new ProfileCutItem("Perfil compensador lateral de cierre", openH, 1, "90° RECTO"));
                cuts.add(new ProfileCutItem("Perfil superior de guiado", openW, 1, "90° RECTO"));
                cuts.add(new ProfileCutItem("Pletina adhesiva suelo ultra-baja (3mm)", openW, 1, "90° RECTO"));

                accessories.add("1x Malla plisada de poliamida negra plisado 20mm con cadenas oruga");
                accessories.add("1x Perfil magnético perimetral de cierre");
                accessories.add("Sistema de hilos de kevlar tensores y roldanas de latón");
                break;

            case "PRACTICABLE":
            default:
                netW = openW;
                netH = openH;
                desc = "Mosquitera Abatible practicable con bisagras de muelle y cierre magnético";

                cuts.add(new ProfileCutItem("Marco exterior perimetral horizontal", netW, 2, "45° INGLETE"));
                cuts.add(new ProfileCutItem("Marco exterior perimetral vertical", netH, 2, "45° INGLETE"));
                cuts.add(new ProfileCutItem("Hoja abatible horizontal", Math.max(0, netW - 40.0), 2, "45° INGLETE"));
                cuts.add(new ProfileCutItem("Hoja abatible vertical", Math.max(0, netH - 40.0), 2, "45° INGLETE"));
                cuts.add(new ProfileCutItem("Zócalo o parteluz central de protección", Math.max(0, netW - 100.0), 1, "90° RECTO"));

                accessories.add("2-3x Bisagras con muelle retorno automático");
                accessories.add("1x Imán perimetral o cerradero magnético");
                accessories.add("1x Manilla o tirador extraplano");
                accessories.add("Zócalo inferior de aluminio protector 100mm");
                break;
        }

        result.setDescription(desc);
        result.setNetWidthMm(netW);
        result.setNetHeightMm(netH);

        double meshM2 = (netW * netH) / 1_000_000.0;
        result.setMeshAreaM2(Math.round(meshM2 * 100.0) / 100.0);

        result.setIntermediateTransomIncluded(hasTransom);
        result.setIntermediateTransomLengthMm(transomLen);
        result.setProfileCuts(cuts);
        result.setAccessories(accessories);

        result.setNotes(String.format("%s (Color: %s): Dimensiones netas %.1fx%.1f mm (Superficie malla: %.2f m²).",
                desc, color, netW, netH, result.getMeshAreaM2()));

        return result;
    }
}
