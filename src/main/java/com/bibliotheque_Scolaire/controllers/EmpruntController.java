package com.bibliotheque_Scolaire.controllers;

import com.bibliotheque_Scolaire.entities.Eleve;
import com.bibliotheque_Scolaire.entities.Ouvrage;
import com.bibliotheque_Scolaire.entities.Pret;
import com.bibliotheque_Scolaire.services.EleveService;
import com.bibliotheque_Scolaire.services.OuvrageService;
import com.bibliotheque_Scolaire.services.PretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/emprunt")
public class EmpruntController {

    @Autowired
    private EleveService eleveService;

    @Autowired
    private OuvrageService ouvrageService;

    @Autowired
    private PretService pretService;

    // Page de vérification d'élève
    @GetMapping("/verification")
    public String verificationEleve(Model model) {
        return "emprunt/verification";
    }

    // Vérifier l'élève par matricule et nom
    @PostMapping("/verifier")
    public String verifierEleve(@RequestParam String matricule, 
                                @RequestParam String nom,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            // Chercher l'élève par matricule
            List<Eleve> eleves = eleveService.getAllEleves();
            Eleve eleveTrouve = eleves.stream()
                    .filter(e -> e.getMatricule().equalsIgnoreCase(matricule.trim()))
                    .findFirst()
                    .orElse(null);

            if (eleveTrouve == null) {
                redirectAttributes.addFlashAttribute("error", 
                    "Aucun élève trouvé avec ce matricule. Veuillez contacter l'administration pour vous inscrire.");
                return "redirect:/emprunt/verification";
            }

            // Vérifier que le nom correspond
            if (!eleveTrouve.getNom().equalsIgnoreCase(nom.trim())) {
                redirectAttributes.addFlashAttribute("error", 
                    "Le nom ne correspond pas au matricule. Veuillez vérifier vos informations.");
                return "redirect:/emprunt/verification";
            }

            // Élève vérifié avec succès
            redirectAttributes.addFlashAttribute("success", 
                "Vérification réussie ! Bienvenue " + eleveTrouve.getNom());
            redirectAttributes.addFlashAttribute("eleveVerifie", eleveTrouve);
            
            return "redirect:/emprunt/catalogue/" + eleveTrouve.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la vérification : " + e.getMessage());
            return "redirect:/emprunt/verification";
        }
    }

    // Catalogue pour élève vérifié
    @GetMapping("/catalogue/{eleveId}")
    public String catalogueEleve(@PathVariable Long eleveId, 
                                 Model model,
                                 @RequestParam(required = false) String niveau,
                                 @RequestParam(required = false) Boolean disponible) {
        
        Eleve eleve = eleveService.getEleveById(eleveId);
        model.addAttribute("eleve", eleve);

        var ouvrages = ouvrageService.getAllOuvrages();

        // Filtrage par niveau
        if (niveau != null && !niveau.isEmpty()) {
            ouvrages = ouvrages.stream()
                    .filter(o -> o.getNiveau().equalsIgnoreCase(niveau))
                    .toList();
        }

        // Filtrage par disponibilité
        if (disponible != null) {
            ouvrages = ouvrages.stream()
                    .filter(o -> o.isDisponible() == disponible)
                    .toList();
        }

        // Créer une Set des IDs des ouvrages déjà empruntés par cet élève
        var pretsActifs = pretService.getAllActiveLoans();
        var ouvrageIdsEmpruntes = pretsActifs.stream()
                .filter(p -> p.getEleve().getId().equals(eleveId))
                .map(p -> p.getOuvrage().getId())
                .collect(java.util.stream.Collectors.toSet());
        
        model.addAttribute("ouvrages", ouvrages);
        model.addAttribute("niveau", niveau);
        model.addAttribute("disponible", disponible);
        model.addAttribute("ouvrageIdsEmpruntes", ouvrageIdsEmpruntes);
        
        return "emprunt/catalogue";
    }

    // Emprunter un ouvrage
    @GetMapping("/emprunter/{eleveId}/{ouvrageId}")
    public String emprunterOuvrage(@PathVariable Long eleveId, 
                                   @PathVariable Long ouvrageId,
                                   RedirectAttributes redirectAttributes) {
        try {
            pretService.createLoan(eleveId, ouvrageId);
            redirectAttributes.addFlashAttribute("success", 
                "Emprunt effectué avec succès ! Vous avez 2 semaines pour retourner l'ouvrage.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de l'emprunt : " + e.getMessage());
        }
        
        return "redirect:/emprunt/catalogue/" + eleveId;
    }

    // Mes emprunts
    @GetMapping("/mes-emprunts/{eleveId}")
    public String mesEmprunts(@PathVariable Long eleveId, Model model) {
        Eleve eleve = eleveService.getEleveById(eleveId);
        List<Pret> prets = pretService.getAllLoans().stream()
                .filter(p -> p.getEleve().getId().equals(eleveId))
                .toList();
        
        model.addAttribute("eleve", eleve);
        model.addAttribute("prets", prets);
        
        return "emprunt/mes-emprunts";
    }
}
