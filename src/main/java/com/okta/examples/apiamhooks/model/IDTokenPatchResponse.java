package com.okta.examples.apiamhooks.model;

import lombok.Data;

@Data
public class IDTokenPatchResponse extends TokenPatchResponse {

    public IDTokenPatchResponse() {
        super.setType("com.okta.identity.patch");
    }
}
