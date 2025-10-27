package com.example.JWT.service;

import com.example.JWT.dto.PostDto;
import com.example.JWT.entity.Post;
import com.example.JWT.exceptions.ResourceNotFoundException;
import com.example.JWT.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService{

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;


    public List<PostDto> getAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(postEntity -> modelMapper.map(postEntity, PostDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public PostDto createNewPost(PostDto inputPost) {
        Post postEntity = modelMapper.map(inputPost,Post.class);
        Post post = postRepository.save(postEntity);
        return modelMapper.map(post,PostDto.class);
    }


    public PostDto getPostById(Long postId) {
        Post postEntity = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id "+postId));
        return modelMapper.map(postEntity, PostDto.class);
    }
}
