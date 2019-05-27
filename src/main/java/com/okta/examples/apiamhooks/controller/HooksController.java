package com.okta.examples.apiamhooks.controller;

import com.okta.examples.apiamhooks.model.Beer;
import com.okta.examples.apiamhooks.model.CreateHookRequest;
import com.okta.examples.apiamhooks.model.IDTokenPatchResponse;
import com.okta.examples.apiamhooks.model.Person;
import com.okta.examples.apiamhooks.model.TokenHookRequest;
import com.okta.examples.apiamhooks.model.TokenHookResponse;
import com.okta.examples.apiamhooks.model.TokenPatchResponse;
import com.okta.examples.apiamhooks.repository.PersonRepository;
import com.okta.examples.apiamhooks.service.InlineHookService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hooks")
public class HooksController {

    private InlineHookService inlineHookService;
    private PersonRepository personRepository;

    public HooksController(InlineHookService inlineHookService, PersonRepository personRepository) {
        this.inlineHookService = inlineHookService;
        this.personRepository = personRepository;
    }

    @PostMapping("/apiam")
    public TokenHookResponse apiam(@RequestBody TokenHookRequest request) {
        String login = request.getData().getContext().getUser().getProfile().getLogin();
        Person p = personRepository.findByEmail(login);

        TokenHookResponse response = new TokenHookResponse();
        if (p != null) {
            IDTokenPatchResponse idTokenPatchResponse = new IDTokenPatchResponse();
            idTokenPatchResponse.getValue().add(
                new TokenPatchResponse.Value("add", "/claims/beers", transformBeers(p.getFavoriteBeers()))
            );
            response.getCommands().add(idTokenPatchResponse);
        }
        return response;
    }

    private List<String> transformBeers(List<Beer> beers) {
        return beers.stream().map(Beer::getName).collect(Collectors.toList());
    }

    @PostMapping("/create-hook")
    public @ResponseBody
    Map<String, Object> createHook(@RequestBody CreateHookRequest createHookRequest) throws Exception {
        return inlineHookService.createInlineHook(createHookRequest);
    }
}
