package com.carpinteria.api.service.csv;

import com.carpinteria.api.dto.csv.CsvImportResult;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Utility Service for importing CSV files into database tables:
 * - Aluminium Profiles
 * - Iron Profiles
 * - Hardware and Accessories
 * - Filling Materials (Glass, panels, etc.)
 *
 * Supports comma (,) and semicolon (;) separators, Spanish decimal commas (12,50),
 * quoted fields, and UTF-8 BOM headers.
 * Updates records if reference already exists, otherwise inserts new records.
 */
@Service
@Transactional
public class CsvImportService {

    private final AluminiumProfileRepository aluminiumRepo;
    private final IronProfileRepository ironRepo;
    private final HardwareAccessoryRepository hardwareRepo;
    private final FillingMaterialRepository fillingRepo;

    public CsvImportService(AluminiumProfileRepository aluminiumRepo,
                            IronProfileRepository ironRepo,
                            HardwareAccessoryRepository hardwareRepo,
                            FillingMaterialRepository fillingRepo) {
        this.aluminiumRepo = aluminiumRepo;
        this.ironRepo = ironRepo;
        this.hardwareRepo = hardwareRepo;
        this.fillingRepo = fillingRepo;
    }

    public CsvImportResult importAluminiumProfiles(MultipartFile file) {
        try {
            return importAluminiumProfiles(file.getInputStream());
        } catch (Exception e) {
            CsvImportResult res = new CsvImportResult("ALUMINIUM_PROFILES");
            res.addErrorMessage("Error al leer el archivo: " + e.getMessage());
            res.setMessage("Fallo al procesar el archivo CSV de perfiles de aluminio.");
            return res;
        }
    }

