package com.codeup.codeupspringblog;

import com.codeup.codeupspringblog.models.Post;
import com.codeup.codeupspringblog.models.User;
import com.codeup.codeupspringblog.repositories.PostRepository;
import com.codeup.codeupspringblog.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertNotNull;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = CodeupSpringBlogApplication.class)
public class CodeupSpringBlogApplicationTests {
    private User testUser;
    private HttpSession httpSession;

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepo;

    @Autowired
    PostRepository postRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() throws Exception {

        testUser = userRepo.findByUsername("testUser");

        // Creates the test user if not exists
        if (testUser == null) {
            User newUser = new User();
            newUser.setUsername("testUser");
            newUser.setPassword(passwordEncoder.encode("pass"));
            newUser.setEmail("testUser@codeup.com");
            testUser = userRepo.save(newUser);
        }

        // Throws a Post request to /login and expect a redirection to the posts index page after being logged in
        httpSession = this.mvc.perform(post("/login").with(csrf())
                        .param("username", "testUser")
                        .param("password", "pass"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/posts"))
                .andReturn()
                .getRequest()
                .getSession();

        // Creates a post so that it can be edited by the test later
        this.mvc.perform(post("/posts/create").with(csrf())
                        .session((MockHttpSession) httpSession)
                        .param("title", "test")
                        .param("body", "testing out the blog post"))
                .andExpect(status().is2xxSuccessful());
    }

    @After
    public void clean() throws Exception {
        postRepo.deleteAll(postRepo.findByUser(testUser));
    }

    @Test
    public void contextLoads() {
        // Sanity Test, just to make sure the MVC bean is working
        assertNotNull(mvc);
    }

    @Test
    public void testIfUserSessionIsActive() throws Exception {
        // It makes sure the returned session is not null
        assertNotNull(httpSession);
    }

    @Test
    public void testCreatePost() throws Exception {
        // Makes a Post request to /posts/create and expect an http 200 status message
        this.mvc.perform(post("/posts/create").with(csrf())
                        .session((MockHttpSession) httpSession)
                        // Add all the required parameters to your request like this
                        .param("title", "test")
                        .param("body", "testing out the blog post"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testShowPost() throws Exception {

        Post existingPost = postRepo.findAll().get(0);

        // Makes a Get request to /posts/{id} and expect a redirection to the Ad show page
        this.mvc.perform(get("/posts/" + existingPost.getId())
                        .session((MockHttpSession) httpSession)
                        .with(csrf()))
                .andExpect(status().isOk())
                // Test the dynamic content of the page
                .andExpect(content().string(containsString(existingPost.getBody())));
    }

    @Test
    public void testPostsIndex() throws Exception {
        Post existingPost = postRepo.findAll().get(0);

        // Makes a Get request to /posts and verifies that we get some static text from posts/index.html template and at least the title from the first post is present in the template.
        this.mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                // Test the static content of the page
                .andExpect(content().string(containsString("All Posts")))
                // Test the dynamic content of the page
                .andExpect(content().string(containsString(existingPost.getTitle())));
    }

    @Test
    public void testEditPost() throws Exception {
        // Gets the first post created by the test user
        Post existingPost = postRepo.findByUser(testUser).get(0);

        //Values used to edit post title and body
        String editedTitle = "edited title";
        String editedBody = "edited body";

        // Makes a Post request to /posts/save and expect a 200 http status message
        this.mvc.perform(post("/posts/save")
                        .with(csrf())
                        .session((MockHttpSession) httpSession)
                        .param("title", editedTitle)
                        .param("body", editedBody)
                        .param("id", existingPost.getId().toString()))
                .andExpect(status().is2xxSuccessful());

        // Makes a GET request to /ads/{id} and expect a redirection to the Ad show page
        this.mvc.perform(get("/posts/" + existingPost.getId()))
                .andExpect(status().isOk())
                // Test the dynamic content of the page
                .andExpect(content().string(containsString(editedTitle)))
                .andExpect(content().string(containsString(editedBody)));
    }

    @Test
    public void testDeletePost() throws Exception {
        // Creates a test Ad to be deleted
        this.mvc.perform(
                        post("/posts/create").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("title", "ad to be deleted")
                                .param("body", "won't last long"))
                .andExpect(status().is2xxSuccessful());

        // Get the recent Post that matches the title
        Post existingPost = postRepo.findByTitle("ad to be deleted");

        // Makes a Post request to /posts/{id}/delete and expect a redirection to the Posts index
        this.mvc.perform(
                        post("/posts/" + existingPost.getId() + "/delete").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("id", String.valueOf(existingPost.getId())))
                .andExpect(status().is3xxRedirection());
    }
}
