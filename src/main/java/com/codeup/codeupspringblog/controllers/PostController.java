package com.codeup.codeupspringblog.controllers;

import com.codeup.codeupspringblog.models.Post;
import com.codeup.codeupspringblog.models.User;
import com.codeup.codeupspringblog.repositories.PostRepository;
import com.codeup.codeupspringblog.repositories.UserRepository;
import com.codeup.codeupspringblog.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
        try {
            postRepo.findById(id).ifPresent(post -> {
                model.addAttribute("post", post);
                if (post.getUser().getId().equals(getLoggedInUser().getId())) {
                    model.addAttribute("editing", true);
                }
            });
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
        return "posts/old-create";
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.OK)
    public void savePost(@ModelAttribute Post post) {
        try {
            post.setUser(getLoggedInUser());
            postRepo.save(post);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            //replace with error page
        }
    }

    private static User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            return new User(-1L, "AnonymousUser", null, null, null);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public void createPost(@ModelAttribute Post post) {
        try {
            userRepo.findById(getLoggedInUser().getId()).ifPresent(post::setUser);
            postRepo.save(post);
            emailService.prepareAndSend(post, "New post created: \"" + post.getTitle() + "\"", "A new post has been added by your account: \n" + post.getBody());
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }

    //Not using postsEditView anymore, it can still be accessed by url
    @GetMapping("/{id}/edit")
    public String postsEditView(Model model, @PathVariable Long id) {
        try {
            postRepo.findById(id).ifPresent(post -> {
                if (!post.getUser().getId().equals(getLoggedInUser().getId())) {
                    throw new RuntimeException("User does not have permissions to edit post");
                }
                model.addAttribute("post", post);
            });
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
            //replace with error page
            return "redirect:/posts";
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return "redirect:/posts/" + id;
        }
        return "posts/old-edit";
    }
}
