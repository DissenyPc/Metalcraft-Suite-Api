# 🛠️ Carpintería Metálica API - REST Backend

Backend API desarrollado en **Java 21** con **Spring Boot 3**, **Spring Data JPA** y base de datos relacional **MySQL**.

Diseñado específicamente para talleres y empresas de **carpintería metálica y cerrajería**, con gestión de perfiles de aluminio y hierro, herrajes, materiales de relleno (vidrios/paneles) y un motor integral de presupuestos con partidas dinámicas, duplicación rápida y vinculación de clientes.

---

## 🚀 Características Principales

1. **Perfiles Separados por Material**:
   - **Perfiles de Aluminio** (`aluminium_profiles`): gestión por serie (ej. Serie Europea 40, Cortizo Cor 60 RPT), acabado/lacado, metros/barra, kg/m, precio por metro y precio por barra.
   - **Perfiles de Hierro / Acero** (`iron_profiles`): gestión por tipo/serie (tubo cuadrado, rectangular, viga UPN, ángulos, pletinas), espesor en mm, precio por kg, metro y barra.
   - **Actualizaciones Masivas de Precios**: Endpoint para actualizar precios en masa por serie o para toda la tabla, aplicando porcentajes (+/- %) o importes fijos (+/- €).
2. **Herrajes y Accesorios** (`hardware_accessories`):
   - Bisagras, cerraduras multipunto, manillas, rodamientos corredera, tornillería, escuadras.
   - Control de stock y alerta de stock mínimo (`/low-stock`).
3. **Materiales de Relleno** (`filling_materials`):
   - Vidrios (Climalit, laminados de seguridad, monolíticos), paneles sandwich térmicos, policarbonato, chapas.
   - Cálculo por m² y seguimiento de stock.
4. **Presupuestos y Partidas (Line Items)** (`budgets` y `budget_items`):
   - **CRUD Completo** de presupuestos y sus partidas individuales.
   - **Cálculo Automático**: Subtotales, descuentos de partida, descuento global, base imponible, IVA (21%) y total.
   - **Duplicación de Presupuestos**: Clona un presupuesto existente con todas sus partidas hacia un nuevo borrador o hacia otro cliente con 1 clic (`POST /api/budgets/{id}/duplicate`).
   - **Vinculación con Clientes**: Relación formal (`Client`) y snapshot de seguridad para preservar los datos de facturación históricos.
5. **CORS Global**: Configurado para integrarse sin bloqueos con cualquier frontend (React, Angular, Vue, Vite, Next.js).

---

## 📁 Estructura del Proyecto

```text
API/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/carpinteria/api/
│   │   │   ├── CarpinteriaApiApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java          # Configuración CORS global
│   │   │   │   └── DataInitializer.java     # Seeder con datos de prueba realistas
│   │   │   ├── controller/
│   │   │   │   ├── AluminiumProfileController.java
│   │   │   │   ├── IronProfileController.java
│   │   │   │   ├── HardwareAccessoryController.java
│   │   │   │   ├── FillingMaterialController.java
│   │   │   │   ├── ClientController.java
│   │   │   │   └── BudgetController.java
│   │   │   ├── dto/
│   │   │   │   ├── BulkPriceUpdateRequest.java
│   │   │   │   ├── BulkPriceUpdateResult.java
│   │   │   │   └── DuplicateBudgetRequest.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   ├── AluminiumProfile.java
│   │   │   │   ├── IronProfile.java
│   │   │   │   ├── HardwareAccessory.java
│   │   │   │   ├── FillingMaterial.java
│   │   │   │   ├── Client.java
│   │   │   │   ├── Budget.java
│   │   │   │   ├── BudgetItem.java
│   │   │   │   └── BudgetStatus.java
│   │   │   ├── repository/
│   │   │   │   ├── AluminiumProfileRepository.java
│   │   │   │   ├── IronProfileRepository.java
│   │   │   │   ├── HardwareAccessoryRepository.java
│   │   │   │   ├── FillingMaterialRepository.java
│   │   │   │   ├── ClientRepository.java
│   │   │   │   ├── BudgetRepository.java
│   │   │   │   └── BudgetItemRepository.java
│   │   │   └── service/
│   │   │       ├── AluminiumProfileService.java
│   │   │       ├── IronProfileService.java
│   │   │       ├── HardwareAccessoryService.java
│   │   │       ├── FillingMaterialService.java
│   │   │       ├── ClientService.java
│   │   │       └── BudgetService.java
│   │   └── resources/
│   │       └── application.properties       # Conexión MySQL y opciones JPA
│   └── test/
│       └── java/com/carpinteria/api/
│           ├── AluminiumProfileServiceTest.java
│           └── BudgetServiceTest.java
```

---

## ⚙️ Configuración de MySQL (`application.properties`)

El archivo se encuentra en `src/main/resources/application.properties`. Por defecto está preparado para conectar a una instancia MySQL local:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/carpinteria_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> **Nota:** La base de datos `carpinteria_db` se creará automáticamente si tu usuario MySQL dispone de permisos, gracias a `createDatabaseIfNotExist=true`.

