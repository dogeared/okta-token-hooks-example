package com.okta.examples.apiamhooks.controller;

import com.okta.examples.apiamhooks.model.CreateHookRequest;
import com.okta.examples.apiamhooks.service.InlineHookService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/hooks")
public class HooksController {

    private InlineHookService inlineHookService;

    public HooksController(InlineHookService inlineHookService) {
        this.inlineHookService = inlineHookService;
    }

    @PostMapping("/apiam")
    public Map<String, Object> apiam(@RequestBody Map<String, Object> request) {
        int i = 0;
        return Map.of("a", "b");
    }

    @PostMapping("/create-hook")
    public @ResponseBody
    Map<String, Object> createHook(@RequestBody CreateHookRequest createHookRequest) throws Exception {
        return inlineHookService.createInlineHook(createHookRequest);
    }
}
