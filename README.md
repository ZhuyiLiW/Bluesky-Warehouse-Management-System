🏭 Bluesky Warehouse Management System

Bluesky Warehouse ist ein Enterprise-ERP-System für Lagerverwaltung, entwickelt mit Java (Spring Boot) und React.
Es bietet Funktionen wie:

Benutzer- und Rollenverwaltung

Warenein-/-auslagerung

Bestandsführung

Preisverwaltung

KPI-Monitoring für Mitarbeiter

zentrale Protokollierung

Dank Docker-Containerisierung und ELK-Integration ist es für größere Netzwerke und interne Produktivumgebungen geeignet.

📂 GitHub Repository: ZhuyiLiW/Bluesky-Warehouse-Management-System

🚀 Hauptfunktionen
🔐 Benutzerverwaltung & Sicherheit

Spring Security + JWT (Login, Logout, Rollen: Admin, Mitarbeiter usw.)

Method-Level Security mit @PreAuthorize

CORS- und Profil-spezifische Konfigurationen (dev/prod)

📦 Ein- und Auslagerungen

Standardisierte Prozesse für Wareneingang & Warenausgang

Automatisches Protokollieren (Benutzer, Zeitstempel)

Intelligente Slot-Auswahl (Menge, Haltbarkeit, Chargen-Strategie)

Transaktionssicherheit durch @Transactional + Sperrmechanismen

📊 Bestandsaufnahme & Umlagerung

Echtzeit-Inventur mit automatischem Abgleich

Umlagerungen zwischen Lagerplätzen mit Transaktionsschutz

Historische Inventuren abrufbar für Reporting

🗄️ Lagerplatzverwaltung

Modellierung von Slots (frei, belegt, reserviert)

Batch-Operationen & dynamische Optimierung mit Regel-Engine

Visualisierung der Slot-Struktur

💰 Preisverwaltung

Erfassung, Änderung und Historisierung von Materialpreisen

Rückverfolgbarkeit, Kosten- & Gewinnanalyse

📋 Aufgaben & KPI

Aufgaben mit Priorität und Deadline

Automatisches Logging für Leistungsbewertung

KPI-Monitoring pro Mitarbeiter

🛠 Technologischer Stack
Ebene	Technologie
Sprache	Java 21
Framework	Spring Boot 3.x
ORM	Spring Data JPA (Hibernate)
Sicherheit	Spring Security + JWT, CORS, @PreAuthorize
Datenbank	MySQL
Caching	Redis
Nebenläufigkeit	Optimistic Locking (@Version), Pessimistic Locking, ReentrantLock
Fehlerhandling	Globale Exception-Handler, Custom Exceptions
Logging & Monitoring	Logback (MDC: traceId, userId), JSON Logs, ELK (Filebeat, Elasticsearch, Kibana)
Frontend	React (separates Repo)
Containerisierung	Docker & Docker Compose
Deployment	Dev/Prod Profiles, interne Serverbereitstellung
Tests	JUnit + Mockito
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

Projekt klonen

git clone https://github.com/ZhuyiLiW/Bluesky-Warehouse-Management-System.git
cd Bluesky-Warehouse-Management-System


Datenbank konfigurieren
application-dev.properties

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bluesky_warehouse
    username: root
    password: <your_password>


Lokal starten

mvn clean install
mvn spring-boot:run


Docker Start

docker-compose up -d

📡 API Response-Format
{
  "code": 200,
  "message": "Operation erfolgreich",
  "data": { ... }
}


Einheitliches Fehler- und Erfolgsformat

Einfache Integration mit React-Frontend

📝 Changelog (v2.0.0)

✅ JUnit-Testabdeckung erweitert

✅ JPQL-Abfragen mit Entitäten verbessert

✅ Einheitliche Fehlercodes & Response-Formate

✅ Spring-Security-Fehlerbehandlung verfeinert

✅ Redis-Circuit-Breaker für Cache-Fehler eingebaut

✅ RESTful-API-Standardisierung (Endpoints & Naming)

🤝 Contribution

Repository forken

Branch erstellen:

git checkout -b feature/xyz


Änderungen committen:

git commit -am "Add xyz feature"


Push & Pull Request

📄 Lizenz

MIT License
