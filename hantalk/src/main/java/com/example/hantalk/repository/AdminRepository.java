package com.example.hantalk.repository;

import com.example.hantalk.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer>{
    boolean existsByAdminId(String user_id);

    Optional<Admin> findByAdminId(String user_id);

    boolean existsByEmail(String email);
}
