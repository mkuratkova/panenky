package cz.czechitas.webapp;

import java.time.*;
import java.util.List;

import javax.swing.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
public class HlavniController {

    private PanenkaRepository panenkaRepository;
    private ObrazkyRepository obrazkyRepository;

    public HlavniController() {
        panenkaRepository = new JdbcPanenkaRepository();
        obrazkyRepository = new ObrazkyRepository();
    }

    @RequestMapping("/")
    public ModelAndView zobrazIndex() {
        return new ModelAndView("index");
    }

    @RequestMapping("/seznam.html")
    public ModelAndView zobrazSeznam() {
        ModelAndView drzakNaDataAJmenoSablony = new ModelAndView("seznam");
        List<Panenka> vsechnyPanenky = panenkaRepository.findAll();
        drzakNaDataAJmenoSablony.addObject("seznamPanenek", vsechnyPanenky);
        drzakNaDataAJmenoSablony.addObject("casovaZona", ZoneId.systemDefault());
        return drzakNaDataAJmenoSablony;
    }

    @RequestMapping(value = "/nova.html", method = RequestMethod.GET)
    public ModelAndView zobrazNova() {
        ModelAndView drzakNaDataAJmenoSablony = new ModelAndView("nova");
        drzakNaDataAJmenoSablony.addObject("seznamVrsku", obrazkyRepository.findAllVrsky());
        drzakNaDataAJmenoSablony.addObject("seznamSpodku", obrazkyRepository.findAllSpodky());
        return drzakNaDataAJmenoSablony;
    }

    @RequestMapping(value = "/nova.html", method = RequestMethod.POST)
    public ModelAndView zpracujNova(NovaForm vyplnenyFormular) {
        Panenka novaPanenka = new Panenka(vyplnenyFormular.getJmeno(), vyplnenyFormular.getVrsek(), vyplnenyFormular.getSpodek());
        if (novaPanenka.getVrsek() == null) {
            novaPanenka.setVrsek("");
        }
        if (novaPanenka.getSpodek() == null) {
            novaPanenka.setSpodek("");
        }
        panenkaRepository.save(novaPanenka);
        return new ModelAndView("redirect:/seznam.html");
    }

    @RequestMapping(value = "/{id:[0-9]+}", method = RequestMethod.DELETE)
    public ModelAndView zpracujSmazat(@PathVariable Long id) {
        panenkaRepository.delete(id);
        return new ModelAndView("redirect:/seznam.html");
    }

    @RequestMapping(value = "/{id:[0-9]+}.html", method = RequestMethod.GET)
    public ModelAndView zobrazUpdate(@PathVariable Long id) {
        ModelAndView drzakNaDataAJmenoSablony = new ModelAndView("update");
        Panenka nalezena = panenkaRepository.findById(id);
        NovaForm formularovaData = new NovaForm();
        formularovaData.setJmeno(nalezena.getJmeno());
        drzakNaDataAJmenoSablony.addObject("seznamVrsku", obrazkyRepository.findAllVrsky());
        drzakNaDataAJmenoSablony.addObject("seznamSpodku", obrazkyRepository.findAllSpodky());
        drzakNaDataAJmenoSablony.addObject("panenka", formularovaData);
        return drzakNaDataAJmenoSablony;
    }

    @RequestMapping(value = "/{id:[0-9]+}.html", method = RequestMethod.POST)
    public ModelAndView zpracujUpdate(@PathVariable Long id, NovaForm formular) {
        Panenka upravenyZaznam = new Panenka(formular.getJmeno(), formular.getVrsek(), formular.getSpodek());
        upravenyZaznam.setId(id);
        panenkaRepository.updatuj(upravenyZaznam);
        return new ModelAndView("redirect:/seznam.html");
    }
}
