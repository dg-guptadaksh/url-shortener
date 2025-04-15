package com.example.urlshortener;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/urlshortner")
    public String hello() {
        return "Welcome to URL Shortener!";
    }
}
