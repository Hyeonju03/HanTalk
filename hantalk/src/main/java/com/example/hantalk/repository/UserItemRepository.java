package com.example.hantalk.repository;

import com.example.hantalk.entity.Item;
import com.example.hantalk.entity.User_Items;
import com.example.hantalk.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserItemRepository extends JpaRepository<User_Items, Long> {

    boolean existsByUsersAndItem(Users users, Item item);

    List<User_Items> findByUsers(Users users);
}

