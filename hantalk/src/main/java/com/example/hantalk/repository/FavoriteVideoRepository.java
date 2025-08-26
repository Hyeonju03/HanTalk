package com.example.hantalk.repository;

import com.example.hantalk.entity.Favorite_video;
import com.example.hantalk.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteVideoRepository extends JpaRepository<Favorite_video, Integer> {
    // 특정 사용자의 찜 목록에서 특정 비디오를 찾습니다.
    Optional<Favorite_video> findByUsersUserNoAndVideoVideoId(int userNo, int videoId);

    // 특정 사용자의 찜한 영상 목록을 조회합니다.
    List<Favorite_video> findByUsersUserNo(int userNo);
}