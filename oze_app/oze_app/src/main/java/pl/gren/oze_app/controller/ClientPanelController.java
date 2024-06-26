package pl.gren.oze_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.gren.oze_app.model.*;
import pl.gren.oze_app.repository.ClientProductRepository;
import pl.gren.oze_app.repository.ClientRepository;
import pl.gren.oze_app.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


// tutaj pojda wszystkie funkcjonalnosci klienta, typu dodaj pompe ciepla, dodaj obliczenia budynku etc.
@Controller
@RequestMapping("/clientpanel")
public class ClientPanelController {

    HeatPumpService heatPumpService;
    ClientRepository clientRepository;
    ClientProductService clientProductService;
    CWUBufforTankService cwuBufforTankService;
    COBufferTankService coBufferTankService;
    ProductService productService;

    @Autowired
    public ClientPanelController(ProductService productService, HeatPumpService heatPumpService, ClientRepository clientRepository, ClientProductService clientProductService, CWUBufforTankService cwuBufforTankService, COBufferTankService coBufferTankService) {
        this.heatPumpService = heatPumpService;
        this.productService = productService;
        this.clientRepository = clientRepository;
        this.clientProductService = clientProductService;
        this.coBufferTankService = coBufferTankService;
        this.cwuBufforTankService = cwuBufforTankService;

    }

    @GetMapping("/addpump/{clientId}")
    public String showAddHeatPumpToClientForm(Model model, @PathVariable Long clientId)
    {
        model.addAttribute("clientId", clientId);
        model.addAttribute("heatPump", new HeatPump());

        List<String> heatPumpTypes = new ArrayList<>();
        heatPumpTypes.add(HeatPumpTypes.SPLIT.toString());
        heatPumpTypes.add(HeatPumpTypes.MONOBLOK.toString());
        heatPumpTypes.add(HeatPumpTypes.ALL_IN_ONE.toString());

        List<String> heatPumpsProducents = heatPumpService.getHeatPumpsProducents();
        model.addAttribute("producents", heatPumpsProducents);
        model.addAttribute("types", heatPumpTypes);

        return "forms/clientHeatPumpForm";
    }

    @GetMapping("/addco-cwu/{clientId}")
    public String showAddCOCWUToClientForm(Model model, @PathVariable Long clientId)
    {
        model.addAttribute("clientId", clientId);
        model.addAttribute("cwuBuffor", new CWUBufforTank());
        model.addAttribute("coBuffer", new COBufferTank());


        List<String> coNames = coBufferTankService.getCOBufferTankNames();
        List<String> cwuNames = cwuBufforTankService.getCWUBufforTankNames();

        model.addAttribute("CWUNames", cwuNames);
        model.addAttribute("CONames", coNames);

        return "forms/clientCWUCO";
    }

    @GetMapping("/savepump/{clientId}")
    public String saveHeatPumpToClient(Model model1, @PathVariable Long clientId, @RequestParam("producent") String producent,
                                     @RequestParam("model") String model,
                                     @RequestParam("type") String type) {

        Client client = clientRepository.findClientById(clientId).orElseThrow(() -> new NoSuchElementException("Nie ma klienta o takim ID"));
        HeatPump heatPump = heatPumpService.getHeatPumpByProducentModelType(producent, model, type);

        Long id = heatPump.getId();
        model1.addAttribute("heatPump", heatPump);
        model1.addAttribute("id", id);
        model1.addAttribute("producent", producent);
        model1.addAttribute("model", model);
        model1.addAttribute("type", type);

        if(client.getClientProducts() == null) {
            ClientProducts clientProducts = new ClientProducts(heatPump);
            client.setClientProducts(clientProducts);
            clientProductService.addClientProduct(clientProducts);
        }
        else
        {
            // do sprawdzenia cos czy przypisanie do settera to rownoczesna aktualizacja tego co w bazie danych....
            client.getClientProducts().setHeatPump(heatPump);
            //ClientProducts clientProducts = clientProductRepository.getReferenceById(clientId);
            //clientProducts.update....

        }
        System.out.println("Dodaliśmy pompe o modelu: " + client.getClientProducts().getHeatPump().getModel() + " do clienta o imieniu: " + client.getName());

        return "forms/clientHeatPumpForm";
    }


    @GetMapping("/showco-cwu/{clientId}")
    public String showCOCWU(@RequestParam("cwuname") String cwuname, @RequestParam("coname") String coname, @RequestParam("heatingCircuits") String heatingCircruits, @RequestParam("hotWaterCirculation") String hotWaterCirculation, Model model, @PathVariable Long clientId) {

        CWUBufforTank cwuBufforTank = cwuBufforTankService.getCWUBufforTankByName(cwuname);
        COBufferTank coBufferTank = coBufferTankService.getCOBufferTankByName(coname);

        model.addAttribute("coBuffer", coBufferTank);
        model.addAttribute("cwuBuffor", cwuBufforTank);
        model.addAttribute("hotWaterCirculation2", hotWaterCirculation);
        model.addAttribute("heatingCircuits2", heatingCircruits);
        model.addAttribute("clientId", clientId);

        System.out.println("Dane coBufferTank: " + coBufferTank.getName());
        System.out.println("cwuname: " + cwuname);
        System.out.println("coname: " + coname);

        return "forms/clientCWUCO";
    }

    @PostMapping("/saveco-cwu/{clientId}")
    public String saveCwuCoToClient(CWUBufforTank cwuBufforTank, COBufferTank coBufferTank, @PathVariable Long clientId, Model model) {

        // ID POBIERZ od co cwu
        Client client = clientRepository.findClientById(clientId).orElseThrow(() -> new NoSuchElementException("Nie ma klienta o takim ID"));


        // przerobic na normalny zapis do repository
        if(client.getClientProducts() == null) {

            ClientProducts clientProducts = new ClientProducts();
            client.setClientProducts(clientProducts);

            // creating product list which we will add to clientproducts
            //adding all products to client products and client products to client

//            clientProducts.setProducts(productService.setCOCWUOthers((Integer.parseInt(heatingCircuits)), hotWaterCirculation));
            clientProductService.addClientProduct(clientProducts);
            client.getClientProducts().setCwuBufforTank(cwuBufforTank);
            client.getClientProducts().setCoBufferTank(coBufferTank);

        }
        else
        {
            client.getClientProducts().setCwuBufforTank(cwuBufforTank);
            client.getClientProducts().setCoBufferTank(coBufferTank);
//            client.getClientProducts().getProducts().addAll(productService.setCOCWUOthers((Integer.parseInt(heatingCircuits)), hotWaterCirculation));
//            System.out.println("Jestesmy w ELSE: Dodaliśmy produkty: " + client.getClientProducts().getProducts().toString());

        }

        model.addAttribute("coBuffer", coBufferTank);

//        System.out.println("Dodaliśmy produkty: " + client.getClientProducts().getProducts().toString());

        System.out.println("Dodaliśmy cobufferTank: " + client.getClientProducts().getCoBufferTank().getModel() + " do klienta o imieniu: " + client.getName());

        model.addAttribute("cwuBuffor", cwuBufforTank);

        System.out.println("Dodaliśmy cwoBufforTank: " + client.getClientProducts().getCwuBufforTank().getModel() + " do klienta o imieniu: " + client.getName());

        return "forms/clientCWUCO";
    }

}