    public CsvImportResult importAluminiumProfiles(InputStream is) {
        CsvImportResult result = new CsvImportResult("ALUMINIUM_PROFILES");
        List<Map<String, String>> rows = parseCsvToMap(is);
        result.setTotalRowsProcessed(rows.size());

        int inserted = 0;
        int updated = 0;

        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            int rowNum = i + 2; // considering 1-indexed header

            String ref = getCleanValue(row, "reference", "referencia", "ref", "codigo", "code");
            if (ref == null || ref.isBlank()) {
                result.addErrorMessage(String.format("Fila %d omitida: La referencia/código es obligatoria.", rowNum));
                continue;
            }

            try {
                String name = getCleanValue(row, "name", "nombre", "descripcion", "description");
                String series = getCleanValue(row, "series", "serie");
                String finish = getCleanValue(row, "finish", "acabado", "color", "lacado");
                BigDecimal length = parseBigDecimal(getCleanValue(row, "lengthMeters", "longitud", "largo"), new BigDecimal("6.00"));
                BigDecimal weight = parseBigDecimal(getCleanValue(row, "weightPerMeter", "peso", "pesopormetro"), null);
                BigDecimal priceMeter = parseBigDecimal(getCleanValue(row, "pricePerMeter", "preciometro", "preciopormetro"), null);
                BigDecimal priceBar = parseBigDecimal(getCleanValue(row, "pricePerBar", "preciobarra", "precioporbarra"), null);
                Integer stockBars = parseInteger(getCleanValue(row, "stockBars", "stock", "barras"), 0);
                String desc = getCleanValue(row, "description", "detalles", "notas");

                // Finishes from Alugom catalogue
                BigDecimal pricePlata = parseBigDecimal(getCleanValue(row, "plata", "priceplata"), null);
                BigDecimal priceLBlanco = parseBigDecimal(getCleanValue(row, "lblanco", "blanco", "pricelblanco"), null);
                BigDecimal priceColStd = parseBigDecimal(getCleanValue(row, "colstd", "colorstd", "pricecolstd"), null);
                BigDecimal priceLColor = parseBigDecimal(getCleanValue(row, "lcolor", "pricelcolor"), null);
                BigDecimal priceBronce = parseBigDecimal(getCleanValue(row, "bronce", "pricebronce"), null);
                BigDecimal priceInox = parseBigDecimal(getCleanValue(row, "inox", "priceinox"), null);
                BigDecimal priceMadera = parseBigDecimal(getCleanValue(row, "madera", "pricemadera"), null);
                BigDecimal priceEzzy = parseBigDecimal(getCleanValue(row, "ezzy", "priceezzy"), null);
                BigDecimal priceMetal = parseBigDecimal(getCleanValue(row, "metal", "pricemetal"), null);
                BigDecimal priceAnodNegro = parseBigDecimal(getCleanValue(row, "anodnegro", "negro", "priceanodnegro"), null);
                BigDecimal priceBicol01 = parseBigDecimal(getCleanValue(row, "bicol01", "pricebicol01"), null);
                BigDecimal priceBicol08 = parseBigDecimal(getCleanValue(row, "bicol08", "pricebicol08"), null);
                BigDecimal priceBicol09 = parseBigDecimal(getCleanValue(row, "bicol09", "pricebicol09"), null);

                // Clean legacy OEM characters (e.g. ¥ -> Ñ)
                ref = cleanText(ref);
                name = cleanText(name);
                series = cleanText(series);
                if (finish != null) finish = cleanText(finish);
                if (desc != null) desc = cleanText(desc);

                if (name == null || name.isBlank()) {
                    name = "Perfil de aluminio " + ref;
                }
                if (series == null || series.isBlank()) {
                    series = "Estándar";
                }

                // Default finish
                if (finish == null || finish.isBlank()) {
                    finish = (priceLBlanco != null && priceLBlanco.compareTo(new BigDecimal("9000")) < 0) ? "L Blanco" : "PLATA";
                }

                // Calculate base pricePerMeter if not explicitly provided
                if (priceMeter == null || priceMeter.compareTo(BigDecimal.ZERO) == 0) {
                    if (priceLBlanco != null && priceLBlanco.compareTo(new BigDecimal("9000")) < 0) {
                        priceMeter = priceLBlanco;
                    } else if (pricePlata != null && pricePlata.compareTo(new BigDecimal("9000")) < 0) {
                        priceMeter = pricePlata;
                    } else if (priceColStd != null && priceColStd.compareTo(new BigDecimal("9000")) < 0) {
                        priceMeter = priceColStd;
                    } else {
                        BigDecimal[] candidates = { priceLColor, priceBronce, priceInox, priceMadera, priceEzzy, priceMetal, priceAnodNegro, priceBicol01, priceBicol08, priceBicol09 };
                        for (BigDecimal c : candidates) {
                            if (c != null && c.compareTo(new BigDecimal("9000")) < 0) {
                                priceMeter = c;
                                break;
                            }
                        }
                        if (priceMeter == null) {
                            priceMeter = BigDecimal.ZERO;
                        }
                    }
                }

                if (priceBar == null || priceBar.compareTo(BigDecimal.ZERO) == 0) {
                    priceBar = priceMeter.multiply(length).setScale(2, RoundingMode.HALF_UP);
                }

                // Fallback to findByReference if findFirstByReference is empty (for test mocks)
                Optional<AluminiumProfile> existingOpt = aluminiumRepo.findFirstByReference(ref);
                if (existingOpt == null || existingOpt.isEmpty()) {
                    existingOpt = aluminiumRepo.findByReference(ref);
                }

                AluminiumProfile p;
                if (existingOpt != null && existingOpt.isPresent()) {
                    p = existingOpt.get();
                    updated++;
                } else {
                    p = new AluminiumProfile();
                    p.setReference(ref);
                    inserted++;
                }

                p.setName(name);
                p.setSeries(series);
                p.setFinish(finish);
                p.setLengthMeters(length);
                p.setWeightPerMeter(weight);
                p.setPricePerMeter(priceMeter);
                p.setPricePerBar(priceBar);
                p.setStockBars(stockBars);
                if (desc != null) p.setDescription(desc);

                p.setPricePlata(pricePlata);
                p.setPriceLBlanco(priceLBlanco);
                p.setPriceColStd(priceColStd);
                p.setPriceLColor(priceLColor);
                p.setPriceBronce(priceBronce);
                p.setPriceInox(priceInox);
                p.setPriceMadera(priceMadera);
                p.setPriceEzzy(priceEzzy);
                p.setPriceMetal(priceMetal);
                p.setPriceAnodNegro(priceAnodNegro);
                p.setPriceBicol01(priceBicol01);
                p.setPriceBicol08(priceBicol08);
                p.setPriceBicol09(priceBicol09);

                aluminiumRepo.save(p);

            } catch (Exception e) {
                result.addErrorMessage(String.format("Error en fila %d (%s): %s", rowNum, ref, e.getMessage()));
            }
        }

