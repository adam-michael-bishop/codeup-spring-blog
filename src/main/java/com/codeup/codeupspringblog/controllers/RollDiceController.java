package com.codeup.codeupspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class RollDiceController {
    private final Random rand = new Random();

    @GetMapping("/roll-dice")
    public String rollDiceGuessView() {
        return "roll-dice";
    }

    @PostMapping("/roll-dice")
    public String rollDiceResultView(@RequestParam(defaultValue = "1") int guess, @RequestParam(defaultValue = "1") int numberOfRolls, Model model) {
        Map<String, Object> attributes = new HashMap<>();
        ArrayList<Integer> results = rollDice(numberOfRolls);
        attributes.put("guess", guess);
        attributes.put("results", results);
        attributes.put("numberOfRolls", numberOfRolls);
        attributes.put("percentCorrect", calculateCorrectGuessPercentage(guess, results));
        model.addAllAttributes(attributes);
        return "roll-dice";
    }

    private double calculateCorrectGuessPercentage(int guess, ArrayList<Integer> results) {
        double guesses = 0;
        double correctGuesses = 0;
        for (Integer result : results) {
            guesses++;
            if (guess == result) {
                correctGuesses++;
            }
        }
        return correctGuesses / guesses * 100;
    }

    private ArrayList<Integer> rollDice(int numberOfRolls) {
        ArrayList<Integer> results = new ArrayList<>();
        for (int i = 0; i < numberOfRolls; i++) {
            results.add(rand.nextInt(6) + 1);
        }
        return results;
    }
}
