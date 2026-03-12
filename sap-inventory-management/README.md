# 📦 SAP Inventory Management System

A full-stack SAP solution for inventory management built with **ABAP OO**, **RAP (RESTful Application Programming)**, **SAP Fiori Elements**, **BTP (Business Technology Platform)**, and **Joule AI**.

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    SAP BTP (Cloud Foundry)               │
│  ┌──────────────┐   ┌──────────────┐  ┌──────────────┐  │
│  │  XSUAA       │   │ Destinations │  │  Joule AI    │  │
│  │  (Auth)      │   │  (Backend)   │  │  (Assistant) │  │
│  └──────┬───────┘   └──────┬───────┘  └──────────────┘  │
│         │                  │                              │
│  ┌──────▼──────────────────▼───────────────────────────┐ │
│  │           SAP Fiori Elements App (UI5)              │ │
│  │     List Report + Object Page + Custom Actions      │ │
│  └─────────────────────────┬───────────────────────────┘ │
└────────────────────────────┼────────────────────────────┘
                             │ OData V4
┌────────────────────────────▼────────────────────────────┐
│                    SAP S/4HANA (ABAP)                    │
│  ┌─────────────────────────────────────────────────────┐ │
│  │              RAP Business Object                    │ │
│  │   ┌──────────┐  ┌──────────┐  ┌──────────────────┐ │ │
│  │   │  CDS     │  │ Behavior │  │ Service Binding   │ │ │
│  │   │  Views   │  │ Impl.    │  │ (OData V4)        │ │ │
│  │   └──────────┘  └──────────┘  └──────────────────┘ │ │
│  └─────────────────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────┐ │
│  │         ABAP OO Business Logic Layer                │ │
│  │   ┌──────────────┐    ┌──────────────────────────┐ │ │
│  │   │  Interfaces  │    │  Classes (impl.)         │ │ │
│  │   └──────────────┘    └──────────────────────────┘ │ │
│  └─────────────────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────┐ │
│  │         Database Tables (DDIC / CDS)                │ │
│  └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
sap-inventory-management/
│
├── abap/
│   ├── src/
│   │   ├── database_tables/
│   │   │   ├── ZINV_MATERIAL.ddls       # Material master table
│   │   │   ├── ZINV_STOCK.ddls          # Stock levels table
│   │   │   └── ZINV_MOVEMENT.ddls       # Stock movements table
│   │   │
│   │   ├── interfaces/
│   │   │   ├── ZIF_INV_MATERIAL.abap    # Material interface
│   │   │   └── ZIF_INV_STOCK_MANAGER.abap  # Stock manager interface
│   │   │
│   │   ├── classes/
│   │   │   ├── ZCL_INV_MATERIAL.abap    # Material class
│   │   │   ├── ZCL_INV_STOCK_MANAGER.abap  # Stock manager class
│   │   │   └── ZCL_INV_VALIDATOR.abap   # Validation class
│   │   │
│   │   └── rap/
│   │       ├── database_tables/         # RAP root tables
│   │       ├── business_objects/        # CDS Views (root, projection)
│   │       ├── behaviors/               # Behavior definitions & implementations
│   │       ├── service_definitions/     # Service definitions
│   │       └── service_bindings/        # OData V4 bindings
│   │
│   └── test/
│       └── ZCL_INV_TEST.abap            # Unit tests
│
├── fiori/
│   └── webapp/
│       ├── manifest.json                # App descriptor
│       ├── view/                        # XML Views
│       ├── controller/                  # JS Controllers
│       ├── model/                       # Data models
│       ├── annotations/                 # UI Annotations
│       └── i18n/                        # Translations
│
├── btp/
│   ├── mta/
│   │   └── mta.yaml                     # Multi-Target Application descriptor
│   ├── xsuaa/
│   │   └── xs-security.json             # Security config
│   └── destinations/
│       └── destinations.json            # BTP Destinations config
│
├── docs/
│   ├── setup.md                         # Setup guide
│   ├── architecture.md                  # Architecture details
│   └── joule-prompts.md                 # Joule AI prompt examples
│
└── README.md
```

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend Logic | ABAP OO (Classes, Interfaces, Design Patterns) |
| API Layer | RAP (RESTful ABAP Programming) + OData V4 |
| Frontend | SAP Fiori Elements (List Report + Object Page) |
| Authentication | BTP XSUAA (OAuth 2.0) |
| Integration | BTP Destinations |
| AI Assistant | SAP Joule (Generative AI) |
| Deployment | SAP BTP Cloud Foundry (MTA) |

---

## ⚙️ Prerequisites

- SAP S/4HANA 2022+ or SAP BTP ABAP Environment
- SAP Business Application Studio (BAS)
- SAP BTP account with:
  - Cloud Foundry environment
  - XSUAA service instance
  - SAP Build Work Zone (optional, for launchpad)
- Node.js 18+ (for Fiori tooling)
- `@sap/cds-dk` (for local testing)

---

## 🛠️ Setup & Deployment

See [docs/setup.md](docs/setup.md) for full instructions.

### Quick Start

```bash
# 1. Clone repository
git clone https://github.com/your-org/sap-inventory-management.git

# 2. Deploy ABAP objects via abapgit
# Import via SE38 > ZABAPGIT or ADT Git integration

# 3. Deploy Fiori app to BTP
cd fiori
npm install
npm run build
cf push

# 4. Configure destinations in BTP Cockpit
# Import btp/destinations/destinations.json
```

---

## 🤖 Joule AI Integration

This project includes example prompts for SAP Joule to:
- Generate ABAP code snippets
- Explain inventory business logic
- Suggest stock replenishment actions
- Auto-draft purchase order descriptions

See [docs/joule-prompts.md](docs/joule-prompts.md) for examples.

---

## 📄 License

MIT License — see [LICENSE](LICENSE)
