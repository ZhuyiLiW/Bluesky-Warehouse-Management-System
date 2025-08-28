🏭 Bluesky Warehouse Management System

Bluesky Warehouse ist ein enterprise-level ERP-System für Lagerverwaltung, entwickelt mit Java (Spring Boot) und React.
Es bietet Funktionen wie Benutzer- und Rollenverwaltung, Warenein-/-auslagerung, Bestandsführung, Preisverwaltung, KPI-Monitoring für Mitarbeiter und zentrale Protokollierung.
Dank Docker-Containerisierung und ELK-Integration ist es für größere Netzwerke und interne Produktivumgebungen geeignet.

📂 GitHub Repository: ZhuyiLiW/Bluesky-Warehouse-Management-System

🚀 Hauptfunktionen
🔐 Benutzerverwaltung & Sicherheit

Spring Security + JWT: Login, Logout, Rollen (Admin, Mitarbeiter usw.).

Method-Level Security mit @PreAuthorize.

CORS- und Profil-spezifische Konfigurationen (dev/prod).

📦 Ein- und Auslagerungen

Standardisierte Prozesse für Wareneingang & Warenausgang.

Automatisches Protokollieren mit Benutzer und Zeitstempel.

Intelligente Slot-Auswahl (Menge, Haltbarkeit, Chargen-Strategie).

Transaktionssicherheit durch @Transactional + Sperrmechanismen.

📊 Bestandsaufnahme & Umlagerung

Echtzeit-Inventur mit automatischem Abgleich.

Umlagerungen zwischen Lagerplätzen mit Transaktionsschutz.

Historische Inventuren abrufbar für Reporting.

🗄️ Lagerplatzverwaltung

Modellierung von Slots (frei, belegt, reserviert).

Batch-Operationen & dynamische Optimierung mit Regel-Engine.

Visualisierung der Slot-Struktur.

💰 Preisverwaltung

Erfassung, Änderung und Historisierung von Materialpreisen.

Rückverfolgbarkeit und Verknüpfung mit Kosten- und Gewinnanalyse.

📋 Aufgaben & KPI

Zuweisung von Aufgaben mit Priorität und Deadline.

Automatisches Logging für Leistungsbewertung.

KPI-Monitoring pro Mitarbeiter.

🛠 Technologischer Stack
Ebene	Technologie
Sprache	Java 21
Framework	Spring Boot 3.x
ORM	Spring Data JPA (Hibernate)
Sicherheit	Spring Security + JWT, CORS, @PreAuthorize
Datenbank	MySQL
Caching	Redis
Nebenläufigkeit	Optimistic Locking (@Version), Pessimistic Locking, ReentrantLock
Fehlerbehandlung	Globale Exception-Handler + eigene Exceptions
Logging & Monitoring	Logback (MDC: traceId, userId), JSON Logs, ELK (Filebeat, Elasticsearch, Kibana)
Frontend	React (separates Repository)
Containerisierung	Docker & Docker Compose (Backend, Frontend, MySQL, ELK)
Deployment	Dev/Prod Profiles, interne Serverbereitstellung
Tests	JUnit + Mockito (Happy Path, Edge Cases, Exceptions)
📂 Projektstruktur
src/main/java/com.example.blueskywarehouse
 ├── Configuration              // Security, Profiles, CORS, JWT
 ├── Controller                 // REST-APIs
 ├── Dto                        // Data Transfer Objects
 ├── Entity                     // JPA Entities (@Version für Optimistic Locking)
 ├── Exception                  // Custom Exceptions + Global ExceptionHandler
 ├── Logging                    // LogContext (MDC: traceId, userId)
 ├── Repository                 // JPA Repositories + Native SQL
 ├── Response                   // Einheitliche API-Response-Struktur
 ├── Service                    // Business-Logik (Transaktionen, Locking, Caching)
 ├── Util                       // Hilfsklassen (z.B. DateTimeUtil)
 └── BlueskyWarehouseApplication // Einstiegspunkt

src/main/resources
 ├── application.properties
 ├── application-dev.properties
 ├── application-prod.properties
 └── logback-spring.xml

src/test/java/com.example.blueskywarehouse
 └── Service
     └── BlueskyWarehouseApplicationTests // Unit Tests


⚙️ Installation & Start
1. Projekt klonen
git clone https://github.com/ZhuyiLiW/Bluesky-Warehouse-Management-System.git
cd Bluesky-Warehouse-Management-System

2. Datenbank konfigurieren
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bluesky_warehouse
    username: root
    password: <your_password>

3. Lokal starten
mvn clean install
mvn spring-boot:run

4. Docker Start
docker-compose up -d

📡 API Response-Format
{
  "code": 200,
  "message": "Operation erfolgreich",
  "data": { ... }
}


Einheitliches Fehler- und Erfolgsformat.

Einfache Integration mit Frontend (React).

📝 Changelog

v2.0.0

v2.0.0

✅ JUnit-Testabdeckung erweitert: Zusätzliche Testszenarien implementiert, sowohl für Erfolgs- als auch Fehlerfälle.

✅ JPQL mit Entitäten verbessert: Abfragen auf Basis von Entitätsklassen refaktoriert, für bessere Lesbarkeit und Wartbarkeit.

✅ Globale Fehlerbehandlung optimiert: Einheitliche Fehlercodes und Response-Formate eingeführt, für konsistente Frontend-Integration.

✅ Spring-Security-Fehlerbehandlung verfeinert: Präzisere Behandlung von Authentifizierungs- und Autorisierungsfehlern.

✅ Redis-Circuit-Breaker hinzugefügt: Erhöhte Systemstabilität durch Abkopplung von Cache-Fehlern.

✅ RESTful-API-Standardisierung: Endpunkte und Benennungen angepasst, um REST-Best Practices zu erfüllen.

🤝 Contribution

Fork Repository

Branch erstellen:

git checkout -b feature/xyz


Änderungen committen:

git commit -am "Add xyz feature"


Push & Pull Request

📄 Lizenz

MIT License