        result.setInsertedCount(inserted);
        result.setUpdatedCount(updated);
        result.setMessage(String.format("Importación completada: %d insertados, %d actualizados, %d errores de un total de %d registros.",
                inserted, updated, result.getErrorCount(), rows.size()));
        return result;
    }

    private String cleanText(String text) {
        if (text == null) return null;
        return text.replace("¥", "Ñ").replace("¤", "ñ").trim();
    }

    public CsvImportResult importIronProfiles(MultipartFile file) {
        try {
            return importIronProfiles(file.getInputStream());
        } catch (Exception e) {
            CsvImportResult res = new CsvImportResult("IRON_PROFILES");
            res.addErrorMessage("Error al leer el archivo: " + e.getMessage());
            res.setMessage("Fallo al procesar el archivo CSV de perfiles de hierro.");
            return res;
        }
    }

    public CsvImportResult importIronProfiles(InputStream is) {
        CsvImportResult result = new CsvImportResult("IRON_PROFILES");
        List<Map<String, String>> rows = parseCsvToMap(is);
        result.setTotalRowsProcessed(rows.size());

        int inserted = 0;
        int updated = 0;

        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            int rowNum = i + 2;

            String ref = getCleanValue(row, "reference", "referencia", "ref");
            if (ref == null || ref.isBlank()) {
                result.addErrorMessage(String.format("Fila %d omitida: La referencia es obligatoria.", rowNum));
                continue;
            }

            try {
                String name = getCleanValue(row, "name", "nombre", "descripcion");
                String series = getCleanValue(row, "series", "serie", "tipo");
                BigDecimal thick = parseBigDecimal(getCleanValue(row, "thicknessMm", "espesor", "grosor"), null);
                BigDecimal length = parseBigDecimal(getCleanValue(row, "lengthMeters", "longitud", "largo"), new BigDecimal("6.00"));
                BigDecimal weight = parseBigDecimal(getCleanValue(row, "weightPerMeter", "peso", "pesopormetro"), null);
                BigDecimal priceKg = parseBigDecimal(getCleanValue(row, "pricePerKg", "preciokg", "precioporkg"), null);
                BigDecimal priceMeter = parseBigDecimal(getCleanValue(row, "pricePerMeter", "preciometro", "preciopormetro"), BigDecimal.ZERO);
                BigDecimal priceBar = parseBigDecimal(getCleanValue(row, "pricePerBar", "preciobarra", "precioporbarra"), null);
                Integer stockBars = parseInteger(getCleanValue(row, "stockBars", "stock", "barras"), 0);
                String desc = getCleanValue(row, "description", "detalles", "notas");

                if (name == null || name.isBlank()) {
                    name = "Perfil de hierro " + ref;
                }
                if (series == null || series.isBlank()) {
                    series = "Tubo Estándar";
                }
                if (priceBar == null || priceBar.compareTo(BigDecimal.ZERO) == 0) {
                    priceBar = priceMeter.multiply(length).setScale(2, RoundingMode.HALF_UP);
                }

                Optional<IronProfile> existingOpt = ironRepo.findByReference(ref);
                if (existingOpt.isPresent()) {
                    IronProfile p = existingOpt.get();
                    p.setName(name);
                    p.setSeries(series);
                    p.setThicknessMm(thick);
                    p.setLengthMeters(length);
                    p.setWeightPerMeter(weight);
                    p.setPricePerKg(priceKg);
                    p.setPricePerMeter(priceMeter);
                    p.setPricePerBar(priceBar);
                    p.setStockBars(stockBars);
                    if (desc != null) p.setDescription(desc);
                    ironRepo.save(p);
                    updated++;
                } else {
                    IronProfile p = new IronProfile(ref, name, series, thick, length, weight, priceKg, priceMeter, priceBar, stockBars);
                    p.setDescription(desc);
                    ironRepo.save(p);
                    inserted++;
                }

            } catch (Exception e) {
                result.addErrorMessage(String.format("Error en fila %d (%s): %s", rowNum, ref, e.getMessage()));
            }
        }

        result.setInsertedCount(inserted);
        result.setUpdatedCount(updated);
        result.setMessage(String.format("Importación completada: %d insertados, %d actualizados, %d errores de un total de %d registros.",
                inserted, updated, result.getErrorCount(), rows.size()));
        return result;
    }

    public CsvImportResult importHardwareAccessories(MultipartFile file) {
        try {
            return importHardwareAccessories(file.getInputStream());
        } catch (Exception e) {
            CsvImportResult res = new CsvImportResult("HARDWARE_ACCESSORIES");
            res.addErrorMessage("Error al leer el archivo: " + e.getMessage());
            res.setMessage("Fallo al procesar el archivo CSV de herrajes y accesorios.");
            return res;
        }
    }

    public CsvImportResult importHardwareAccessories(InputStream is) {
        CsvImportResult result = new CsvImportResult("HARDWARE_ACCESSORIES");
        List<Map<String, String>> rows = parseCsvToMap(is);
        result.setTotalRowsProcessed(rows.size());

        int inserted = 0;
        int updated = 0;

        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            int rowNum = i + 2;

            String ref = getCleanValue(row, "reference", "referencia", "ref");
            if (ref == null || ref.isBlank()) {
                result.addErrorMessage(String.format("Fila %d omitida: La referencia es obligatoria.", rowNum));
                continue;
            }

            try {
                String name = getCleanValue(row, "name", "nombre", "descripcion");
                String cat = getCleanValue(row, "category", "categoria");
                String brand = getCleanValue(row, "brand", "marca");
                String supplier = getCleanValue(row, "supplier", "proveedor");
                String unit = getCleanValue(row, "unit", "unidad", "ud");
                BigDecimal costPrice = parseBigDecimal(getCleanValue(row, "costPrice", "coste", "preciocoste"), null);
                BigDecimal salePrice = parseBigDecimal(getCleanValue(row, "salePrice", "venta", "precioventa", "precio"), BigDecimal.ZERO);
                Integer stock = parseInteger(getCleanValue(row, "stockQuantity", "stock", "cantidad"), 0);
                Integer minStock = parseInteger(getCleanValue(row, "minStockAlert", "stockminimo", "alertastock"), 5);
                String notes = getCleanValue(row, "notes", "notas", "observaciones");

                if (name == null || name.isBlank()) {
                    name = "Accesorio " + ref;
                }
                if (cat == null || cat.isBlank()) {
                    cat = "General";
                }
                if (unit == null || unit.isBlank()) {
                    unit = "UD";
                }

                Optional<HardwareAccessory> existingOpt = hardwareRepo.findByReference(ref);
                if (existingOpt.isPresent()) {
                    HardwareAccessory h = existingOpt.get();
                    h.setName(name);
                    h.setCategory(cat);
                    h.setBrand(brand);
                    h.setSupplier(supplier);
                    h.setUnit(unit);
                    h.setCostPrice(costPrice);
                    h.setSalePrice(salePrice);
                    h.setStockQuantity(stock);
                    h.setMinStockAlert(minStock);
                    if (notes != null) h.setNotes(notes);
                    hardwareRepo.save(h);
                    updated++;
                } else {
                    HardwareAccessory h = new HardwareAccessory(ref, name, cat, brand, unit, costPrice, salePrice, stock);
                    h.setSupplier(supplier);
                    h.setMinStockAlert(minStock);
                    h.setNotes(notes);
                    hardwareRepo.save(h);
                    inserted++;
                }

            } catch (Exception e) {
                result.addErrorMessage(String.format("Error en fila %d (%s): %s", rowNum, ref, e.getMessage()));
            }
        }

        result.setInsertedCount(inserted);
        result.setUpdatedCount(updated);
        result.setMessage(String.format("Importación completada: %d insertados, %d actualizados, %d errores de un total de %d registros.",
                inserted, updated, result.getErrorCount(), rows.size()));
        return result;
    }

    public CsvImportResult importFillingMaterials(MultipartFile file) {
        try {
            return importFillingMaterials(file.getInputStream());
        } catch (Exception e) {
            CsvImportResult res = new CsvImportResult("FILLING_MATERIALS");
            res.addErrorMessage("Error al leer el archivo: " + e.getMessage());
            res.setMessage("Fallo al procesar el archivo CSV de materiales de relleno.");
            return res;
        }
    }

    public CsvImportResult importFillingMaterials(InputStream is) {
        CsvImportResult result = new CsvImportResult("FILLING_MATERIALS");
        List<Map<String, String>> rows = parseCsvToMap(is);
        result.setTotalRowsProcessed(rows.size());

        int inserted = 0;
        int updated = 0;

        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            int rowNum = i + 2;

            String ref = getCleanValue(row, "reference", "referencia", "ref", "codigo");
            if (ref == null || ref.isBlank()) {
                result.addErrorMessage(String.format("Fila %d omitida: La referencia es obligatoria.", rowNum));
                continue;
            }

            try {
                String name = getCleanValue(row, "name", "nombre", "descripcion", "nombrematerial");
                String matType = getCleanValue(row, "materialType", "tipo", "tipomaterial", "categoria");
                String finishColor = getCleanValue(row, "finishColor", "acabado", "acabadocolor", "color");
                BigDecimal thick = parseBigDecimal(getCleanValue(row, "thicknessMm", "espesor", "grosor"), null);
                String unit = getCleanValue(row, "unit", "unidad", "ud");
                BigDecimal priceM2 = parseBigDecimal(getCleanValue(row, "pricePerSquareMeter", "preciom2", "precio", "precioventa"), BigDecimal.ZERO);
                BigDecimal cost = parseBigDecimal(getCleanValue(row, "costPrice", "coste", "preciocoste"), null);
                BigDecimal stockM2 = parseBigDecimal(getCleanValue(row, "stockM2", "stock", "stockm2"), BigDecimal.ZERO);
                String desc = getCleanValue(row, "description", "detalles", "notas");

                // Dimension constraints from huecos.csv
                Integer minW = parseInteger(getCleanValue(row, "minWidthMm", "anchominmm", "anchomin"), null);
                Integer minH = parseInteger(getCleanValue(row, "minHeightMm", "altominmm", "altomin"), null);
                Integer maxW = parseInteger(getCleanValue(row, "maxWidthMm", "anchomaxmm", "anchomax"), null);
                Integer maxH = parseInteger(getCleanValue(row, "maxHeightMm", "altomaxmm", "altomax"), null);

                if (name == null || name.isBlank()) {
                    name = "Material de relleno " + ref;
                }
                if (matType == null || matType.isBlank()) {
                    matType = "VIDRIO";
                }
                if (unit == null || unit.isBlank()) {
                    unit = "M2";
                }

                Optional<FillingMaterial> existingOpt = fillingRepo.findByReference(ref);
                FillingMaterial fm;
                if (existingOpt.isPresent()) {
                    fm = existingOpt.get();
                    updated++;
                } else {
                    fm = new FillingMaterial();
                    fm.setReference(ref);
                    inserted++;
                }

                fm.setName(name);
                fm.setMaterialType(matType);
                fm.setFinishColor(finishColor);
                fm.setThicknessMm(thick);
                fm.setUnit(unit);
                fm.setPricePerSquareMeter(priceM2);
                fm.setCostPrice(cost);
                fm.setStockM2(stockM2);
                fm.setMinWidthMm(minW);
                fm.setMinHeightMm(minH);
                fm.setMaxWidthMm(maxW);
                fm.setMaxHeightMm(maxH);
                if (desc != null) fm.setDescription(desc);

                fillingRepo.save(fm);

            } catch (Exception e) {
                result.addErrorMessage(String.format("Error en fila %d (%s): %s", rowNum, ref, e.getMessage()));
            }
        }

        result.setInsertedCount(inserted);
        result.setUpdatedCount(updated);
        result.setMessage(String.format("Importación completada: %d insertados, %d actualizados, %d errores de un total de %d registros.",
                inserted, updated, result.getErrorCount(), rows.size()));
        return result;
    }

    // =========================================================================
    // Robust CSV Parsing Utilities
    // =========================================================================

    private List<Map<String, String>> parseCsvToMap(InputStream is) {
        List<Map<String, String>> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                return result;
            }

            // Remove UTF-8 BOM if present
            if (headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }

            char delimiter = detectDelimiter(headerLine);
            List<String> headers = parseCsvLine(headerLine, delimiter);
            List<String> normalizedHeaders = headers.stream()
                    .map(h -> h.toLowerCase().replaceAll("[^a-z0-9]", ""))
                    .toList();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> values = parseCsvLine(line, delimiter);
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < normalizedHeaders.size(); i++) {
                    String val = (i < values.size()) ? values.get(i).trim() : "";
                    row.put(normalizedHeaders.get(i), val);
                }
                result.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear el archivo CSV: " + e.getMessage(), e);
        }
        return result;
    }

    private char detectDelimiter(String line) {
        int semicolons = 0;
        int commas = 0;
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '\"') inQuotes = !inQuotes;
            else if (!inQuotes) {
                if (c == ';') semicolons++;
                else if (c == ',') commas++;
            }
        }
        return (semicolons > commas) ? ';' : ',';
    }

    private List<String> parseCsvLine(String line, char delimiter) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == delimiter && !inQuotes) {
                tokens.add(cleanQuotes(sb.toString()));
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(cleanQuotes(sb.toString()));
        return tokens;
    }

    private String cleanQuotes(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed.replace("\"\"", "\"").trim();
    }

    private String getCleanValue(Map<String, String> row, String... possibleKeys) {
        for (String k : possibleKeys) {
            String normalized = k.toLowerCase().replaceAll("[^a-z0-9]", "");
            if (row.containsKey(normalized)) {
                String val = row.get(normalized);
                if (val != null && !val.isBlank()) {
                    return val.trim();
                }
            }
        }
        return null;
    }

    private BigDecimal parseBigDecimal(String val, BigDecimal defaultVal) {
        if (val == null || val.isBlank()) return defaultVal;
        try {
            // Replace Spanish comma decimal separator with dot
            String cleaned = val.replace("€", "").replace("$", "").trim();
            // If contains both . and , (e.g. 1.234,56 or 1,234.56)
            if (cleaned.contains(".") && cleaned.contains(",")) {
                if (cleaned.lastIndexOf(',') > cleaned.lastIndexOf('.')) {
                    // Spanish format: 1.250,50 -> 1250.50
                    cleaned = cleaned.replace(".", "").replace(",", ".");
                } else {
                    // English format: 1,250.50 -> 1250.50
                    cleaned = cleaned.replace(",", "");
                }
            } else if (cleaned.contains(",")) {
                cleaned = cleaned.replace(",", ".");
            }
            return new BigDecimal(cleaned).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private Integer parseInteger(String val, Integer defaultVal) {
        if (val == null || val.isBlank()) return defaultVal;
        try {
            String cleaned = val.replaceAll("[^0-9-]", "");
            return Integer.parseInt(cleaned);
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