---

## 🌐 Endpoints de la API REST

### 1. Perfiles de Aluminio (`/api/profiles/aluminium`)
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/profiles/aluminium` | Listar perfiles (filtros: `?series=...` y `?search=...`) |
| `GET` | `/api/profiles/aluminium/{id}` | Obtener perfil por ID |
| `GET` | `/api/profiles/aluminium/series` | Listar nombres de series disponibles |
| `POST` | `/api/profiles/aluminium` | Crear perfil individual |
| `PUT` | `/api/profiles/aluminium/{id}` | Modificar perfil existente |
| `DELETE`| `/api/profiles/aluminium/{id}` | Eliminar perfil |
| `POST` | `/api/profiles/aluminium/import-csv` | **Importar perfiles desde archivo CSV** (`multipart/form-data`) |
| `POST` | `/api/profiles/aluminium/bulk-price-update` | Actualización masiva de precios por serie o tabla |

---

### 2. Perfiles de Hierro (`/api/profiles/iron`)
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/profiles/iron` | Listar perfiles (filtros: `?series=...` y `?search=...`) |
| `GET` | `/api/profiles/iron/{id}` | Obtener perfil por ID |
| `GET` | `/api/profiles/iron/series` | Listar series/tipos de hierro disponibles |
| `POST` | `/api/profiles/iron` | Crear perfil individual |
| `PUT` | `/api/profiles/iron/{id}` | Modificar perfil existente |
| `DELETE`| `/api/profiles/iron/{id}` | Eliminar perfil |
| `POST` | `/api/profiles/iron/import-csv` | **Importar perfiles desde archivo CSV** (`multipart/form-data`) |
| `POST` | `/api/profiles/iron/bulk-price-update` | Actualización masiva de precios por tipo o tabla |

---

### 3. Herrajes y Accesorios (`/api/hardware-accessories`)
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/hardware-accessories` | Listar accesorios (`?category=...`, `?search=...`) |
| `GET` | `/api/hardware-accessories/categories` | Listar categorías de accesorios |
| `GET` | `/api/hardware-accessories/low-stock` | Alertas de stock bajo (stock <= minStockAlert) |
| `GET` | `/api/hardware-accessories/{id}` | Obtener por ID |
| `POST` | `/api/hardware-accessories` | Crear accesorio |
| `PUT` | `/api/hardware-accessories/{id}` | Actualizar accesorio |
| `DELETE`| `/api/hardware-accessories/{id}` | Eliminar accesorio |
| `POST` | `/api/hardware-accessories/import-csv` | **Importar accesorios desde archivo CSV** (`multipart/form-data`) |
| `POST` | `/api/hardware-accessories/bulk-price-update` | Actualización masiva de precios por categoría o tabla |

---

### 4. Materiales de Relleno / Vidrios (`/api/filling-materials`)
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/filling-materials` | Listar materiales (`?materialType=...`, `?search=...`) |
| `GET` | `/api/filling-materials/types` | Listar tipos de materiales (Climalit, Panel, etc.) |
| `GET` | `/api/filling-materials/{id}` | Obtener por ID |
| `POST` | `/api/filling-materials` | Crear material |
| `PUT` | `/api/filling-materials/{id}` | Actualizar material |
| `DELETE`| `/api/filling-materials/{id}` | Eliminar material |
| `POST` | `/api/filling-materials/import-csv` | **Importar materiales desde archivo CSV** (`multipart/form-data`) |
| `POST` | `/api/filling-materials/bulk-price-update` | Actualización masiva de precios por tipo o tabla |

---

### 5. Importación Centralizada CSV (`/api/import/csv`)
Endpoints dedicados para carga masiva mediante archivos CSV:

| Método | Endpoint | Tabla Destino |
|---|---|---|
| `POST` | `/api/import/csv/aluminium-profiles` | `aluminium_profiles` |
| `POST` | `/api/import/csv/iron-profiles` | `iron_profiles` |
| `POST` | `/api/import/csv/hardware-accessories` | `hardware_accessories` |
| `POST` | `/api/import/csv/filling-materials` | `filling_materials` |

> Admite delimitador por comas (`,`) o punto y coma (`;`), números con decimal español (`12,50`), codificación UTF-8 con o sin BOM, y actualiza automáticamente los registros cuya referencia ya exista.

---

### 6. Ajuste Dinámico de Precios en Lote (`/api/pricing/batch-adjust`)
Endpoint unificado para subir o bajar precios porcentualmente (`+` o `-` %) sobre toda una tabla o filtrando por serie/categoría:

```json
POST /api/pricing/batch-adjust
{
  "targetTable": "ALUMINIUM_PROFILES",
  "seriesFilter": "Serie Europea 40",
  "percentage": 5.0,
  "priceField": "ALL"
}
```
*Tablas disponibles:* `ALUMINIUM_PROFILES`, `IRON_PROFILES`, `HARDWARE_ACCESSORIES`, `FILLING_MATERIALS`. Si `seriesFilter` es omitido o `null`, se aplica a **toda la tabla**.

