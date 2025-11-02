package com.example.JWT.controller;

import com.example.JWT.dto.PostDto;
import com.example.JWT.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @PostMapping("/create-post")
    @PreAuthorize("hasRole('ADMIN')")
    public PostDto createNewPost(@RequestBody PostDto inputPost) {
        return postService.createNewPost(inputPost);
    }

}
