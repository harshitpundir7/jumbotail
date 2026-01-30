package com.jumbotail.shipping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving the landing page.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
}
