# Bibliothèque Scolaire - Spring Boot + Thymeleaf
# Architecture 
<img width="1536" height="1024" alt="architecture" src="https://github.com/user-attachments/assets/61ce12a7-c452-4579-96a7-426f44945d86" />

# Demo

https://github.com/user-attachments/assets/613a0e5d-b4b8-4501-b0c6-62eac2dee30c

## 🎬 Demo sur YouTube

Découvrez la démo du projet en haute qualité sur YouTube 👇  

[![Regarder la démo en HD](https://img.youtube.com/vi/EBNgXmqv0vU/maxresdefault.jpg)](https://www.youtube.com/watch?v=EBNgXmqv0vU)

 ##  Captures d’écran
<img width="1435" height="814" alt="Screenshot 2025-11-01 at 23 46 02" src="https://github.com/user-attachments/assets/b421fe43-8e91-4b4e-8bcc-78c27862d9d4" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 46 19" src="https://github.com/user-attachments/assets/0a87c98f-1b1c-401d-b8b5-266c200c24b6" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 46 32" src="https://github.com/user-attachments/assets/602e09e1-f9a3-4bcd-ae8e-781247ea0e79" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 46 36" src="https://github.com/user-attachments/assets/6504b71e-2e95-44af-9802-04b30d6d01bc" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 46 43" src="https://github.com/user-attachments/assets/a12289f5-3d25-407a-bb86-83c6b75decfd" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 46 56" src="https://github.com/user-attachments/assets/8d78739f-cf62-49fa-a92c-37dcdea70c08" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 47 04" src="https://github.com/user-attachments/assets/1737fb81-dc97-4302-a1a7-9e5ab90b9887" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 47 11" src="https://github.com/user-attachments/assets/5ac51b87-86e3-47fa-bcd9-b2989a97c763" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 47 16" src="https://github.com/user-attachments/assets/c4615351-ad64-4566-8db9-f03e8438879f" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 47 23" src="https://github.com/user-attachments/assets/73d12ce9-34d3-4c9c-ba34-300fa77b2eff" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 47 31" src="https://github.com/user-attachments/assets/f8aabee6-769c-4834-8085-3e98f796dba4" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 47 38" src="https://github.com/user-attachments/assets/4fcb7295-b6f8-43f2-b967-886ae4762b9d" />
<img width="1440" height="900" alt="Screenshot 2025-11-01 at 23 47 41" src="https://github.com/user-attachments/assets/16f8194b-1b83-4c82-b27c-206116356cb9" />


## 📋 Description
Application web de gestion de bibliothèque scolaire développée avec Spring Boot et Thymeleaf.



## 🎯 Fonctionnalités

### Ouvrages
- ✅ CRUD complet (Créer, Lire, Modifier, Supprimer)
- ✅ Filtrage par niveau et disponibilité
- ✅ Gestion des images
- ✅ Vue admin et vue élève

### Élèves
- ✅ CRUD complet
- ✅ Filtrage par classe
- ✅ Gestion des matricules

### Prêts
- ✅ Création de prêts
- ✅ Retour de prêts
- ✅ Filtrage par classe et retards
- ✅ Suivi du statut (actif, retourné, en retard)

### Statistiques
- ✅ Prêts par niveau
- ✅ Retards par mois
- ✅ Nombre total de prêts actifs et en retard

## 🛠️ Technologies
- Java 17
- Spring Boot 3.x
- Thymeleaf
- MySQL
- Bootstrap/Tailwind CSS (via DaisyUI)
- Maven

## 📦 Installation

1. **Cloner le projet**
```bash
git clone <url-du-projet>
cd Bibliothèque_Scolaire_Thymleaf_V2
```

2. **Configuration de la base de données**
Créer une base de données MySQL nommée `bibliotheque_scolaire_db`

Modifier les paramètres de connexion dans `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bibliotheque_scolaire_db
spring.datasource.username=votre_username
spring.datasource.password=votre_password
```

3. **Lancer l'application**
```bash
./mvnw spring-boot:run
```

4. **Accéder à l'application**
Ouvrir http://localhost:8081

## 🎨 Structure du Projet
```
src/main/java/com/bibliotheque_Scolaire/
├── entities/        # Entités JPA
├── repositories/    # Repositories Spring Data
├── services/        # Services métier
└── controllers/     # Contrôleurs MVC

src/main/resources/
├── templates/       # Vues Thymeleaf
└── static/         # Ressources statiques
```

## 🌐 Routes Principales
- `/` - Page d'accueil avec statistiques
- `/ouvrages/admin` - Gestion des ouvrages (admin)
- `/ouvrages/eleve` - Catalogue des ouvrages (élèves)
- `/eleves` - Gestion des élèves
- `/prets` - Gestion des prêts
- `/prets/stats` - Statistiques

## 📝 Base de Données

### Table: Ouvrage
- id (Long)
- titre (String)
- niveau (String)
- codeBarres (String)
- disponible (Boolean)
- imageFileName (String)

### Table: Eleve
- id (Long)
- nom (String)
- classe (String)
- matricule (String)

### Table: Pret
- id (composite: eleveId, ouvrageId, datePret)
- dateRetourPrevu (LocalDate)
- dateRetour (LocalDate nullable)
- Relations avec Ouvrage et Eleve

## 🚀 Fonctionnalités Avancées
- Filtrage avancé avec combinaisons de critères
- Détection automatique des retards
- Statistiques en temps réel
- Interface responsive (mobile-first)
- Upload et affichage d'images

## 👤 Auteur
Développé dans le cadre d'un projet scolaire



