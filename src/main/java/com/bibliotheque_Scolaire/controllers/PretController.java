package com.bibliotheque_Scolaire.controllers;

import com.bibliotheque_Scolaire.entities.Eleve;
import com.bibliotheque_Scolaire.entities.Pret;
import com.bibliotheque_Scolaire.repositories.EleveRepository;
import com.bibliotheque_Scolaire.services.EleveService;
import com.bibliotheque_Scolaire.services.OuvrageService;
import com.bibliotheque_Scolaire.services.PretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/prets")
public class PretController {

    @Autowired
    private PretService pretService;

    @Autowired
    private OuvrageService ouvrageService;

    @Autowired
    private EleveRepository eleveRepository;

    @Autowired
    private EleveService eleveService;

    // List all loans with filtering
    @GetMapping
    public String listPrets(Model model,
                            @RequestParam(required = false) String niveau,
                            @RequestParam(required = false) String codeBarres) {
        List<Pret> prets;
        
        if (niveau != null && !niveau.isEmpty()) {
            prets = pretService.getLoansByNiveau(niveau);
        } else if (codeBarres != null && !codeBarres.isEmpty()) {
            prets = pretService.getLoansByCodeBarres(codeBarres);
        } else {
            // Show all loans (actifs et retournés)
            prets = pretService.getAllLoans();
        }

        model.addAttribute("prets", prets);
        model.addAttribute("niveau", niveau);
        model.addAttribute("codeBarres", codeBarres);
        
        // Get all students and books for the form
        model.addAttribute("eleves", eleveRepository.findAll());
        model.addAttribute("ouvrages", ouvrageService.getAvailableOuvrages());
        
        return "prets/list";
    }

    // Create a new loan (nouvelle version avec création élève si nécessaire)
    @PostMapping("/create")
    public String createLoan(@RequestParam(required = false) Long eleveId,
                           @RequestParam Long ouvrageId,
                           @RequestParam(required = false) String nom,
                           @RequestParam(required = false) String matricule,
                           @RequestParam(required = false) String classe,
                           RedirectAttributes redirectAttributes) {
        try {
            // Si les infos élève sont fournies (emprunt par l'élève lui-même)
            if (nom != null && matricule != null && classe != null) {
                // Chercher l'élève par matricule
                Eleve eleve = eleveRepository.findFirstByMatricule(matricule);

                // Si l'élève n'existe pas, le créer
                if (eleve == null) {
                    eleve = new Eleve();
                    eleve.setNom(nom);
                    eleve.setMatricule(matricule);
                    eleve.setClasse(classe);
                    eleve = eleveRepository.save(eleve);
                }
                eleveId = eleve.getId();
            }

            if (eleveId == null) {
                redirectAttributes.addFlashAttribute("error", "Informations manquantes !");
                return "redirect:/ouvrages/eleve";
            }

            pretService.createLoan(eleveId, ouvrageId);
            redirectAttributes.addFlashAttribute("success", "Emprunt enregistré avec succès !");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur inattendue s'est produite : " + e.getMessage());
        }
        return "redirect:/ouvrages/eleve";
    }

    // Return a loan
    @GetMapping("/return/{eleveId}/{ouvrageId}")
    public String returnLoan(@PathVariable Long eleveId,
                            @PathVariable Long ouvrageId,
                            @RequestParam String datePret,
                            RedirectAttributes redirectAttributes) {
        try {
            LocalDate pretDate = LocalDate.parse(datePret);
            pretService.returnLoan(eleveId, ouvrageId, pretDate);
            redirectAttributes.addFlashAttribute("success", "Prêt retourné avec succès !");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prets";
    }

    // Statistics page
    @GetMapping("/stats")
    public String stats(Model model) {
        model.addAttribute("statsByNiveau", pretService.getLoansByNiveauStats());
        model.addAttribute("statsLateByMonth", pretService.getLateLoansByMonthStats());
        model.addAttribute("totalActive", pretService.getAllActiveLoans().size());
        model.addAttribute("totalLate", pretService.getLateLoans().size());
        model.addAttribute("totalEleves", eleveService.getAllEleves().size());
        model.addAttribute("totalOuvrages", ouvrageService.getAllOuvrages().size());
        model.addAttribute("totalOuvragesDisponibles", ouvrageService.getAvailableOuvrages().size());
        return "prets/stats";
    }
}

