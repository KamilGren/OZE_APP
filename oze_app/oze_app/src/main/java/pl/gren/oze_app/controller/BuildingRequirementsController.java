package pl.gren.oze_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.gren.oze_app.model.BuildingRequirements;
import pl.gren.oze_app.model.Client;
import pl.gren.oze_app.repository.ClientRepository;
import pl.gren.oze_app.service.BuildingCalculatorService;
import pl.gren.oze_app.service.BuildingRequirementsService;
import pl.gren.oze_app.service.ClientService;
import pl.gren.oze_app.service.Impl.RedirectServiceImpl;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/buildings")
public class BuildingRequirementsController {

    BuildingRequirementsService buildingRequirementsService;
    BuildingCalculatorService buildingCalculatorService;
    ClientService clientService;
    RedirectServiceImpl redirectService;

    @Autowired
    public BuildingRequirementsController(BuildingRequirementsService buildingRequirementsService, BuildingCalculatorService buildingCalculatorService, ClientService clientService) {
        this.buildingRequirementsService = buildingRequirementsService;
        this.buildingCalculatorService = buildingCalculatorService;
        this.clientService = clientService;
    }

    @GetMapping("/math/{buildingRequirementsID}")
    public String lostMath(@PathVariable Long buildingRequirementsID) {
        BuildingRequirements buildingRequirements = buildingRequirementsService.getBuildingReqById(buildingRequirementsID);
        Long id = buildingRequirements.getId();
        Double CO = buildingCalculatorService.calculateCO(buildingRequirements);
        System.out.println("CO: " + CO);

        return "redirect:/admin/home";
    }


    @GetMapping("/add")
    public String showBuildingRequirementsForm(Model model) {
        // Tworzymy nowy obiekt BuildingRequirements, który będzie używany przez formularz
        BuildingRequirements buildingRequirements = new BuildingRequirements();

        // Przesyłamy pusty obiekt do widoku formularza
        model.addAttribute("buildingRequirements", buildingRequirements);

        // Zwracamy nazwę widoku formularza
        return "buildingRequirementsForm";
    }


    // Endpoint obsługujący żądania POST z formularza
    @PostMapping("/add-building-data/{clientId}")
    public String processBuildingRequirementsForClient(BuildingRequirements buildingRequirements, Model model, @PathVariable Long clientId) {

        // building calculator
        //buildingCalculatorService.lost1(buildingRequirements.getId());

        buildingRequirementsService.saveBuildingRequirements(buildingRequirements);

        Client client = clientService.showClientById(clientId);
        System.out.println("imie: " + client.getName());
        client.setBuildingRequirements(buildingRequirements);


        // Przesyłamy dane do widoku wyniku
        model.addAttribute("buildingRequirements", buildingRequirements);

        // Zwracamy nazwę widoku wyniku
        return "buildingRequirementsResult";
    }

    @PostMapping("/add")
    public String processBuildingRequirements(BuildingRequirements buildingRequirements, Model model) {

        // building calculator
        //buildingCalculatorService.lost1(buildingRequirements.getId());

        buildingRequirementsService.saveBuildingRequirements(buildingRequirements);

        // Przesyłamy dane do widoku wyniku
        model.addAttribute("buildingRequirements", buildingRequirements);

        // Zwracamy nazwę widoku wyniku
        return "buildingRequirementsResult";
    }
}
