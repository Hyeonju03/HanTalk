package com.example.hantalk.repository;

import com.example.hantalk.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.profileImage = :imageName WHERE u.userNo = :userNo")
    void updateProfileImage(@Param("userNo") int userNo, @Param("imageName") String imageName);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.profileFrame = :frameName WHERE u.userNo = :userNo")
    void updateProfileFrame(@Param("userNo") int userNo, @Param("frameName") String frameName);

}