package com.carpinteria.api;

import com.carpinteria.api.dto.csv.CsvImportResult;
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
import com.carpinteria.api.service.csv.CsvImportService;
import com.carpinteria.api.service.pricing.PriceAdjustmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataManagementServicesTest {

    @Mock
    private AluminiumProfileRepository aluminiumRepo;

    @Mock
    private IronProfileRepository ironRepo;

    @Mock
    private HardwareAccessoryRepository hardwareRepo;

    @Mock
    private FillingMaterialRepository fillingRepo;

    private CsvImportService csvImportService;
    private PriceAdjustmentService priceAdjustmentService;

    @BeforeEach
    void setUp() {
        csvImportService = new CsvImportService(aluminiumRepo, ironRepo, hardwareRepo, fillingRepo);
        priceAdjustmentService = new PriceAdjustmentService(aluminiumRepo, ironRepo, hardwareRepo, fillingRepo);
    }

    // =========================================================================
    // 1. CSV Import Tests
    // =========================================================================

    @Test
    void testImportAluminiumProfilesFromCsvWithSpanishCommasAndSemicolons() {
        // CSV with semicolon delimiter, Spanish decimal comma (12,50) and quotes
        String csvContent = "reference;name;series;finish;lengthMeters;weightPerMeter;pricePerMeter;pricePerBar;stockBars\n" +
                "AL-TEST-CSV-01;Marco Serie 40;Serie Europea 40;Blanco;6,00;1,15;12,50;75,00;30\n" +
                "AL-TEST-CSV-02;Hoja Serie 40;Serie Europea 40;Plata;6,00;0,95;10,00;60,00;25\n";

        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        when(aluminiumRepo.findByReference("AL-TEST-CSV-01")).thenReturn(Optional.empty());
        when(aluminiumRepo.findByReference("AL-TEST-CSV-02")).thenReturn(Optional.empty());
        when(aluminiumRepo.save(any(AluminiumProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        CsvImportResult result = csvImportService.importAluminiumProfiles(is);

        assertNotNull(result);
        assertEquals(2, result.getTotalRowsProcessed());
        assertEquals(2, result.getInsertedCount());
        assertEquals(0, result.getUpdatedCount());
        assertEquals(0, result.getErrorCount());
        verify(aluminiumRepo, times(2)).save(any(AluminiumProfile.class));
    }

    @Test
    void testImportAluminiumProfilesUpdateExistingRecord() {
        AluminiumProfile existing = new AluminiumProfile("AL-EXISTING", "Antiguo Nombre", "Serie 40", "Blanco",
                new BigDecimal("6.00"), new BigDecimal("1.00"), new BigDecimal("10.00"), new BigDecimal("60.00"), 10);

        String csvContent = "reference,name,series,finish,lengthMeters,pricePerMeter\n" +
                "AL-EXISTING,Nombre Actualizado,Serie Cortizo 60,Antracita,6.50,18.50\n";

        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        when(aluminiumRepo.findByReference("AL-EXISTING")).thenReturn(Optional.of(existing));
        when(aluminiumRepo.save(any(AluminiumProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        CsvImportResult result = csvImportService.importAluminiumProfiles(is);

        assertEquals(1, result.getTotalRowsProcessed());
        assertEquals(0, result.getInsertedCount());
        assertEquals(1, result.getUpdatedCount());
        assertEquals("Nombre Actualizado", existing.getName());
        assertEquals("Serie Cortizo 60", existing.getSeries());
        assertEquals(new BigDecimal("18.50"), existing.getPricePerMeter());
    }

    @Test
    void testImportAlugomCsvCatalogueFormat() {
        String csvContent = "Codigo;Descripcion;PLATA;L Blanco;Col std;L Color;Bronce;Inox;Madera;EZZY;METAL;Anod Negro;BICOL01;BICOL08;BICOL09;Serie\n" +
                "100;CERCO VENTANA 28 S100;8.39;6.97;7.82;8.56;9.62;10.10;11.46;9.42;9.47;9999;9999;9999;9999;100\n" +
                "101;TRAVESA¥O S100;9.73;8.10;9.08;9.93;11.16;11.71;13.29;10.93;10.99;9999;9999;9999;9999;100\n";

        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        when(aluminiumRepo.findFirstByReference(anyString())).thenReturn(Optional.empty());
        when(aluminiumRepo.findByReference(anyString())).thenReturn(Optional.empty());
        when(aluminiumRepo.save(any(AluminiumProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        CsvImportResult result = csvImportService.importAluminiumProfiles(is);

        assertNotNull(result);
        assertEquals(2, result.getTotalRowsProcessed());
        assertEquals(2, result.getInsertedCount());
        assertEquals(0, result.getErrorCount());
    }

    @Test
    void testImportHardwareAccessoriesCsv() {
        String csvContent = "reference,name,category,brand,salePrice,stockQuantity\n" +
                "HW-TEST-01,Bisagra Pala Blanca,Bisagras,Giesse,9.50,50\n" +
                "HW-TEST-02,Cerradura 3 Puntos,Cerraduras,Tesa,68.00,15\n";

        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        when(hardwareRepo.findByReference(anyString())).thenReturn(Optional.empty());
        when(hardwareRepo.save(any(HardwareAccessory.class))).thenAnswer(inv -> inv.getArgument(0));

        CsvImportResult result = csvImportService.importHardwareAccessories(is);

        assertEquals(2, result.getTotalRowsProcessed());
        assertEquals(2, result.getInsertedCount());
        verify(hardwareRepo, times(2)).save(any(HardwareAccessory.class));
    }

    // =========================================================================
    // 2. Dynamic Price Adjustment Tests
    // =========================================================================

    @Test
    void testBatchPriceAdjustmentAluminiumBySeries() {
        AluminiumProfile p1 = new AluminiumProfile("P1", "Marco", "Serie Europea 40", "Blanco",
                new BigDecimal("6.00"), new BigDecimal("1.00"), new BigDecimal("10.00"), new BigDecimal("60.00"), 10);
        AluminiumProfile p2 = new AluminiumProfile("P2", "Hoja", "Serie Europea 40", "Blanco",
                new BigDecimal("6.00"), new BigDecimal("0.90"), new BigDecimal("20.00"), new BigDecimal("120.00"), 5);

        when(aluminiumRepo.findBySeriesIgnoreCase("Serie Europea 40")).thenReturn(List.of(p1, p2));
        when(aluminiumRepo.save(any(AluminiumProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // Increase prices by +5%
        BatchPriceAdjustmentRequest request = new BatchPriceAdjustmentRequest("ALUMINIUM_PROFILES", "Serie Europea 40", new BigDecimal("5.0"));
        BatchPriceAdjustmentResult result = priceAdjustmentService.adjustPrices(request);

        assertNotNull(result);
        assertEquals(2, result.getUpdatedRecordsCount());
        // 10.00 * 1.05 = 10.50
        assertEquals(new BigDecimal("10.50"), p1.getPricePerMeter());
        // 60.00 * 1.05 = 63.00
        assertEquals(new BigDecimal("63.00"), p1.getPricePerBar());
        // 20.00 * 1.05 = 21.00
        assertEquals(new BigDecimal("21.00"), p2.getPricePerMeter());
    }

    @Test
    void testBatchPriceAdjustmentHardwareEntireTableDecrease() {
        HardwareAccessory h1 = new HardwareAccessory("H1", "Bisagra", "Bisagras", "Giesse", "UD",
                new BigDecimal("5.00"), new BigDecimal("10.00"), 20);
        HardwareAccessory h2 = new HardwareAccessory("H2", "Manilla", "Manillas", "Savio", "UD",
                new BigDecimal("10.00"), new BigDecimal("20.00"), 15);

        when(hardwareRepo.findAll()).thenReturn(List.of(h1, h2));
        when(hardwareRepo.save(any(HardwareAccessory.class))).thenAnswer(inv -> inv.getArgument(0));

        // Decrease prices by -10% across entire table (filter = null)
        BatchPriceAdjustmentRequest request = new BatchPriceAdjustmentRequest("HARDWARE_ACCESSORIES", null, new BigDecimal("-10.0"));
        BatchPriceAdjustmentResult result = priceAdjustmentService.adjustPrices(request);

        assertNotNull(result);
        assertEquals(2, result.getUpdatedRecordsCount());
        // 10.00 * 0.90 = 9.00
        assertEquals(new BigDecimal("9.00"), h1.getSalePrice());
        // 20.00 * 0.90 = 18.00
        assertEquals(new BigDecimal("18.00"), h2.getSalePrice());
    }

    @Test
    void testBatchPriceAdjustmentFillingMaterials() {
        FillingMaterial fm = new FillingMaterial("FM1", "Cristal Climalit", "VIDRIO_CLIMALIT",
                new BigDecimal("20.00"), "M2", new BigDecimal("50.00"), new BigDecimal("30.00"), new BigDecimal("100.00"));

        when(fillingRepo.findByMaterialTypeIgnoreCase("VIDRIO_CLIMALIT")).thenReturn(List.of(fm));
        when(fillingRepo.save(any(FillingMaterial.class))).thenAnswer(inv -> inv.getArgument(0));

        // Increase by +8%
        BatchPriceAdjustmentRequest request = new BatchPriceAdjustmentRequest("FILLING_MATERIALS", "VIDRIO_CLIMALIT", new BigDecimal("8.0"));
        BatchPriceAdjustmentResult result = priceAdjustmentService.adjustPrices(request);

        assertNotNull(result);
        assertEquals(1, result.getUpdatedRecordsCount());
        // 50.00 * 1.08 = 54.00
        assertEquals(new BigDecimal("54.00"), fm.getPricePerSquareMeter());
    }

    @Test
    void testImportHuecosCsvCatalogueFormat() {
        String csvContent = "referencia;nombre_material;categoria;acabado_color;precio_m2;ancho_min_mm;alto_min_mm;ancho_max_mm;alto_max_mm\n" +
                "MAT-MON-001;Cristal Monolítico 4mm;Cristal;Incoloro/Gris/Bronce;22.50;100;100;2000;3000\n" +
                "MAT-SAND-001;Panel Sándwich Aluminio 20mm;Panel Aislante;Blanco RAL 9010;45.00;200;200;1250;3000\n";

        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        when(fillingRepo.findByReference(anyString())).thenReturn(Optional.empty());
        when(fillingRepo.save(any(FillingMaterial.class))).thenAnswer(inv -> inv.getArgument(0));

        CsvImportResult result = csvImportService.importFillingMaterials(is);

        assertNotNull(result);
        assertEquals(2, result.getTotalRowsProcessed());
        assertEquals(2, result.getInsertedCount());
        assertEquals(0, result.getErrorCount());
        verify(fillingRepo, times(2)).save(any(FillingMaterial.class));
    }
}
