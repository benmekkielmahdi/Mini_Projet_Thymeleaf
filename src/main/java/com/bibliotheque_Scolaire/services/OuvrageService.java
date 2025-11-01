package com.bibliotheque_Scolaire.services;

import com.bibliotheque_Scolaire.entities.Ouvrage;
import com.bibliotheque_Scolaire.repositories.OuvrageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OuvrageService {

    @Autowired
    private OuvrageRepository ouvrageRepository;

    public List<Ouvrage> getAllOuvrages() {
        return ouvrageRepository.findAll();
    }

    public List<Ouvrage> getOuvragesByNiveau(String niveau) {
        return ouvrageRepository.findByNiveau(niveau);
    }

    public List<Ouvrage> getOuvragesDisponibles(Boolean disponible) {
        return ouvrageRepository.findByDisponible(disponible);
    }

    public List<Ouvrage> getOuvragesByNiveauAndDisponible(String niveau, Boolean disponible) {
        return ouvrageRepository.findByNiveauAndDisponible(niveau, disponible);
    }

    public Ouvrage saveOuvrage(Ouvrage ouvrage) {
        // Si le codeBarres est vide ou null, générer un code automatique SEULEMENT pour les nouveaux ouvrages
        if ((ouvrage.getCodeBarres() == null || ouvrage.getCodeBarres().trim().isEmpty()) && ouvrage.getId() == null) {
            ouvrage.setCodeBarres("CODE-" + System.currentTimeMillis());
        }
        // Pour les ouvrages existants, on ne change pas le codeBarres s'il est vide
        // Le controller gère déjà cette logique
        return ouvrageRepository.save(ouvrage);
    }

    public Ouvrage getOuvrageById(Long id) {
        return ouvrageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ouvrage introuvable"));
    }

    public void deleteOuvrage(Long id) {
        ouvrageRepository.deleteById(id);
    }

    public List<Ouvrage> getAvailableOuvrages() {
        return ouvrageRepository.findByDisponible(true);
    }
}


