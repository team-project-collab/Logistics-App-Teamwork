# Logistics-App-Teamwork

## Application Startup & Data Persistence

- **Initial Trucks:**  
  Upon application start, the system ensures a total fleet of up to **40 trucks**. These are distributed by brand as follows:
    - 10 Scania
    - 15 MAN
    - 15 Actros

  If any brand has fewer trucks initially, additional trucks of that brand are automatically added at random supported city locations to meet these totals.

- **Supported Cities:**  
  When entering city parameters in commands, use the following city codes:
    - Sydney (`SYD`)
    - Melbourne (`MEL`)
    - Adelaide (`ADL`)
    - Alice Springs (`ASP`)
    - Brisbane (`BRI`)
    - Darwin (`DAR`)
    - Perth (`PER`)

- **Data Persistence:**  
  All application data — including trucks, routes, packages, customers, and locations — is automatically saved in XML files inside the `data` folder. This ensures data is preserved across application restarts.

## 🧾 Command Reference

- Accepted timestamp format for the commands: `YYYY-MM-DD-HH:MM`.
- The keyword `"now"` can be used to insert the current timestamp automatically. The keyword can also be used to add minutes or hours to the current time. Example: "now+5m", "now+3h".

### 📍 1. Route Setup

| Command Keyword        | Description                                                     | Parameters                               |
|------------------------|-----------------------------------------------------------------|------------------------------------------|
| `CREATEROUTE`          | Creates a new delivery route with stops and calculates the time | At least 2 cities (locations), timestamp |
| `ASSIGNVEHICLETOROUTE` | Assigns a created truck to a specific delivery route            | Truck ID, Route ID                       |

---

### 🚚 2. Vehicle Management

| Command Keyword  | Description                                                             | Parameters                                                                                                                    |
|------------------|-------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| `CREATETRUCK`    | Creates a new truck (vehicle) with specific initial location and brand. | Brand (e.g., MAN), location (city code, e.g., SYD)                                                                            |
| `LISTVEHICLES`   | Lists all created trucks/vehicles.                                      | Optional: "Free" to list free vehicles, or a city code (e.g., SYD) to list free vehicles stationed in that city               |

---

### 👤 3. Customer Management

| Command Keyword              | Description                                                      | Parameters                           |
|------------------------------|------------------------------------------------------------------|------------------------------------|
| `CREATECUSTOMERCONTACTINFO` | Creates a new customer contact record with name, phone, email, and city | Full name, phone, email, city       |

---

### 📦 4. Package Management

| Command Keyword           | Description                                                  | Parameters                                        |
|---------------------------|--------------------------------------------------------------|--------------------------------------------------|
| `CREATEDELIVERYPACKAGE`   | Creates a delivery package with source, destination, weight, and customer | Origin city, destination city, weight (kg), customer ID |
| `ASSIGNPACKAGE`           | Assigns a package to a specific delivery route               | Route ID, package ID                             |
| `GETUNASSIGNEDPACKAGES`   | Lists all packages not yet assigned to a delivery route     | —                                                |
| `BULKASSIGNPACKAGES`      | Assigns all unassigned, eligible packages to a provided route | Route ID                                         |

---

### 🔍 5. Inspection & Monitoring

| Command Keyword                  | Description                                                                     | Parameters                                   |
|----------------------------------|---------------------------------------------------------------------------------|----------------------------------------------|
| `LISTROUTES`                     | Lists all delivery routes in the system, or only active routes if specified     | Optional: "active"                            |
| `GETPACKAGESTATE`                | Returns the delivery status of a package at the provided timestamp              | Package ID, timestamp                         |
| `FINDROUTESSERVICINGSTARTANDEND`| Finds all routes that service or have a subroute from origin to destination     | Origin city (e.g., SYD), destination city (e.g., MEL) |
