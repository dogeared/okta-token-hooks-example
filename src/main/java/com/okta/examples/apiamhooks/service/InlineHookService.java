package com.okta.examples.apiamhooks.service;

import com.okta.examples.apiamhooks.model.hooks.CreateHookRequest;

import java.util.Map;

public interface InlineHookService {

    Map<String, Object> createInlineHook(CreateHookRequest createHookRequest) throws Exception;
}
