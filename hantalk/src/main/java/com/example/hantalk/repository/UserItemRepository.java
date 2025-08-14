package com.example.hantalk.repository;

import com.example.hantalk.entity.Item;
import com.example.hantalk.entity.User_Items;
import com.example.hantalk.entity.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserItemRepository extends JpaRepository<User_Items, Long> {

    boolean existsByUsersAndItem(Users users, Item item);

    // Users 객체로 조회
    @EntityGraph(attributePaths = "item")
    List<User_Items> findByUsers(Users users);

    // userNo로 바로 조회 (이거 쓰면 Users 조회 생략 가능)
    @EntityGraph(attributePaths = "item")
    List<User_Items> findByUsers_UserNo(Integer userNo);
}


