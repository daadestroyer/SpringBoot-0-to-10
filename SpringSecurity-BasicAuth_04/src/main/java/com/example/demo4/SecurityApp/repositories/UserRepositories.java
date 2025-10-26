package com.example.demo4.SecurityApp.repositories;

import com.example.demo4.SecurityApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositories extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
}
