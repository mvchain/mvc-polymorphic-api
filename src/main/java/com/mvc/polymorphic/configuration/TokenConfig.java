package com.mvc.polymorphic.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "mvc")
@Component
public class TokenConfig {

    public static final String ENV_LOCAL = "local";
    public static final String ENV_TEST = "test";
    public static final String ENV_PROD = "prod";

    private Map<String, String> env = new HashMap<>();

    private Map<String, Map<String, String>> url = new HashMap<>();


}
