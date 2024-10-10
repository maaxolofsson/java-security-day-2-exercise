package com.booleanuk.api.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class MessageResponse extends Response<Map<String, String>> {

    public MessageResponse(String message) {
        super();
        this.status = "success";
        Map<String, String> reply = new HashMap<>();
        reply.put("message", message);
        this.data = reply;
    }
}