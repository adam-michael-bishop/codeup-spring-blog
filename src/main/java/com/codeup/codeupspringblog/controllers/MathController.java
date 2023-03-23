package com.codeup.codeupspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MathController {

    @GetMapping("/add/{a}/and/{b}")
    @ResponseBody
    public String add(@PathVariable String a, @PathVariable String b) {
        try {
            return Integer.toString(Integer.parseInt(a) + Integer.parseInt(b));
        } catch (NumberFormatException e) {
            return "Unable to parse integer from path variable.<br>" + e.getMessage();
        }
    }

    @GetMapping("/subtract/{a}/from/{b}")
    @ResponseBody
    public String subtract(@PathVariable String a, @PathVariable String b) {
        try {
            return Integer.toString(Integer.parseInt(b) - Integer.parseInt(a));
        } catch (NumberFormatException e) {
            return "Unable to parse integer from path variable.<br>" + e.getMessage();
        }
    }

    @GetMapping("/multiply/{a}/and/{b}")
    @ResponseBody
    public String multiply(@PathVariable String a, @PathVariable String b) {
        try {
            return Integer.toString(Integer.parseInt(a) * Integer.parseInt(b));
        } catch (NumberFormatException e) {
            return "Unable to parse integer from path variable.<br>" + e.getMessage();
        }
    }

    @GetMapping("/divide/{a}/by/{b}")
    @ResponseBody
    public String divide(@PathVariable String a, @PathVariable String b) {
        try {
            return Integer.toString(Integer.parseInt(a) / Integer.parseInt(b));
        } catch (ArithmeticException e) {
            return "Arithmetic exception: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Unable to parse integer from path variable.<br>" + e.getMessage();
        }
    }
}
