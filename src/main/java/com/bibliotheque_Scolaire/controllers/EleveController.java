package com.bibliotheque_Scolaire.controllers;

import com.bibliotheque_Scolaire.entities.Eleve;
import com.bibliotheque_Scolaire.services.EleveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/eleves")
public class EleveController {

    @Autowired
    private EleveService eleveService;

    // List all students
    @GetMapping
    public String listEleves(Model model, @RequestParam(required = false) String classe) {
        List<Eleve> eleves;
        if (classe != null && !classe.isEmpty()) {
            eleves = eleveService.getAllEleves().stream()
                    .filter(e -> e.getClasse().equalsIgnoreCase(classe))
                    .toList();
        } else {
            eleves = eleveService.getAllEleves();
        }
        model.addAttribute("eleves", eleves);
        model.addAttribute("classe", classe);
        return "eleves/list";
    }

    // Add student form
    @GetMapping("/ajouter")
    public String addForm(Model model) {
        model.addAttribute("eleve", new Eleve());
        return "eleves/form";
    }

    // Save student
    @PostMapping("/save")
    public String saveEleve(@Valid @ModelAttribute Eleve eleve, 
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.eleve", bindingResult);
            redirectAttributes.addFlashAttribute("eleve", eleve);
            redirectAttributes.addFlashAttribute("error", "Veuillez corriger les erreurs du formulaire.");
            return eleve.getId() == null ? "redirect:/eleves/ajouter" : "redirect:/eleves/edit/" + eleve.getId();
        }
        
        try {
            boolean matriculeAlreadyExists = false;
            
            // Vérifier l'unicité du matricule selon le contexte
            if (eleve.getId() == null) {
                // Nouvel élève : vérifier si le matricule existe
                matriculeAlreadyExists = eleveService.existsByMatricule(eleve.getMatricule());
            } else {
                // Modification : vérifier si le matricule existe ailleurs (exclure l'élève courant)
                matriculeAlreadyExists = eleveService.existsByMatriculeExcludingId(eleve.getMatricule(), eleve.getId());
            }
            
            // Si le matricule existe déjà, afficher l'erreur
            if (matriculeAlreadyExists) {
                redirectAttributes.addFlashAttribute("error", "Ce matricule existe déjà. Veuillez en choisir un autre.");
                redirectAttributes.addFlashAttribute("eleve", eleve);
                if (eleve.getId() == null) {
                    return "redirect:/eleves/ajouter";
                } else {
                    return "redirect:/eleves/edit/" + eleve.getId();
                }
            }
            
            eleveService.saveEleve(eleve);
            redirectAttributes.addFlashAttribute("success", eleve.getId() == null ? 
                "Élève ajouté avec succès !" : "Élève modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la sauvegarde : " + e.getMessage());
        }
        return "redirect:/eleves";
    }

    // Edit student
    @GetMapping("/edit/{id}")
    public String editEleve(@PathVariable Long id, Model model) {
        Eleve eleve = eleveService.getEleveById(id);
        model.addAttribute("eleve", eleve);
        return "eleves/form";
    }

    // Delete student
    @GetMapping("/delete/{id}")
    public String deleteEleve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eleveService.deleteEleve(id);
            redirectAttributes.addFlashAttribute("success", "Élève supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/eleves";
    }
}
