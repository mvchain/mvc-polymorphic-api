package com.mvc.polymorphic.controller;

import com.mvc.polymorphic.configuration.TokenConfig;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("Some Demos")
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private TokenConfig tokenConfig;

    @GetMapping("/config")
    public Object config() {
        return tokenConfig.getUrl();
    }
}
