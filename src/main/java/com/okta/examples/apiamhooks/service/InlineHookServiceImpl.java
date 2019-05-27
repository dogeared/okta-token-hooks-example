package com.okta.examples.apiamhooks.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.examples.apiamhooks.model.CreateHookRequest;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;

@Service
public class InlineHookServiceImpl implements InlineHookService {

    @Value("#{ @environment['okta.hooks.id'] }")
    private String hooksId;

    @Value("#{ @environment['okta.hooks.password'] }")
    private String hooksPassword;

    @Value("#{ @environment['okta.hooks.apiToken'] }")
    private String apiToken;

    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<>() {};

    private String createHookTemplate;
    private RestTemplate restTemplate;

    private static final String INLINE_HOOKS_URI = "/api/v1/inlineHooks";

    @PostConstruct
    public void setup() throws Exception {
        File file = ResourceUtils.getFile("classpath:hooks.json");
        createHookTemplate = new String(Files.readAllBytes(file.toPath()));

        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public Map<String, Object> createInlineHook(CreateHookRequest createHookRequest) throws Exception {
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
}
