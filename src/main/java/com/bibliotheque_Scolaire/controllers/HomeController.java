package com.bibliotheque_Scolaire.controllers;

import com.bibliotheque_Scolaire.services.EleveService;
import com.bibliotheque_Scolaire.services.OuvrageService;
import com.bibliotheque_Scolaire.services.PretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private EleveService eleveService;

    @Autowired
    private OuvrageService ouvrageService;

    @Autowired
    private PretService pretService;

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("/admin")
    public String adminHome(Model model) {
        model.addAttribute("totalEleves", eleveService.getAllEleves().size());
        model.addAttribute("totalOuvrages", ouvrageService.getAllOuvrages().size());
        model.addAttribute("totalPretsActifs", pretService.getAllActiveLoans().size());
        model.addAttribute("totalOuvragesDisponibles", ouvrageService.getAvailableOuvrages().size());
        model.addAttribute("totalLate", pretService.getLateLoans().size());
        return "admin/index";
    }
}

