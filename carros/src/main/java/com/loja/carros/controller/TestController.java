package com.loja.carros.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class TestController {

    @GetMapping("/status")
    public String getStatus() {
        return "ONLINE";
    }
    

}
