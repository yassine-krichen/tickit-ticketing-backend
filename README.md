
<div align="center">
  <img src="assets/logo.png" alt="TickIT Logo"/>
  
  <p>
    <img src="https://img.shields.io/badge/Java-1.8-red?style=flat-square&logo=java" alt="Java Version"/>
    <img src="https://img.shields.io/badge/Maven-3.9.2-C71A36?style=flat-square&logo=apache-maven" alt="Maven Version"/>
    <img src="https://img.shields.io/badge/PostgreSQL-11.9-336791?style=flat-square&logo=postgresql" alt="PostgreSQL Version"/>
    <img src="https://img.shields.io/badge/Spring%20Boot-2.5.9-6DB33F?style=flat-square&logo=spring-boot" alt="Spring Boot Version"/>
  </p>
</div>

## 🌍 Language / Langue
- [🇬🇧 English](#english)
- [🇫🇷 Français](#français)

---

<a id="francais"></a>
# 🇫🇷 Français

## 📋 Table des matières
- [À propos du projet](#à-propos-du-projet)
- [Problématique](#problématique)
- [Notre Solution](#notre-solution)
- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Interface Utilisateur](#interface-utilisateur)
- [Installation](#installation)
- [Structure du Projet](#structure-du-projet)
- [Contribution](#contribution)
- [Licence](#licence)
- [Contact](#contact)

<a id="à-propos-du-projet"></a>
## 🎯 À propos du projet
TickIT est un système de gestion de tickets développé par Sofrecom Tunisie pour améliorer le processus de traitement des demandes d'extension fibre d'Orange France. Ce projet s'intègre avec BoTool, une application existante utilisée pour le traitement des dossiers de demande.

### Contexte
Orange France propose des connexions fibre, et les clients peuvent demander une extension pour une connexion internet plus rapide en soumettant un dossier de demande ("case"). Ces demandes sont traitées par Sofrecom Tunisie via l'application BoTool.

### Situation Actuelle
Face à l'identification de nouveaux besoins et d'améliorations potentielles, TickIT a été développé comme un nouveau système intégré pour moderniser BoTool. Ce module se concentre particulièrement sur la gestion des blocages et la traçabilité des actions pendant le traitement des dossiers.

### Objectifs
- Améliorer la visibilité sur l'état des dossiers
- Optimiser la gestion des blocages
- Réduire les pertes de temps
- Augmenter la satisfaction client
- Faciliter la facturation
- Améliorer l'efficacité globale du processus

<a id="problématique"></a>
## ⚠️ Problématique
Les entreprises font face à plusieurs défis dans la gestion du support client :
- Manque de traçabilité des demandes
- Difficulté de priorisation des tickets
- Communication inefficace entre les équipes
- Temps de résolution prolongés
- Absence de métriques de performance

<a id="notre-solution"></a>
## 💡 Notre Solution
TickIT apporte une solution innovante pour la gestion des blocages dans le traitement des dossiers fibre d'Orange France. Notre approche combine des technologies modernes et des fonctionnalités avancées pour résoudre les problèmes de traçabilité et d'efficacité :

- **Subdivision Granulaire des Statuts** : Affinage de l'état "en traitement" en sous-états détaillés pour une identification précise des blocages
- **Suivi Visuel Avancé** : Interface dynamique avec barres de progression et vues chronologiques
- **Intelligence Artificielle** : Analyse prédictive des goulots d'étranglement basée sur l'historique
- **Traçabilité Blockchain** : Enregistrement immuable des actions et transitions
- **Intégration Transparente** : Connexion fluide avec BoTool et autres systèmes

<a id="fonctionnalités"></a>
## ✨ Fonctionnalités

### 🎯 Gestion des Statuts
- **États Granulaires**
  - Découpage précis des étapes de traitement
  - Identification instantanée des blocages
  - Suivi des sous-états en temps réel

### 📊 Tableau de Bord Intelligent
- **Visualisation Dynamique**
  - Barres de progression
  - Statuts codés par couleur
  - Vues chronologiques détaillées
- **KPIs Personnalisables**
  - Temps de traitement par étape
  - Taux de blocage
  - Performance des équipes

### 🤖 Intelligence Artificielle
- **Analyse Prédictive**
  - Détection des motifs de retard
  - Prédiction des goulots d'étranglement
  - Recommandations d'optimisation

### 🔗 Traçabilité Blockchain
- **Audit Trail**
  - Enregistrement immuable des actions
  - Historique complet des transitions
  - Transparence totale du processus

### 🔔 Système de Notifications
- **Alertes Intelligentes**
  - Notifications de blocage prolongé
  - Alertes de performance
  - Rappels automatiques

### 🔄 Intégration
- **Connectivité**
  - API REST robuste
  - Intégration avec BoTool
  - Synchronisation en temps réel

<a id="architecture"></a>
## 🏗 Architecture

### Diagramme de Classes
![Diagramme de Classes](assets/class-diagram.png)

### Diagramme de Cas d'Utilisation
![Diagramme de Cas d'Utilisation](assets/use-case-diagram.png)

<a id="interface-utilisateur"></a>
## 🎨 Interface Utilisateur (En cours)
<!-- ### Dashboard
![Dashboard](assets/ui-dashboard.png)

### Création de Ticket
![Création de Ticket](assets/ui-ticket-creation.png)

### Vue détaillée
![Vue détaillée](assets/ui-ticket-detail.png) -->

<a id="installation"></a>
## 🚀 Installation

1. Cloner le repository
```bash
git clone git@example.com:tickit.git
```

2. Configurer la base de données
```bash
# Se connecter à PostgreSQL
psql -U postgres

# Créer la base de données
CREATE DATABASE tickit;

# Lister toutes les bases de données pour vérifier
\l 

# Donner les privilèges à l'utilisateur postgres
GRANT ALL PRIVILEGES ON DATABASE tickit TO postgres;

# Se connecter à la base de données tickit
\c tickit
```

3. Configurer les variables d'environnement
```bash
# Éditer .env avec vos configurations
# Pas de variables env pour le moment 
```

4. Lancer l'application
```bash
mvn spring-boot:run
```
Ou bien, cliquez `Run` sur la méthode main dans `TickItTicketingSystemApplication.java`

<a id="structure-du-projet"></a>
## 📁 Structure du Projet

```
src/main/java/com/sofrecom/tickit/
├── config/          # Classes de configuration Spring Boot
├── controller/      # Contrôleurs REST
├── model/           # Entités JPA
│   └── enum/        # Énumérations
├── dto/             # Objets de transfert de données
├── repository/      # Interfaces JPA Repository
├── service/         # Interfaces des services
├── impl/            # Implémentations des services
├── security/        # Configuration de sécurité
└── util/            # Classes utilitaires
```

<a id="contribution"></a>
## 👥 Contribution
Ce projet est privé et réservé aux développeurs autorisés. Pour contribuer :
1. Créer une branche pour votre fonctionnalité
2. Commitez vos changements
3. Créer une merge request

<a id="licence"></a>
## 📄 Licence
Ce projet appartient à Sofrecom. Tous droits réservés © 2025 Sofrecom.

<a id="contact"></a>
## 📧 Contact
Pour toute question ou support :
- Yassine Krichen : yassine.krichen@sofrecom.com
- Talel Turki : talel@example.com

---

<a id="english"></a>
# 🇬🇧 English

## 📋 Table of Contents
- [About the Project](#about-the-project)
- [Problem Statement](#problem-statement)
- [Our Solution](#our-solution)
- [Features](#features)
- [Architecture](#architecture)
- [User Interface](#user-interface)
- [Installation](#installation)
- [Project Structure](#project-structure)
- [Contribution](#contribution)
- [License](#license)
- [Contact](#contact)

<a id="about-the-project"></a>
## 🎯 About the Project
TickIT is a ticket management system developed by Sofrecom Tunisia to improve the processing of fiber extension requests for Orange France. It integrates with BoTool, an existing application used for case processing.

### Context
Orange France offers fiber connections, and customers can request an extension for faster internet by submitting a case. TickIT aims to modernize this process by enhancing traceability and blockage management.

### Current Situation
In response to newly identified needs and potential improvements, **TickIT** was developed as a new integrated system to modernize **BoTool**.  
This comprehensive solution focuses particularly on **managing blockages** and **ensuring traceability of actions** during case processing.

### Objectives
- Improve **visibility** into the status of cases  
- Optimize **blockage management**  
- Reduce **time loss**  
- Increase **customer satisfaction**  
- Simplify **billing**  
- Enhance overall **process efficiency**


<a id="problem-statement"></a>
## ⚠️ Problem Statement
Companies face several challenges in customer support management:
- Lack of request traceability
- Difficulty prioritizing tickets
- Inefficient team communication
- Long resolution times
- Lack of performance metrics

<a id="our-solution"></a>
## 💡 Our Solution
TickIT provides an innovative solution for managing bottlenecks in fiber request processing. Our approach leverages modern technologies and advanced features to enhance efficiency and traceability.

<a id="features"></a>
## ✨ Features

### 🎯 Status Management
- **Granular States**
  - Precise breakdown of processing stages
  - Instant identification of blockages
  - Real-time monitoring of sub-states

### 📊 Intelligent Dashboard
- **Dynamic Visualization**
  - Progress bars
  - Color-coded statuses
  - Detailed chronological views
- **Customizable KPIs**
  - Processing time per stage
  - Blockage rate
  - Team performance

### 🤖 Artificial Intelligence
- **Predictive Analysis**
  - Detection of delay patterns
  - Prediction of bottlenecks
  - Optimization recommendations

### 🔗 Blockchain Traceability
- **Audit Trail**
  - Immutable record of actions
  - Complete history of transitions
  - Total process transparency

### 🔔 Notification System
- **Smart Alerts**
  - Alerts for prolonged blockages
  - Performance alerts
  - Automatic reminders

### 🔄 Integration
- **Connectivity**
  - Robust REST API
  - Integration with BoTool
  - Real-time synchronization

<a id="architecture"></a>
## 🏗 Architecture

### Class Diagram
![Class Diagram](assets/class-diagram.png)

### Use Case Diagram
![Use Case Diagram](assets/use-case-diagram.png)

<a id="user-interface"></a>
## 🎨 User Interface (In progress)
<!-- 
### Dashboard
![Dashboard](assets/ui-dashboard.png)

### Ticket Creation
![Ticket Creation](assets/ui-ticket-creation.png)

### Detailed View
![Detailed View](assets/ui-ticket-detail.png) 
-->

<a id="installation"></a>
## 🚀 Installation
1. Clone the repository:
```bash
git clone git@example.com:tickit.git
```
2. Configure the PostgreSQL database
3. Configure the environment variables
```bash
# Connect to PostgreSQL
psql -U postgres

# Create the database
CREATE DATABASE tickit;

# List all databases to check
\l 

# Grant privileges to the user postgres
GRANT ALL PRIVILEGES ON DATABASE tickit TO postgres;

# Connect to the database tickit
\c tickit
```
4. Run the application with Maven:
```bash
mvn spring-boot:run
```
Otherwise, click `Run` on the main method in `TickItTicketingSystemApplication.java`

<a id="project-structure"></a>
## 📁 Project Structure
```
src/main/java/com/sofrecom/tickit/
├── config/          # Spring Boot Configuration Classes
├── controller/      # REST Controllers
├── model/           # JPA Entities
│   └── enum/        # Enumerations
├── dto/             # Data Transfer Objects
├── repository/      # JPA Repository Interfaces
├── service/         # Service Interfaces
├── impl/            # Service Implementations
├── security/        # Security Configuration
└── util/            # Utility Classes
```

<a id="contribution"></a>
## 👥 Contribution
This project is private and restricted to authorized developers. To contribute:
1. Create a feature branch
2. Commit your changes
3. Create a merge request

<a id="license"></a>
## 📄 License
This project is proprietary and belongs to Sofrecom. All rights reserved © 2025 Sofrecom.

<a id="contact"></a>
## 📧 Contact
For any questions or support:
- Yassine Krichen: yassine.krichen@sofrecom.com
- Talel Turki: talel@example.com

---
<div align="center">
  <p>Développé avec passion par l'équipe stagiaire Sofrecom / Developed with passion by the Sofrecom intern team</p>
  <p>© 2025 Sofrecom. Tous droits réservés. / All rights reserved.</p>
</div>