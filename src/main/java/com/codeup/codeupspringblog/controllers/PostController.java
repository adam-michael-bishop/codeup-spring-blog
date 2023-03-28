package com.codeup.codeupspringblog.controllers;

import com.codeup.codeupspringblog.models.Post;
import com.codeup.codeupspringblog.models.User;
import com.codeup.codeupspringblog.repositories.PostRepository;
import com.codeup.codeupspringblog.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    @GetMapping
    public String allPostsView(Model model) {
        List<Post> posts = postRepo.findAll();
        model.addAttribute("posts", posts);
        return "posts/index";
    }

    @GetMapping("/{id}")
    public String postView(@PathVariable Long id, Model model) {
        Optional<Post> post = postRepo.findById(id);
        try {
            post.ifPresent(newPost -> {
                model.addAttribute("post", newPost);
                Optional<User> user = userRepo.findById(newPost.getUser().getId());
                user.ifPresent(newUser -> model.addAttribute("user", newUser));
            });
        } catch (NullPointerException e) {
            System.out.println(e);
            //replace with error page
            return "posts/show";
        }
        return "posts/show";
    }

    @GetMapping("/create")
    public String postsCreateView(Model model) {
        Post post = new Post();
        model.addAttribute("post", post);
        return "posts/create";
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute Post post) {
        try {
            userRepo.findById(1L).ifPresent(post::setUser);
        } catch (NullPointerException e) {
            System.out.println(e);
            //replace with error page
            return "redirect:/posts";
        }
        postRepo.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String postsEditView(Model model, @PathVariable Long id) {
        try {
            postRepo.findById(id).ifPresent(post -> model.addAttribute("post", post));
        } catch (NullPointerException e) {
            System.out.println(e);
            //replace with error page
            return "redirect:/posts";
        }
        return "posts/edit";
    }
}
