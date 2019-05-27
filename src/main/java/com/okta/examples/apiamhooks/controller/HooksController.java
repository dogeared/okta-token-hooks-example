package com.okta.examples.apiamhooks.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hooks")
public class HooksController {

    @GetMapping("/apiam")
    public String apiam() {
        return "HELLO";
    }
}
