package com.codeup.codeupspringblog.controllers;

import com.codeup.codeupspringblog.models.Post;
import com.codeup.codeupspringblog.repositories.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Controller
public class PostController {
    private final PostRepository postRepo;

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
        post.ifPresent(value -> model.addAttribute("post", value));
        return "posts/show";
    }

    @GetMapping("/posts/create")
    @ResponseBody
    public String postsCreatePageDescription() {
        return "view the form for creating a post";
    }

    @PostMapping("/posts/create")
    @ResponseBody
    public String createPostDescription() {
        return "create a new post";
    }
}
