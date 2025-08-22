package com.example.hantalk.service;

import com.example.hantalk.dto.ItemDTO;
import com.example.hantalk.dto.UsersDTO;
import com.example.hantalk.entity.Item;
import com.example.hantalk.entity.User_Items;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.ItemRepository;
import com.example.hantalk.repository.UserItemRepository;
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
    private final UserItemRepository userItemRepository;

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
        if(!dto.getPassword().isEmpty()){
            user.setPassword(encode(dto.getPassword()));
        }
        user.setEmail(dto.getEmail());
        user.setProfileImage(dto.getProfileImage());

        usersRepository.save(user);
    }

    // 이 메서드는 컨트롤러에 맞춰 비밀번호 검증과 탈퇴를 동시에 처리합니다.
    @Transactional
    public void deactivateUser(Integer userNo, String password) {
        // 1. 사용자 엔티티 조회
        Users user = usersRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 입력된 비밀번호를 해시하여 DB의 비밀번호와 비교
        String encodedPassword = encode(password);
        if (!user.getPassword().equals(encodedPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 비밀번호가 일치하면 사용자 상태를 비활성화합니다.
        user.setStatus("SignOut"); // 비활성화
        usersRepository.save(user);
    }

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
        dto.setProfileFrame(users.getProfileFrame());
        dto.setJoinDate(users.getJoinDate());
        dto.setBirth(users.getBirth());
        dto.setStatus(users.getStatus());
        dto.setPoint(users.getPoint());
        return dto;
    }

    // SHA-256 암호화 메서드
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


    /** 구매한 아이템 목록 (항상 최신 Item 이미지 반영) */
    public List<ItemDTO> getUserOwnedItems(Integer userNo) {
        List<User_Items> userItems = userItemRepository.findByUsers_UserNo(userNo);

        return userItems.stream()
                .map(userItem -> {
                    Item item = userItem.getItem(); // 항상 최신 Item 엔티티 참조
                    ItemDTO dto = new ItemDTO();
                    dto.setItemId(item.getItemId());
                    dto.setItemName(item.getItemName());
                    dto.setItemDescription(item.getItemDescription());
                    dto.setPrice(item.getPrice());
                    dto.setItemImage(item.getItemImage()); // 최신 이미지 반영
                    dto.setItemType(item.getItemType());
                    return dto;
                })
                .toList();
    }


    public List<User_Items> getUserItems(Integer userNo) {
        Users user = usersRepository.findById(userNo)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        return user.getUserItemsList();
    }

    public void applyProfileImageByImageName(int userNo, String imageName) {
        usersRepository.updateProfileImage(userNo, imageName);
    }

    public void applyProfileFrameByImageName(int userNo, String frameName) {
        usersRepository.updateProfileFrame(userNo, frameName);
    }
}
