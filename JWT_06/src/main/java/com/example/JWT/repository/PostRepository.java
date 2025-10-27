package com.example.JWT.repository;

import com.example.JWT.dto.PostDto;
import com.example.JWT.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
