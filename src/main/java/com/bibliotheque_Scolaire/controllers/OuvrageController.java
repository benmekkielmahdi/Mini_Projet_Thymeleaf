package com.bibliotheque_Scolaire.controllers;

import com.bibliotheque_Scolaire.entities.Ouvrage;
import com.bibliotheque_Scolaire.services.OuvrageService;
import com.bibliotheque_Scolaire.services.PretService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/ouvrages")
public class OuvrageController {

    @Autowired
    private OuvrageService ouvrageService;

    @Autowired
    private PretService pretService;

    private final String uploadDir = "src/main/resources/static/images/";

    // 🔹 Liste admin (CRUD)
    @GetMapping("/admin")
    public String adminListe(Model model,
                             @RequestParam(required = false) String niveau,
                             @RequestParam(required = false) Boolean disponible) {

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

        model.addAttribute("ouvrages", ouvrages);
        model.addAttribute("niveau", niveau);
        model.addAttribute("disponible", disponible);
        return "ouvrages/admin";
    }

    // 🔹 Liste élève (lecture seule)
    @GetMapping("/eleve")
    public String eleveListe(Model model,
                             @RequestParam(required = false) String niveau,
                             @RequestParam(required = false) Boolean disponible) {

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

        // Récupérer tous les emprunts actifs pour vérifier les emprunts déjà effectués
        var pretsActifs = pretService.getAllActiveLoans();
        
        // Créer une map pour vérifier rapidement si un ouvrage est déjà emprunté
        var ouvrageIdsEmpruntes = pretsActifs.stream()
                .map(p -> p.getOuvrage().getId())
                .collect(java.util.stream.Collectors.toSet());
        
        model.addAttribute("ouvrages", ouvrages);
        model.addAttribute("niveau", niveau);
        model.addAttribute("disponible", disponible);
        model.addAttribute("ouvrageIdsEmpruntes", ouvrageIdsEmpruntes);
        return "ouvrages/eleve";
    }

    // 🔹 Formulaire d'emprunt pour élèves
    @GetMapping("/eleve/emprunter/{ouvrageId}")
    public String formEmprunt(@PathVariable Long ouvrageId, Model model) {
        model.addAttribute("ouvrage", ouvrageService.getOuvrageById(ouvrageId));
        return "ouvrages/form-emprunt";
    }

    // 🔹 Formulaire d'ajout
    @GetMapping("/ajouter")
    public String formAjouter(Model model) {
        model.addAttribute("ouvrage", new Ouvrage());
        return "ouvrages/form";
    }

    // 🔹 Enregistrer un ouvrage
    @PostMapping("/save")
    public String saveOuvrage(@Valid @ModelAttribute Ouvrage ouvrage,
                              BindingResult bindingResult,
                              @RequestParam(value = "image", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.ouvrage", bindingResult);
            redirectAttributes.addFlashAttribute("ouvrage", ouvrage);
            redirectAttributes.addFlashAttribute("error", "Veuillez corriger les erreurs du formulaire.");
            return ouvrage.getId() == null ? "redirect:/ouvrages/ajouter" : "redirect:/ouvrages/edit/" + ouvrage.getId();
        }
        
        try {
            Boolean isEdit = ouvrage.getId() != null;
            
            // Si c'est une modification, charger l'ouvrage existant
            if (isEdit) {
                Ouvrage existingOuvrage = ouvrageService.getOuvrageById(ouvrage.getId());
                
                // S'assurer que la quantité n'est pas nulle ou négative
                if (ouvrage.getQuantite() == null || ouvrage.getQuantite() < 0) {
                    ouvrage.setQuantite(existingOuvrage.getQuantite() != null ? existingOuvrage.getQuantite() : 1);
                }
                
                // Mettre à jour la disponibilité selon la quantité
                ouvrage.setDisponible(ouvrage.getQuantite() > 0);
                
                // Préserver l'image existante si aucune nouvelle image n'est uploadée
                if (imageFile.isEmpty()) {
                    ouvrage.setImageFileName(existingOuvrage.getImageFileName());
                } else {
                    // Upload nouvelle image
                    String fileName = imageFile.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + fileName);
                    Files.write(filePath, imageFile.getBytes());
                    ouvrage.setImageFileName(fileName);
                }
                
                // Préserver le codeBarres existant s'il était vide dans le formulaire
                if (ouvrage.getCodeBarres() == null || ouvrage.getCodeBarres().trim().isEmpty()) {
                    ouvrage.setCodeBarres(existingOuvrage.getCodeBarres());
                }
                
            } else {
                // Nouvel ouvrage
                // S'assurer que la quantité est définie et positive
                if (ouvrage.getQuantite() == null || ouvrage.getQuantite() < 0) {
                    ouvrage.setQuantite(1);
                }
                
                // Définir la disponibilité selon la quantité
                ouvrage.setDisponible(ouvrage.getQuantite() > 0);
                
                if (!imageFile.isEmpty()) {
                    String fileName = imageFile.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + fileName);
                    Files.write(filePath, imageFile.getBytes());
                    ouvrage.setImageFileName(fileName);
                }
            }
            
            ouvrageService.saveOuvrage(ouvrage);
            redirectAttributes.addFlashAttribute("success", ouvrage.getId() == null ? 
                "Ouvrage ajouté avec succès !" : "Ouvrage modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la sauvegarde : " + e.getMessage());
        }
        return "redirect:/ouvrages/admin";
    }

    // 🔹 Modifier un ouvrage
    @GetMapping("/edit/{id}")
    public String editOuvrage(@PathVariable Long id, Model model) {
        Ouvrage ouvrage = ouvrageService.getOuvrageById(id);
        model.addAttribute("ouvrage", ouvrage);
        return "ouvrages/form";
    }

    // 🔹 Supprimer un ouvrage
    @GetMapping("/delete/{id}")
    public String deleteOuvrage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ouvrageService.deleteOuvrage(id);
            redirectAttributes.addFlashAttribute("success", "Ouvrage supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/ouvrages/admin";
    }
}
