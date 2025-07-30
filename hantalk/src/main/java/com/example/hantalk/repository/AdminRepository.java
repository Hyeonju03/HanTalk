package com.example.hantalk.repository;

import com.example.hantalk.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer>{
    boolean existsByUserId(String userId);

    Optional<Admin> findByUserId(String userid);
}
