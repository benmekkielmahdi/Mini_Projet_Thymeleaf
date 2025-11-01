package com.bibliotheque_Scolaire.services;

import com.bibliotheque_Scolaire.entities.Eleve;
import com.bibliotheque_Scolaire.repositories.EleveRepository;
import com.bibliotheque_Scolaire.repositories.PretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EleveService {
    

    @Autowired
    private EleveRepository eleveRepository;

    @Autowired
    private PretRepository pretRepository;

    public List<Eleve> getAllEleves() {
        return eleveRepository.findAll();
    }

    public Eleve saveEleve(Eleve eleve) {
        return eleveRepository.save(eleve);
    }

    public Eleve getEleveById(Long id) {
        return eleveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Élève introuvable"));
    }

    @Transactional
    public void deleteEleve(Long id) {
        // Supprimer d'abord tous les prêts associés
        var prets = pretRepository.findByEleveId(id);
        pretRepository.deleteAll(prets);
        
        // Ensuite supprimer l'élève
        eleveRepository.deleteById(id);
    }

    public boolean existsByMatricule(String matricule) {
        return eleveRepository.existsByMatricule(matricule);
    }
    
    public boolean existsByMatriculeExcludingId(String matricule, Long id) {
        return eleveRepository.existsByMatriculeAndIdNot(matricule, id);
    }
}

