# Logistics-App-Teamwork

## 🧾 Command Reference

### 📍 1. Route Setup

| Command Keyword        | Description                                                     | Parameters                                  |
|------------------------|-----------------------------------------------------------------|---------------------------------------------|
| `CREATEROUTE`          | Creates a new delivery route with stops and calculates the time | At least 2 cities (locations), time (format: YYYY-MM-DD-HH:MM)  |
| `ASSIGNVEHICLETOROUTE` | Assigns a created truck to a specific delivery route            | Truck ID, Route ID                          |

---

### 🚚 2. Vehicle Management

| Command Keyword  | Description                                                             | Parameters                                                                                                                    |
|------------------|-------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| `CREATETRUCK`    | Creates a new truck (vehicle) with specific initial location and brand. | Brand (e.g., MAN), Location (city code, e.g., SYD)                                                                            |
| `LISTVEHICLES`   | Lists all created trucks/vehicles.                                      | Optional: "Free" to list free vehicles, or a city code (e.g., SYD) to list free vehicles stationed in that city               |

---

### 👤 3. Customer Management

| Command Keyword              | Description                                                      | Parameters                           |
|------------------------------|------------------------------------------------------------------|------------------------------------|
| `CREATECUSTOMERCONTACTINFO` | Creates a new customer contact record with name, phone, email, and city | Full name, Phone, Email, City       |

---

### 📦 4. Package Management

| Command Keyword           | Description                                                  | Parameters                                        |
|---------------------------|--------------------------------------------------------------|--------------------------------------------------|
| `CREATEDELIVERYPACKAGE`   | Creates a delivery package with source, destination, weight, and customer | Origin city, Destination city, Weight (kg), Customer ID |
| `ASSIGNPACKAGE`           | Assigns a package to a specific delivery route               | Route ID, Package ID                             |
| `GETUNASSIGNEDPACKAGES`   | Lists all packages not yet assigned to a delivery route     | —                                                |
| `BULKASSIGNPACKAGES`      | Assigns all unassigned, eligible packages to a provided route | Route ID                                         |

---

### 🔍 5. Inspection & Monitoring

| Command Keyword                  | Description                                                                     | Parameters                                   |
|----------------------------------|---------------------------------------------------------------------------------|----------------------------------------------|
| `LISTROUTES`                     | Lists all delivery routes in the system, or only active routes if specified     | Optional: "active"                            |
| `GETPACKAGESTATE`                | Returns the delivery status of a package at the provided timestamp              | Package ID, Timestamp                         |
| `FINDROUTESSERVICINGSTARTANDEND`| Finds all routes that service or have a subroute from Origin to Destination     | Origin city (e.g., SYD), Destination city (e.g., MEL) |
