package com.okta.examples.apiamhooks.controller;

import com.okta.examples.apiamhooks.model.Beer;
import com.okta.examples.apiamhooks.model.Person;
import com.okta.examples.apiamhooks.repository.BeerRepository;
import com.okta.examples.apiamhooks.repository.PersonRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class PersonController {

    BeerRepository beerRepository;
    PersonRepository personRepository;

    public PersonController(BeerRepository beerRepository, PersonRepository personRepository) {
        this.beerRepository = beerRepository;
        this.personRepository = personRepository;
    }

    @PostMapping("/api/add-beer")
    @ResponseBody List<Beer> addBeer(@RequestBody Beer beer, @AuthenticationPrincipal Principal principal) {

        Beer b = findOrCreateBeer(beer);
        Person p = findOrCreatePerson(principal);

        // no need to add if it's already there
        if (!p.getFavoriteBeers().contains(b)) {
            p.getFavoriteBeers().add(b);
            personRepository.save(p);
        }

        return p.getFavoriteBeers();
    }

    @GetMapping("/api/me-beers")
    @ResponseBody List<Beer> listBeers(@AuthenticationPrincipal Principal principal) {
        return findOrCreatePerson(principal).getFavoriteBeers();
    }

    private Person findOrCreatePerson(Principal principal) {
        Person p = personRepository.findByEmail(principal.getName());
        if (p == null) {
            p = new Person();
            p.setEmail(principal.getName());
            personRepository.save(p);
        }
        return p;
    }

    private Beer findOrCreateBeer(Beer beer) {
        Beer b = beerRepository.findByName(beer.getName());
        if (b == null) {
            b = new Beer();
            b.setName(beer.getName());
            beerRepository.save(b);
        }
        return b;
    }
}
