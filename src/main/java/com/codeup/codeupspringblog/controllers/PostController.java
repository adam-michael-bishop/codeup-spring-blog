package com.codeup.codeupspringblog.controllers;

import com.codeup.codeupspringblog.models.Post;
import com.codeup.codeupspringblog.models.User;
import com.codeup.codeupspringblog.repositories.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Controller
public class PostController {
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    @GetMapping("/posts")
    public String allPostsView(Model model) {
        List<Post> posts = postRepo.findAll();
//        posts.add(new Post("My First Post", "This is exciting, blogging much fun."));
//        posts.add(new Post("My Last Post", "Screw this, y'all are wild."));
        model.addAttribute("posts", posts);
        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String postView(@PathVariable Long id, Model model) {
        Optional<Post> post = postRepo.findById(id);
        //        Post post = new Post("Placeholder Post", "This post is a placeholder that will eventually show the post with the id: " + id);
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

    @GetMapping("/posts/create")
    public String postsCreateView() {
        return "posts/create";
    }

    @PostMapping("/posts/create")
    public String createPost(@RequestParam("post-title") String title, @RequestParam("post-body") String body) {
        Post post = new Post();
        post.setTitle(title);
        post.setBody(body);
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
}
