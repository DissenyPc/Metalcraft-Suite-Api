package com.carpinteria.api.config;

import com.carpinteria.api.model.*;
import com.carpinteria.api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Initializes realistic sample data if tables are empty.
 * Can be enabled/disabled via app.data-seeder.enabled in application.properties.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AluminiumProfileRepository aluminiumRepo;
    private final IronProfileRepository ironRepo;
    private final HardwareAccessoryRepository hardwareRepo;
    private final FillingMaterialRepository fillingRepo;
    private final ClientRepository clientRepo;
    private final BudgetRepository budgetRepo;
    private final com.carpinteria.api.service.csv.CsvImportService csvImportService;

    @Value("${app.data-seeder.enabled:true}")
    private boolean seederEnabled;

    public DataInitializer(AluminiumProfileRepository aluminiumRepo,
                           IronProfileRepository ironRepo,
                           HardwareAccessoryRepository hardwareRepo,
                           FillingMaterialRepository fillingRepo,
                           ClientRepository clientRepo,
                           BudgetRepository budgetRepo,
                           com.carpinteria.api.service.csv.CsvImportService csvImportService) {
        this.aluminiumRepo = aluminiumRepo;
        this.ironRepo = ironRepo;
        this.hardwareRepo = hardwareRepo;
        this.fillingRepo = fillingRepo;
        this.clientRepo = clientRepo;
        this.budgetRepo = budgetRepo;
        this.csvImportService = csvImportService;
    }

    @Override
    public void run(String... args) {
        if (!seederEnabled) {
            log.info("Data seeder is disabled.");
            return;
        }

        if (aluminiumRepo.count() <= 5) {
            java.io.File alugomCsv = new java.io.File("Alugom.csv");
            if (alugomCsv.exists()) {
                log.info("Archivo Alugom.csv detectado ({}). Importando catálogo completo...", alugomCsv.getAbsolutePath());
                try (java.io.FileInputStream fis = new java.io.FileInputStream(alugomCsv)) {
                    var res = csvImportService.importAluminiumProfiles(fis);
                    log.info("Catálogo Alugom importado: {}", res.getMessage());
                } catch (Exception e) {
                    log.error("Error al auto-importar Alugom.csv: {}", e.getMessage(), e);
                    if (aluminiumRepo.count() == 0) {
                        seedAluminiumProfiles();
                    }
                }
            } else if (aluminiumRepo.count() == 0) {
                seedAluminiumProfiles();
            }
        }

        if (ironRepo.count() == 0) {
            seedIronProfiles();
        }

        if (hardwareRepo.count() == 0) {
            seedHardwareAccessories();
        }

        if (fillingRepo.count() <= 4) {
            java.io.File huecosCsv = new java.io.File("huecos.csv");
            if (huecosCsv.exists()) {
                log.info("Archivo huecos.csv detectado ({}). Importando catálogo de materiales de relleno...", huecosCsv.getAbsolutePath());
                try (java.io.FileInputStream fis = new java.io.FileInputStream(huecosCsv)) {
                    var res = csvImportService.importFillingMaterials(fis);
                    log.info("Catálogo huecos importado: {}", res.getMessage());
                } catch (Exception e) {
                    log.error("Error al auto-importar huecos.csv: {}", e.getMessage(), e);
                    if (fillingRepo.count() == 0) {
                        seedFillingMaterials();
                    }
                }
            } else if (fillingRepo.count() == 0) {
                seedFillingMaterials();
            }
        }

        if (clientRepo.count() == 0) {
            seedClientsAndBudgets();
        }

        log.info("Carpintería Metálica database seed check completed.");
    }

    private void seedAluminiumProfiles() {
        log.info("Seeding Aluminium Profiles...");
        List<AluminiumProfile> profiles = List.of(
                new AluminiumProfile(
                        "AL-EUR-MARCO-40",
                        "Marco Serie Europea 40mm",
                        "Serie Europea 40",
                        "Blanco Brillo",
                        new BigDecimal("6.00"),
                        new BigDecimal("1.150"),
                        new BigDecimal("12.50"),
                        new BigDecimal("75.00"),
                        45
                ),
                new AluminiumProfile(
                        "AL-EUR-HOJA-40",
                        "Hoja Ventana Corredera Europea 40mm",
                        "Serie Europea 40",
                        "Blanco Brillo",
                        new BigDecimal("6.00"),
                        new BigDecimal("0.980"),
                        new BigDecimal("11.20"),
                        new BigDecimal("67.20"),
                        50
                ),
                new AluminiumProfile(
                        "AL-COR-60-MARCO-RPT",
                        "Marco Cortizo Cor 60 RPT con Rotura de Puente Térmico",
                        "Cortizo Cor 60 RPT",
                        "RAL 7016 Gris Antracita",
                        new BigDecimal("6.50"),
                        new BigDecimal("1.850"),
                        new BigDecimal("24.80"),
                        new BigDecimal("161.20"),
                        30
                ),
                new AluminiumProfile(
                        "AL-COR-60-HOJA-RPT",
                        "Hoja Practicable Cortizo Cor 60 RPT",
                        "Cortizo Cor 60 RPT",
                        "RAL 7016 Gris Antracita",
                        new BigDecimal("6.50"),
                        new BigDecimal("1.720"),
                        new BigDecimal("22.50"),
                        new BigDecimal("146.25"),
                        35
                ),
                new AluminiumProfile(
                        "AL-TUB-50X20-PLATA",
                        "Tubo rectangular 50x20mm",
                        "Tubular Estándar",
                        "Anodizado Plata Mate",
                        new BigDecimal("6.00"),
                        new BigDecimal("0.750"),
                        new BigDecimal("8.90"),
                        new BigDecimal("53.40"),
                        60
                )
        );
        aluminiumRepo.saveAll(profiles);
    }

    private void seedIronProfiles() {
        log.info("Seeding Iron Profiles...");
        List<IronProfile> profiles = List.of(
                new IronProfile(
                        "FE-TUB-40X40X1.5",
                        "Tubo Cuadrado Estructural 40x40x1.5mm",
                        "Tubo Cuadrado",
                        new BigDecimal("1.50"),
                        new BigDecimal("6.00"),
                        new BigDecimal("1.820"),
                        new BigDecimal("1.95"),
                        new BigDecimal("3.55"),
                        new BigDecimal("21.30"),
                        80
                ),
                new IronProfile(
                        "FE-TUB-60X40X2.0",
                        "Tubo Rectangular Estructural 60x40x2.0mm",
                        "Tubo Rectangular",
                        new BigDecimal("2.00"),
                        new BigDecimal("6.00"),
                        new BigDecimal("2.980"),
                        new BigDecimal("1.95"),
                        new BigDecimal("5.81"),
                        new BigDecimal("34.87"),
                        50
                ),
                new IronProfile(
                        "FE-UPN-80",
                        "Viga UPN 80mm Perfil en U",
                        "Viga UPN",
                        new BigDecimal("6.00"),
                        new BigDecimal("6.00"),
                        new BigDecimal("8.640"),
                        new BigDecimal("2.10"),
                        new BigDecimal("18.14"),
                        new BigDecimal("108.86"),
                        20
                ),
                new IronProfile(
                        "FE-ANG-30X3",
                        "Perfil Ángulo L 30x30x3mm",
                        "Ángulo",
                        new BigDecimal("3.00"),
                        new BigDecimal("6.00"),
                        new BigDecimal("1.360"),
                        new BigDecimal("1.90"),
                        new BigDecimal("2.58"),
                        new BigDecimal("15.50"),
                        100
                ),
                new IronProfile(
                        "FE-PLET-40X4",
                        "Pletina Maciza de Hierro 40x4mm",
                        "Pletina",
                        new BigDecimal("4.00"),
                        new BigDecimal("6.00"),
                        new BigDecimal("1.260"),
                        new BigDecimal("1.90"),
                        new BigDecimal("2.39"),
                        new BigDecimal("14.36"),
                        75
                )
        );
        ironRepo.saveAll(profiles);
    }

    private void seedHardwareAccessories() {
        log.info("Seeding Hardware and Accessories...");
        List<HardwareAccessory> hardware = List.of(
                new HardwareAccessory(
                        "HW-BIS-PALA-BL",
                        "Bisagra de pala practicable 2 palas aluminio",
                        "Bisagras",
                        "Giesse",
                        "UD",
                        new BigDecimal("4.20"),
                        new BigDecimal("9.50"),
                        120
                ),
                new HardwareAccessory(
                        "HW-CER-EMB-3P",
                        "Cerradura de seguridad embutir 3 puntos cierre",
                        "Cerraduras",
                        "Tesa",
                        "UD",
                        new BigDecimal("32.50"),
                        new BigDecimal("68.00"),
                        25
                ),
                new HardwareAccessory(
                        "HW-MAN-AC-BL",
                        "Manilla acodada para ventana practicable",
                        "Manillas",
                        "Savio",
                        "UD",
                        new BigDecimal("6.80"),
                        new BigDecimal("15.20"),
                        80
                ),
                new HardwareAccessory(
                        "HW-ROD-CORR-NYLON",
                        "Tándem rodamiento regulable nylon para corredera",
                        "Rodamientos",
                        "Stac",
                        "PAR",
                        new BigDecimal("5.10"),
                        new BigDecimal("12.00"),
                        90
                ),
                new HardwareAccessory(
                        "HW-TOR-AUT-42X32",
                        "Tornillo autotaladrante cabeza alomada 4.2x32mm",
                        "Tornillería",
                        "Index",
                        "CAJA",
                        new BigDecimal("6.50"),
                        new BigDecimal("14.00"),
                        40
                )
        );
        hardwareRepo.saveAll(hardware);
    }

    private void seedFillingMaterials() {
        log.info("Seeding Filling Materials...");
        List<FillingMaterial> materials = List.of(
                new FillingMaterial(
                        "FILL-CRIS-CLIM-4-12-4",
                        "Cristal Doble Acristalamiento Climalit 4/12/4 Transparente",
                        "VIDRIO_CLIMALIT",
                        new BigDecimal("20.00"),
                        "M2",
                        new BigDecimal("48.00"),
                        new BigDecimal("28.00"),
                        new BigDecimal("150.00")
                ),
                new FillingMaterial(
                        "FILL-CRIS-LAM-331",
                        "Vidrio Laminado de Seguridad 3+3.1 Boutiral Transparente",
                        "VIDRIO_LAMINADO",
                        new BigDecimal("6.38"),
                        "M2",
                        new BigDecimal("42.00"),
                        new BigDecimal("24.00"),
                        new BigDecimal("90.00")
                ),
                new FillingMaterial(
                        "FILL-PAN-SAND-16-BL",
                        "Panel Sandwich Térmico 16mm Blanco dos caras lacado",
                        "PANEL_SANDWICH",
                        new BigDecimal("16.00"),
                        "M2",
                        new BigDecimal("34.50"),
                        new BigDecimal("19.50"),
                        new BigDecimal("120.00")
                ),
                new FillingMaterial(
                        "FILL-POLIC-CEL-10",
                        "Policarbonato Celular 10mm Translúcido Filtro UV",
                        "POLICARBONATO",
                        new BigDecimal("10.00"),
                        "M2",
                        new BigDecimal("26.00"),
                        new BigDecimal("14.50"),
                        new BigDecimal("80.00")
                )
        );
        fillingRepo.saveAll(materials);
    }

    private void seedClientsAndBudgets() {
        log.info("Seeding Clients and Sample Budget...");
        Client client1 = new Client(
                "Construcciones Metálicas del Sur SL",
                "Construcciones Metálicas del Sur SL",
                "B-12345678",
                "contacto@construccionesdelsur.es",
                "+34 954 112 233",
                "Polígono Industrial La Isla, Calle A, Nave 12",
                "Dos Hermanas",
                "41700",
                "Sevilla"
        );
        clientRepo.save(client1);

        Client client2 = new Client(
                "Juan Pérez Fernández",
                null,
                "28999888K",
                "juan.perez@example.com",
                "+34 600 554 433",
                "Avenida de la Constitución 45, 3ºB",
                "Mairena del Aljarafe",
                "41927",
                "Sevilla"
        );
        clientRepo.save(client2);

        // Create sample budget with partidas (budget items)
        Budget sampleBudget = new Budget(
                "PRE-2026-0001",
                "Suministro e instalación de ventanas Cortizo Cor 60 RPT con Climalit",
                LocalDate.now(),
                client1
        );
        sampleBudget.setDescription("Presupuesto para 4 ventanas practicables oscilobatientes de aluminio con rotura de puente térmico y vidrio Climalit 4/12/4.");
        sampleBudget.setStatus(BudgetStatus.PENDING_APPROVAL);

        BudgetItem partida1 = new BudgetItem(
                1,
                "ALUMINIO",
                "AL-COR-60-MARCO-RPT",
                "Ventana 2 hojas oscilobatiente Cortizo Cor 60 RPT dimensiones 1200x1200mm acabado RAL 7016 Antracita",
                new BigDecimal("4.00"),
                "UD",
                new BigDecimal("345.00"),
                new BigDecimal("5.00"),
                "Incluye herrajes perimetrales Savio y manillas"
        );

        BudgetItem partida2 = new BudgetItem(
                2,
                "VIDRIO",
                "FILL-CRIS-CLIM-4-12-4",
                "Vidrio Climalit 4/12/4 para ventanas de 1200x1200mm",
                new BigDecimal("5.76"),
                "M2",
                new BigDecimal("48.00"),
                BigDecimal.ZERO,
                "Instalado con calzos de acristalamiento y junta EPDM"
        );

        BudgetItem partida3 = new BudgetItem(
                3,
                "MONTAJE",
                null,
                "Mano de obra, transporte, desmontaje de carpintería existente e instalación con espuma aislante y silicona neutra",
                new BigDecimal("4.00"),
                "UD",
                new BigDecimal("75.00"),
                BigDecimal.ZERO,
                "Trabajo garantizado conforme a normativa de estanqueidad CTE"
        );

        sampleBudget.addItem(partida1);
        sampleBudget.addItem(partida2);
        sampleBudget.addItem(partida3);
        sampleBudget.calculateTotals();

        budgetRepo.save(sampleBudget);
    }
}
