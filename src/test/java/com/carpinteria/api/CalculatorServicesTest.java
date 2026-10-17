package com.carpinteria.api;

import com.carpinteria.api.dto.calculator.*;
import com.carpinteria.api.repository.AluminiumProfileRepository;
import com.carpinteria.api.repository.FillingMaterialRepository;
import com.carpinteria.api.repository.IronProfileRepository;
import com.carpinteria.api.service.calculator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalculatorServicesTest {

    @Mock
    private AluminiumProfileRepository aluminiumRepo;

    @Mock
    private IronProfileRepository ironRepo;

    @Mock
    private FillingMaterialRepository fillingRepo;

    private StructureCalculationService structureService;
    private ShutterCalculationService shutterService;
    private FixedModuleCalculationService fixedModuleService;
    private GlazingCalculationService glazingService;
    private SpecialProductsService specialProductsService;

    @BeforeEach
    void setUp() {
        structureService = new StructureCalculationService(aluminiumRepo, ironRepo);
        shutterService = new ShutterCalculationService();
        fixedModuleService = new FixedModuleCalculationService();
        glazingService = new GlazingCalculationService(fillingRepo);
        specialProductsService = new SpecialProductsService();
    }

    // -------------------------------------------------------------------------
    // 1. Structure Calculation Service Tests
    // -------------------------------------------------------------------------
    @Test
    void testStructureCalculationDeductionsAndStock() {
        // Opening: 1200 x 1400 mm
        StructureCalculationRequest request = new StructureCalculationRequest(1200.0, 1400.0);
        request.setSolapePerSideMm(2.5); // -2.5mm per side -> -5.0mm total width & height
        request.setCommercialBarLengthMm(6000.0);

        StructureCalculationResult result = structureService.calculate(request);

        assertNotNull(result);
        assertEquals(5.0, result.getTotalSolapeWidthDeductionMm());
        assertEquals(5.0, result.getTotalSolapeHeightDeductionMm());
        assertEquals(1195.0, result.getFabricationWidthMm());
        assertEquals(1395.0, result.getFabricationHeightMm());

        // Total perimeter cuts = 2 * 1195 + 2 * 1395 = 5180mm
        assertEquals(5180.0, result.getTotalProfileLengthMm());
        assertEquals(5.18, result.getTotalProfileLengthMeters());

        // Total 5180mm fits in 1 commercial bar of 6000mm!
        assertEquals(1, result.getCommercialBarsNeeded());
        assertEquals(6000.0, result.getSelectedCommercialBarLengthMm());
        assertEquals(0.82, result.getWasteMeters()); // (6000 - 5180) / 1000 = 0.82m
    }

    // -------------------------------------------------------------------------
    // 2. Built-in Shutter (Persiana Compacto) Service Tests
    // -------------------------------------------------------------------------
    @Test
    void testShutterCalculationWithBuiltInWinderDeduction() {
        // Opening: 1200 x 1400 mm with Built-in winder
        ShutterCalculationRequest request = new ShutterCalculationRequest(1200.0, 1400.0);
        request.setBoxHeightMm(155.0);
        request.setWinderType("BUILT_IN");
        request.setBuiltInWinderDeductionMm(30.0);
        request.setGuideDeductionMm(40.0);

        ShutterCalculationResult result = shutterService.calculate(request);

        assertNotNull(result);
        // Window fabrication height = 1400 - 155 = 1245mm
        assertEquals(1245.0, result.getWindowFabricationHeightMm());
        assertEquals(155.0, result.getBoxHeightMm());

        // Slat cut width = 1200 - 40 (guides) - 30 (built-in winder deduction) = 1130mm
        assertEquals(30.0, result.getWinderDeductionAppliedMm());
        assertEquals(1130.0, result.getShutterSlatCutWidthMm());

        // Guide height = window height = 1245mm
        assertEquals(1245.0, result.getGuideLengthMm());
        assertTrue(result.getSlatCount() > 0);
        assertTrue(result.getComponentsIncluded().size() >= 5);
    }

    // -------------------------------------------------------------------------
    // 3. Fixed Modules Service Tests
    // -------------------------------------------------------------------------
    @Test
    void testFixedModulePerimeterSeries() {
        // Total opening: 1200 x 2000 mm, Top fixed module of 500mm, Perimeter Series
        FixedModuleCalculationRequest request = new FixedModuleCalculationRequest(
                1200.0, 2000.0, "TOP", 500.0, "PERIMETER"
        );
        request.setFrameProfileWidthMm(45.0);
        request.setTransomProfileWidthMm(40.0);

        FixedModuleCalculationResult result = fixedModuleService.calculate(request);

        assertNotNull(result);
        assertEquals("PERIMETER", result.getSeriesType());
        // Shared outer frame: total width 1200, total height 2000
        // Transom cut length = 1200 - (2 * 45) = 1110mm
        assertEquals(1110.0, result.getTransomLengthMm());
        assertNotNull(result.getTransomDescription());
        assertEquals(3, result.getFrameCuts().size()); // Horizontales (cant: 2), Verticales (cant: 2), Travesaño (cant: 1)
        int totalPieces = result.getFrameCuts().stream().mapToInt(ProfileCutItem::getQuantity).sum();
        assertEquals(5, totalPieces);
    }

    @Test
    void testFixedModuleNonPerimeterSeries() {
        // Total opening: 1200 x 2000 mm, Top fixed module of 500mm, Non-perimeter (independent frames + H profile)
        FixedModuleCalculationRequest request = new FixedModuleCalculationRequest(
                1200.0, 2000.0, "TOP", 500.0, "NON_PERIMETER"
        );
        request.setCouplingProfileWidthMm(6.0);

        FixedModuleCalculationResult result = fixedModuleService.calculate(request);

        assertNotNull(result);
        assertEquals("NON_PERIMETER", result.getSeriesType());
        // Fixed frame: height 500mm
        // Window frame: height = 2000 - 500 - 6 (H joint) = 1494mm
        assertEquals(500.0, result.getFixedModuleHeightMm());
        assertEquals(1494.0, result.getMainWindowHeightMm());
        assertEquals(1200.0, result.getCouplingHProfileLengthMm());
        assertNotNull(result.getCouplingProfileDescription());
    }

    // -------------------------------------------------------------------------
    // 4. Glazing / Vanos Service Tests
    // -------------------------------------------------------------------------
    @Test
    void testGlazingCalculationSlidingWindow() {
        // 2-leaf sliding window: Frame 1200 x 1200 mm
        GlazingCalculationRequest request = new GlazingCalculationRequest(1200.0, 1200.0, 2, 1);
        request.setWindowType("SLIDING");
        request.setFrameProfileWidthMm(45.0);
        request.setSashProfileWidthMm(50.0);
        request.setSlidingOverlapMm(40.0);
        request.setGlassClearanceMm(8.0);

        GlazingCalculationResult result = glazingService.calculate(request);

        assertNotNull(result);
        assertEquals(2, result.getTotalPanesCount());
        assertEquals(2, result.getPanes().size());

        GlassPaneItem pane1 = result.getPanes().get(0);
        assertTrue(pane1.getCutWidthMm() > 0);
        assertTrue(pane1.getCutHeightMm() > 0);
        assertTrue(pane1.getAreaM2() > 0);
        assertEquals(4, pane1.getGlazingBeads().size()); // 4 glazing beads per glass
        assertTrue(result.getTotalGlassAreaM2() > 0);
    }

    // -------------------------------------------------------------------------
    // 5. Special Products Services Tests
    // -------------------------------------------------------------------------
    @Test
    void testGrilleCalculationScrewedAndEmbedded() {
        // Screwed grille: 1000 x 1200 mm
        GrilleCalculationRequest screwedReq = new GrilleCalculationRequest(1000.0, 1200.0, "IRON", "SCREWED");
        screwedReq.setMaxSpacingMm(130.0);
        screwedReq.setBarThicknessMm(16.0);

        GrilleCalculationResult screwedResult = specialProductsService.calculateGrille(screwedReq);

        assertNotNull(screwedResult);
        assertEquals(4, screwedResult.getFixingAnglesCount());
        assertTrue(screwedResult.getExactSpacingMm() <= 130.0);
        assertTrue(screwedResult.getBarCount() > 0);

        // Embedded grille: +100mm per side (+200mm total width & height)
        GrilleCalculationRequest embeddedReq = new GrilleCalculationRequest(1000.0, 1200.0, "IRON", "EMBEDDED");
        embeddedReq.setEmbeddedAdditionPerSideMm(100.0);

        GrilleCalculationResult embeddedResult = specialProductsService.calculateGrille(embeddedReq);

        assertEquals(1200.0, embeddedResult.getFabricationWidthMm());  // 1000 + 200
        assertEquals(1400.0, embeddedResult.getFabricationHeightMm()); // 1200 + 200
        assertEquals(200.0, embeddedResult.getEmbeddedAddedWidthMm());
        assertEquals(200.0, embeddedResult.getEmbeddedAddedHeightMm());
        assertTrue(embeddedResult.getExactSpacingMm() <= 130.0);
    }

    @Test
    void testRailingCalculation() {
        // Railing: 3000mm length, 1000mm height
        RailingCalculationRequest req = new RailingCalculationRequest(3000.0, 1000.0, "ROUND_TUBE_50");
        req.setMaxPostSpacingMm(1000.0);
        req.setMaxBarSpacingMm(100.0); // <= 100mm per CTE
        req.setInfillBarThicknessMm(16.0);

        RailingCalculationResult result = specialProductsService.calculateRailing(req);

        assertNotNull(result);
        assertEquals(3.0, result.getHandrailLengthMeters());
        // For 3000mm with posts <= 1000mm: 3 spaces -> 4 posts
        assertEquals(4, result.getPostCount());
        assertTrue(result.getExactInfillSpacingMm() <= 100.0);
        assertEquals(4, result.getBasePlatesCount());
        assertTrue(result.getCutList().size() >= 3);
    }

    @Test
    void testMosquitoNetCalculationSlidingAndFixed() {
        // Sliding net for 2-leaf window 1200 x 1200 mm
        MosquitoNetCalculationRequest slidingReq = new MosquitoNetCalculationRequest("SLIDING", 1200.0, 1200.0);
        slidingReq.setWindowLeafCount(2);
        slidingReq.setSlidingOverlapMm(40.0);
        slidingReq.setRunnerDeductionMm(10.0);

        MosquitoNetCalculationResult slidingResult = specialProductsService.calculateMosquitoNet(slidingReq);

        assertNotNull(slidingResult);
        // Width = (1200 + 40) / 2 = 620.0mm
        assertEquals(620.0, slidingResult.getNetWidthMm());
        // Height = 1200 - 10 = 1190.0mm
        assertEquals(1190.0, slidingResult.getNetHeightMm());
        assertTrue(slidingResult.getAccessories().size() >= 5);

        // Fixed net 800 x 1000 mm
        MosquitoNetCalculationRequest fixedReq = new MosquitoNetCalculationRequest("FIXED", 800.0, 1000.0);
        MosquitoNetCalculationResult fixedResult = specialProductsService.calculateMosquitoNet(fixedReq);

        assertEquals(800.0, fixedResult.getNetWidthMm());
        assertEquals(1000.0, fixedResult.getNetHeightMm());
    }
}