---

### 7. Clientes (`/api/clients`)
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/clients` | Listar clientes (`?search=...`) |
| `GET` | `/api/clients/{id}` | Obtener cliente por ID |
| `GET` | `/api/clients/tax-id/{taxId}` | Obtener por NIF/CIF |
| `POST` | `/api/clients` | Crear cliente |
| `PUT` | `/api/clients/{id}` | Actualizar cliente |
| `DELETE`| `/api/clients/{id}` | Eliminar cliente |

---

### 8. Presupuestos y Partidas (`/api/budgets`)
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/budgets` | Listar presupuestos (`?status=...`, `?clientId=...`, `?search=...`) |
| `GET` | `/api/budgets/{id}` | Obtener presupuesto completo con sus partidas y datos de cliente |
| `GET` | `/api/budgets/number/{num}` | Obtener presupuesto por código de referencia |
| `POST` | `/api/budgets` | Crear presupuesto (con o sin partidas iniciales) |
| `PUT` | `/api/budgets/{id}` | Actualizar presupuesto y partidas |
| `PATCH` | `/api/budgets/{id}/status` | Cambiar estado (`DRAFT`, `APPROVED`, `INVOICED`, etc.) |
| `DELETE`| `/api/budgets/{id}` | Eliminar presupuesto y todas sus partidas (en cascada) |
| `POST` | `/api/budgets/{id}/duplicate` | **Duplicar presupuesto existente con todas sus partidas y cliente** |
| `GET` | `/api/budgets/{id}/items` | Listar todas las partidas de un presupuesto |
| `GET` | `/api/budgets/{id}/items/{itemId}` | Obtener una partida específica |
| `POST` | `/api/budgets/{id}/items` | Agregar una nueva partida (recalcula totales automáticamente) |
| `PUT` | `/api/budgets/{id}/items/{itemId}` | Actualizar una partida (recalcula totales automáticamente) |
| `DELETE`| `/api/budgets/{id}/items/{itemId}` | Eliminar una partida (recalcula totales automáticamente) |

#### Ejemplo para Duplicar un Presupuesto:
```json
POST /api/budgets/1/duplicate
{
  "newBudgetNumber": "PRE-2026-0002",
  "newTitle": "Revisión 2 - Ventanas Cortizo Cor 60 RPT",
  "targetClientId": null,
  "validityDays": 30
}
```

### 9. Motor de Cálculo de Carpintería (`/api/calculator`)
Servicios de cálculo avanzados para presupuestación técnica y despiece automático:

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/calculator/structure` | **Cálculo de Estructura**: Deducciones de solape (-2.5mm por lado), corte y optimización a stock comercial superior. |
| `POST` | `/api/calculator/shutter` | **Persiana Compacto**: Descuento ancho (-30mm recogedor empotrado) y ajuste de altura por cajón compacto. |
| `POST` | `/api/calculator/fixed-modules` | **Módulos Fijos**: Serie perimetral (marco compartido + parteluz T) o no perimetral (adiciones/deducciones marco independiente con perfil H). |
| `POST` | `/api/calculator/glazing` | **Vanos / Acristalamiento**: Divisiones horizontales/verticales, calzos, m² de vidrio, perímetro de goma y junquillos. |
| `POST` | `/api/calculator/grille` | **Rejas**: Barrotes dinámicos (separación <= 130mm), fijaciones embutidas (+100mm por lado) o atornilladas (4 escuadras). |
| `POST` | `/api/calculator/railing` | **Barandillas**: Pasamanos ergonómicos, pilares, zócalo, placas base y barrotes según código de seguridad CTE. |
| `POST` | `/api/calculator/mosquito-net` | **Mosquiteras**: Correderas (derivadas de la hoja de ventana), fijas y kits de reventa (enrollable, plisada lateral, abatible). |

#### Ejemplo: Cálculo de Persiana Compacto con Recogedor Empotrado
```json
POST /api/calculator/shutter
{
  "openingWidthMm": 1200,
  "openingHeightMm": 1400,
  "boxHeightMm": 155,
  "winderType": "BUILT_IN",
  "builtInWinderDeductionMm": 30.0,
  "guideDeductionMm": 40.0
}
```

#### Ejemplo: Cálculo de Reja de Seguridad
```json
POST /api/calculator/grille
{
  "openingWidthMm": 1000,
  "openingHeightMm": 1200,
  "material": "IRON",
  "fixingType": "SCREWED",
  "maxSpacingMm": 130.0,
  "barThicknessMm": 16.0
}
```

---

## 🧪 Ejecución y Pruebas

### Compilar y Ejecutar Pruebas Unitarias:
```bash
mvn test
```

### Ejecutar la Aplicación:
```bash
mvn spring-boot:run
```

La aplicación arrancará por defecto en `http://localhost:8080`.
Al iniciar, el componente `DataInitializer` creará automáticamente datos de demostración si las tablas se encuentran vacías.
