package com.luv2code.spring_boot_library.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.Base64;
import java.util.Map;

public class JwtParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String header;
    private final String payload;
    @Getter
    private final Map<String, Object> payloadMap;
    private final String signature;

    public JwtParser(String token) {

        String cleanToken = token.replace("Bearer ", "");
        String[] chunks = cleanToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        header = new String(decoder.decode(chunks[0]));
        payload = new String(decoder.decode(chunks[1]));
        payloadMap = parseJsonToMap(payload);
        signature = new String(decoder.decode(chunks[2]));
    }

    private Map<String, Object> parseJsonToMap(String json) {

        try {
            return MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserEmailFromToken() {

        return (String) payloadMap.get("sub");
    }

    public String getUserType() {

        return (String) payloadMap.get("userType");
    }

}
