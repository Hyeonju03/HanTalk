package com.example.hantalk.repository;

import com.example.hantalk.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    boolean existsByUserId(String userId);

    Optional<Users> findByUserId(String userId);

    Optional<Users> findByUserNo(Integer userNo);

    Optional<Users> findByName(String name);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByNameAndEmail(String name, String email);

    boolean existsByEmail(String email);
}
