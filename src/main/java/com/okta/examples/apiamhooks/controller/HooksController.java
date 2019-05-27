package com.okta.examples.apiamhooks.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;

@RestController
@RequestMapping("/api/hooks")
public class HooksController {

    @Value("#{ @environment['okta.hooks.id'] }")
    private String hooksId;

    @Value("#{ @environment['okta.hooks.password'] }")
    private String hooksPassword;

    @Value("#{ @environment['okta.hooks.apiToken'] }")
    private String apiToken;

    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {};

    private String createHookTemplate;
    private RestTemplate restTemplate;


    private static final String INLINE_HOOKS_URI = "/api/v1/inlineHooks";
    private static final Logger LOG = LoggerFactory.getLogger(HooksController.class);

    @PostConstruct
    public void setup() throws Exception {
        File file = ResourceUtils.getFile("classpath:hooks.json");
        createHookTemplate = new String(Files.readAllBytes(file.toPath()));

        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    @PostMapping("/apiam")
    public Map<String, Object> apiam(@RequestBody Map<String, Object> request) {
        int i = 0;
        return Map.of("a", "b");
    }

    @PostMapping("/create-hook")
    public @ResponseBody
    Map<String, Object> createHook(@RequestBody CreateHookRequest createHookRequest) throws Exception {
        // replace variables
        String template =createHookTemplate
            .replace("{{okta-org}}", createHookRequest.getOktaOrg())
            .replace("{{ngrok-url}}", createHookRequest.getHookUrl())
            .replace("{{basic-auth}}", encodeBasicAuth(hooksId, hooksPassword));
        Map<String, Object> templateMap = mapper.readValue(template, mapTypeReference);

        // make rest call
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(templateMap, createHeaders());
        String resp =
            restTemplate.postForObject(createHookRequest.getOktaOrg() + INLINE_HOOKS_URI, entity, String.class);
        return mapper.readValue(resp, mapTypeReference);
    }

    private String encodeBasicAuth(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        return new String(encodedAuth);
    }

    private HttpHeaders createHeaders() {
        return new HttpHeaders() {
            {
                String authHeader = "SSWS " + apiToken;
                set("Authorization", authHeader);
            }
        };
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateHookRequest {

        private String oktaOrg;
        private String hookUrl;
    }
}
