🏭 Bluesky Warehouse Management System
Bluesky Warehouse ist ein enterprise-level Lagerverwaltungssystem, das mit Java und Spring Boot entwickelt wurde. Es umfasst wichtige Funktionen wie Benutzerberechtigungen, Ein- und Auslagerungen, Bestandsmanagement, Lageroptimierung, Materialpreiserfassung und Aufgabenverteilung für Mitarbeiter. Es unterstützt React-Frontend-Anbindungen und Docker-Containerisierung, geeignet für den Einsatz in größeren Netzwerken und internen Serverumgebungen.

GitHub Repository: ZhuyiLiW/Bluesky-Warehouse-Management-System

🚀 Hauptfunktionen
1. Benutzer-Authentifizierung und Berechtigungssteuerung
Ein Sicherheitssystem basierend auf Spring Security und JWT, das Benutzerregistrierung, Login, Logout, Token-Aktualisierung und rollenbasierte Berechtigungen unterstützt. Dies gewährleistet die Sicherheit des Zugriffs auf das System und bietet Flexibilität in Szenarien mit mehreren Berechtigungsstufen.

2. Ein- und Auslagerungsoperationen sowie intelligente Lagerplatzzuweisung
Unterstützt standardisierte Material-Einlagerungs- und Auslagerungsprozesse, protokolliert automatisch Bestandsänderungen und die beteiligten Benutzer, und erstellt eine vollständige Prüfprotokollkette. Das System enthält eine intelligente Auslagerungsstrategie, die basierend auf Parametern wie Materialcharge, Restmenge und Ablaufdatum automatisch den optimalen Auslagerungsplatz auswählt, um die Effizienz zu steigern und manuelle Entscheidungen zu reduzieren.

3. Bestandsaufnahme und dynamische Positionsanpassung
Bietet eine Echtzeit-Bestandsaufnahme-Schnittstelle und einen automatisierten Abgleichmechanismus, der die Bewegungen, Umlagerungen und Bestandsprüfungen der Waren im Lager unterstützt. Durch Transaktionssicherung wird die Konsistenz der Daten gewährleistet. Historische Bestandsaufnahmen können zur Unterstützung des Bestandsmanagements abgefragt werden.

4. Lagerplatzstrukturverwaltung und dynamische Inhaltsbearbeitung
Unterstützt das Modellieren und Verwalten der Lagerplatzstruktur. Das System ermöglicht das Leeren, Verschieben und Visualisieren von Waren an verschiedenen Lagerplätzen sowie das Markieren der Verfügbarkeit. Zur Verbesserung der Raumauslastung werden batch-orientierte Operationen und die Zusammenarbeit mit einer Regel-Engine zur Optimierung der Lagerplatzzuweisung unterstützt.

5. Materialpreisverwaltung und historische Rückverfolgbarkeit
Bietet Funktionen zum Erfassen, Bearbeiten, Einfrieren und Verwalten historischer Versionen von Materialpreisen. Dies stellt sicher, dass der Preisbildungsprozess kontrollierbar und nachvollziehbar bleibt. Es ist mit der Bestandskostenberechnung verbunden, um die Integration von Beschaffungskosten, Gewinnverlustanalyse und Finanzabgleich zu ermöglichen.

6. Aufgabenplanung und Mitarbeiterleistungsüberwachung
Ein System zur Aufgabenvergabe für Lageraufgaben, das verschiedene Dimensionen wie Aufgabentyp, Priorität und Frist unterstützt und an verschiedene Mitarbeiterrollen und -arten angepasst ist. Das System protokolliert automatisch die Ausführungsprotokolle der Aufgaben, die zur Bewertung der Mitarbeiterleistung verwendet werden können.

🛠 Technologischer Stack und Projektstruktur
📦 Übersicht der verwendeten Technologien

Ebene	Technologie
Sprache	Java 21 — Unterstützung für moderne Parallelität und Leistungsoptimierungen

Framework	Spring Boot 3.x 

ORM	Spring Data JPA — Persistenzschicht basierend auf Hibernate

Sicherheit	Spring Security + JWT — Authentifizierung und Autorisierung

Datenbank	MySQL — Relationale Datenbankverwaltung

Nebenläufigkeit	Optimistische Sperren (@Version) und pessimistische Sperren (@Lock), geschäftskritische Logik mit ReentrantLock fein abgestimmt

Fehlerbehandlung	Eigene Fehlerklassen + globale Fehlerbehandlung für eine einheitliche API-Ausgabe

Containerisierung	Docker & Docker Compose — Unterstützung für Container-basierte Bereitstellung

Frontend	React (siehe Frontend-Repository)

Bereitstellung	Interne Serverbereitstellung mit Firewall- und Sicherheitsisolierung sowie Portweiterleitung

🧱 Projektstruktur

src/
├── controller/      // Controller-Schicht: Empfängt Frontend-Anfragen, verarbeitet Parameter, ruft Geschäftslogik auf
├── service/         // Service-Schicht: Kapselt Kernlogik und Geschäftsregeln
├── dao/             // Datenzugriffs-Schicht: Datenbankoperationen mit Spring Data JPA
├── entity/          // Entitäten: Datenbank-Entities mit Optimismus-Sperre @Version
├── configuration/   // Konfigurationen: Spring Security, CORS, JWT usw.
├── exception/       // Fehlerbehandlung: Eigene Fehlerklassen und globale Fehlerbehandlung
├── response/        // Einheitliche Antwortstruktur: Standardisierte API-Antworten
├── Application.java // Einstiegspunkt der Anwendung


⚙️ Schnellstart-Anleitung 还需完善
1. Projekt klonen
bash

git clone https://github.com/ZhuyiLiW/Bluesky-Warehouse-Management-System.git
cd Bluesky-Warehouse-Management-System

2. Datenbank-Konfiguration
Erstellen Sie die Datenbank bluesky_warehouse und konfigurieren Sie die Verbindung in der application.yml:

yaml
复制
编辑
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bluesky_warehouse
    username: root
    password: ihr_passwort
3. Lokale Ausführung
bash
复制
编辑
mvn clean install
mvn spring-boot:run
4. Docker-Start (optional)
bash
复制
编辑
docker-compose up -d
📡 API-Antwortformat
Die einheitliche Antwortstruktur sieht folgendermaßen aus:

json
复制
编辑
{
  "code": 200,
  "message": "Operation erfolgreich",
  "data": {
    // Tatsächliche Rückgabedaten
  }
}
Fehlerbehandlungen folgen ebenfalls diesem einheitlichen Format, um eine globale Fehlerbehandlung und Frontend-Integration zu erleichtern.

📝 Changelog
v1.0.0
Fertigstellung der Kernmodule (Authentifizierung, Ein- und Auslagerung, Bestandsmanagement)

Unterstützung für JWT-basierte Sicherheits- und Berechtigungssteuerung

Aufbau der globalen Fehlerbehandlung und einheitlichen API-Antwortstruktur

Docker-Containerisierung für die Bereitstellung

🤝 Beitrag leisten
Beiträge sind willkommen! Bitte folgen Sie diesen Schritten:

Forken Sie dieses Repository

Erstellen Sie einen Feature-Branch: git checkout -b feature/xxx

Führen Sie Ihre Änderungen durch: git commit -am 'Füge xxx Funktion hinzu'

Pushen Sie den Branch: git push origin feature/xxx

Erstellen Sie eine Pull-Anfrage

📄 Lizenz
Dieses Projekt wird unter der MIT-Lizenz lizenziert.

