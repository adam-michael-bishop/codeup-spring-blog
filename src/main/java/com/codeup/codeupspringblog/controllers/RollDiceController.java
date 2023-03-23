package com.codeup.codeupspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class RollDiceController {
    private double guesses = 0;
    private double correctGuesses = 0;

    @GetMapping("/roll-dice")
    public String rollDiceGuessView() {
        return "roll-dice";
    }

    @GetMapping("/roll-dice/{guess}")
    public String rollDiceResultView(@PathVariable int guess, Model model) {
        Random rand = new Random();
        int result = rand.nextInt(6) + 1;
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("guess", guess);
        attributes.put("result", result);
        attributes.put("percentCorrect", calculateCorrectGuessPercentage(guess, result));
        model.addAllAttributes(attributes);
        return "roll-dice";
    }

    private double calculateCorrectGuessPercentage(int guess, int result) {
        guesses++;
        if (guess == result) {
            correctGuesses++;
        }
        return correctGuesses / guesses * 100;
    }
}
