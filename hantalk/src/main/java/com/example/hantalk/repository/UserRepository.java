package com.example.hantalk.repository;

import com.example.hantalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    boolean existsByUserId(String userId);

    Optional<User> findByUserId(String userid);
}
