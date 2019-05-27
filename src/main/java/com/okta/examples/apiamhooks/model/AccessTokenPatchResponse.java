package com.okta.examples.apiamhooks.model;

import lombok.Data;

@Data
public class AccessTokenPatchResponse extends TokenPatchResponse {

    public AccessTokenPatchResponse() {
        super.setType("com.okta.access.patch");
    }
}
