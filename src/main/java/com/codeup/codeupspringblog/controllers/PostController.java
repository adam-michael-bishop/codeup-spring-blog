package com.codeup.codeupspringblog.controllers;

import com.codeup.codeupspringblog.models.Post;
import com.codeup.codeupspringblog.repositories.PostRepository;
import com.codeup.codeupspringblog.repositories.UserRepository;
import com.codeup.codeupspringblog.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;

    @GetMapping
    public String allPostsView(Model model) {
        List<Post> posts = postRepo.findAll();
        Post post = new Post();
        model.addAttribute("posts", posts);
        model.addAttribute("post", post);
        return "posts/index";
    }

    @GetMapping("/{id}")
    public String postView(@PathVariable Long id, Model model) {
        model.addAttribute("editing", true);
        try {
            postRepo.findById(id).ifPresent(post -> model.addAttribute("post", post));
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
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
    @ResponseStatus(HttpStatus.OK)
    public void savePost(@ModelAttribute Post post) {
        try {
            userRepo.findById(1L).ifPresent(post::setUser);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
            //replace with error page
//            return "redirect:/posts";
        }
        postRepo.save(post);
//        return "redirect:/posts";
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public void createPost(@ModelAttribute Post post) {
        try {
            userRepo.findById(1L).ifPresent(post::setUser);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
        postRepo.save(post);
        emailService.prepareAndSend(post, "New post created: \"" + post.getTitle() + "\"", "A new post has been added by your account: \n" + post.getBody());
    }

    //Not using postsEditView anymore, it can still be accessed by url
    @Deprecated
    @GetMapping("/{id}/edit")
    public String postsEditView(Model model, @PathVariable Long id) {
        try {
            postRepo.findById(id).ifPresent(post -> model.addAttribute("post", post));
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
            //replace with error page
            return "redirect:/posts";
        }
        return "posts/old-edit";
    }
}
