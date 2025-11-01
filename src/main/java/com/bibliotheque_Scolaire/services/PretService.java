package com.bibliotheque_Scolaire.services;

import com.bibliotheque_Scolaire.entities.*;
import com.bibliotheque_Scolaire.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PretService {

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private OuvrageRepository ouvrageRepository;

    @Autowired
    private EleveRepository eleveRepository;

    // Create a new loan
    @Transactional
    public Pret createLoan(Long eleveId, Long ouvrageId) {
        try {
            Eleve eleve = eleveRepository.findById(eleveId)
                    .orElseThrow(() -> new IllegalArgumentException("Élève introuvable"));
            
            Ouvrage ouvrage = ouvrageRepository.findById(ouvrageId)
                    .orElseThrow(() -> new IllegalArgumentException("Ouvrage introuvable"));

            // Vérifier si l'élève a déjà cet ouvrage en emprunt actif
            List<Pret> existingLoans = pretRepository.findByEleveId(eleveId);
            boolean hasActiveLoan = existingLoans.stream()
                    .anyMatch(p -> p.getOuvrage().getId().equals(ouvrageId) && p.getDateRetour() == null);
            
            if (hasActiveLoan) {
                throw new IllegalArgumentException("Vous avez déjà emprunté cet ouvrage et ne l'avez pas encore retourné");
            }

            // Vérifier si l'ouvrage est disponible (quantité > 0)
            if (ouvrage.getQuantite() == null || ouvrage.getQuantite() <= 0) {
                throw new IllegalArgumentException("L'ouvrage n'est pas disponible (stock épuisé)");
            }

            // Vérifier la disponibilité
            if (!ouvrage.isDisponible()) {
                throw new IllegalArgumentException("L'ouvrage n'est pas disponible");
            }

            // Réduire la quantité de 1
            ouvrage.setQuantite(ouvrage.getQuantite() - 1);
            
            // Marquer comme indisponible si la quantité atteint 0
            if (ouvrage.getQuantite() <= 0) {
                ouvrage.setDisponible(false);
            }
            
            // Assurez-vous que le codeBarres existe
            if (ouvrage.getCodeBarres() == null || ouvrage.getCodeBarres().isEmpty()) {
                ouvrage.setCodeBarres("CODE-" + ouvrage.getId());
            }
            
            ouvrageRepository.save(ouvrage);

            // Create loan
            PretId pretId = new PretId(eleveId, ouvrageId, LocalDate.now());
            Pret pret = new Pret();
            pret.setId(pretId);
            pret.setEleve(eleve);
            pret.setOuvrage(ouvrage);
            pret.setDateRetourPrevu(LocalDate.now().plusWeeks(2)); // 2 weeks loan period

            return pretRepository.save(pret);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur lors de la création du prêt : " + e.getMessage());
        }
    }

    // Return a loan
    @Transactional
    public Pret returnLoan(Long eleveId, Long ouvrageId, LocalDate datePret) {
        PretId pretId = new PretId(eleveId, ouvrageId, datePret);
        Pret pret = pretRepository.findById(pretId)
                .orElseThrow(() -> new IllegalArgumentException("Prêt introuvable"));

        if (pret.getDateRetour() != null) {
            throw new IllegalArgumentException("Le prêt a déjà été retourné");
        }

        Ouvrage ouvrage = pret.getOuvrage();
        
        // Restaurer la quantité de l'ouvrage
        if (ouvrage.getQuantite() == null) {
            ouvrage.setQuantite(0);
        }
        ouvrage.setQuantite(ouvrage.getQuantite() + 1);
        
        // Marquer comme disponible
        if (ouvrage.getQuantite() > 0) {
            ouvrage.setDisponible(true);
        }
        
        ouvrageRepository.save(ouvrage);

        // Update loan return date
        pret.setDateRetour(LocalDate.now());
        return pretRepository.save(pret);
    }

    // Get all active loans
    public List<Pret> getAllActiveLoans() {
        return pretRepository.findByDateRetourIsNull();
    }

    // Get late loans
    public List<Pret> getLateLoans() {
        return pretRepository.findLateLoans(LocalDate.now());
    }

    // Get loans by class
    public List<Pret> getLoansByClasse(String classe) {
        return pretRepository.findByClasse(classe);
    }

    // Get loans by niveau
    public List<Pret> getLoansByNiveau(String niveau) {
        return pretRepository.findByNiveau(niveau);
    }

    // Get loans by code barres
    public List<Pret> getLoansByCodeBarres(String codeBarres) {
        return pretRepository.findByOuvrageCodeBarres(codeBarres);
    }

    // Get late loans by class
    public List<Pret> getLateLoansByClasse(String classe) {
        return pretRepository.findLateLoansByClasse(classe, LocalDate.now());
    }

    // Statistics: Get count of loans by niveau
    public Map<String, Long> getLoansByNiveauStats() {
        Map<String, Long> stats = new HashMap<>();
        try {
            List<Object[]> results = pretRepository.countLoansByNiveau();
            for (Object[] row : results) {
                stats.put((String) row[0], (Long) row[1]);
            }
        } catch (Exception e) {
            // En cas d'erreur, retourner une map vide
        }
        return stats;
    }

    // Statistics: Get late loans by month
    public Map<String, Long> getLateLoansByMonthStats() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> results = pretRepository.countLateLoansByMonth(LocalDate.now());
        for (Object[] row : results) {
            int year = (Integer) row[0];
            int month = (Integer) row[1];
            long count = (Long) row[2];
            String key = year + "-" + (month < 10 ? "0" + month : month);
            stats.put(key, count);
        }
        return stats;
    }

    public List<Pret> getAllLoans() {
        return pretRepository.findAll();
    }

    // Check if a student has an active loan for a specific book
    public boolean hasActiveLoan(Long eleveId, Long ouvrageId) {
        List<Pret> existingLoans = pretRepository.findByEleveId(eleveId);
        return existingLoans.stream()
                .anyMatch(p -> p.getOuvrage().getId().equals(ouvrageId) && p.getDateRetour() == null);
    }
}

