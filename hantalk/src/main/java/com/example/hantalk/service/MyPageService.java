package com.example.hantalk.service;

import com.example.hantalk.dto.UsersDTO;
import com.example.hantalk.entity.Item;
import com.example.hantalk.entity.User_Items;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.ItemRepository;
import com.example.hantalk.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UsersRepository usersRepository;
    private final ItemRepository itemRepository;

    public UsersDTO getMyPageInfo(Integer userNo) {
        Users user = usersRepository.findById(userNo)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return toDTO(user);
    }

    // 수정
    public void updateMyPage(UsersDTO dto) {
        Users user = usersRepository.findById(dto.getUserNo())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        user.setNickname(dto.getNickname());
        user.setPassword(encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setProfileImage(dto.getProfileImage());

        usersRepository.save(user);
    }

    // 탈퇴
    public void deactivateUser(Integer userNo) {
        Users user = usersRepository.findById(userNo)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        user.setStatus("1"); // 비활성화
        usersRepository.save(user);
    }

//    private Users toEntity(UsersDTO dto) {
//        if (dto == null) return null;
//
//        Users users = new Users();
//        users.setUserNo(dto.getUserNo());
//        users.setUserId(dto.getUserId());
//        users.setName(dto.getName());
//        users.setEmail(dto.getEmail());
//        users.setPassword(dto.getPassword());
//        users.setNickname(dto.getNickname());
//        users.setProfileImage(dto.getProfileImage());
//        users.setJoinDate(dto.getJoinDate());
//        users.setBirth(dto.getBirth());
//        users.setStatus(dto.getStatus());
//        users.setPoint(dto.getPoint());
//        return users;
//    }

    private UsersDTO toDTO(Users users) {
        if (users == null) return null;

        UsersDTO dto = new UsersDTO();
        dto.setUserNo(users.getUserNo());
        dto.setUserId(users.getUserId());
        dto.setName(users.getName());
        dto.setEmail(users.getEmail());
        dto.setPassword(users.getPassword());
        dto.setNickname(users.getNickname());
        dto.setProfileImage(users.getProfileImage());
        dto.setJoinDate(users.getJoinDate());
        dto.setBirth(users.getBirth());
        dto.setStatus(users.getStatus());
        dto.setPoint(users.getPoint());
        return dto;
    }
    private String encode(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public List<User_Items> getUserOwnedItems(Integer userNo) {
        Users user = usersRepository.findById(userNo)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return user.getUserItemsList();
    }

    @Transactional
    public void applyProfileImage(int userNo, int itemId) {
        Users user = usersRepository.findById(userNo).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();

        user.setProfileImage(item.getItemImage());
        usersRepository.save(user);
    }

    public List<User_Items> getUserItems(Integer userNo) {
        Users user = usersRepository.findById(userNo)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        return user.getUserItemsList();
    }

}