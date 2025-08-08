package com.example.hantalk.service;

import com.example.hantalk.entity.Item;
import com.example.hantalk.entity.User_Items;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.ItemRepository;
import com.example.hantalk.repository.UserItemRepository;
import com.example.hantalk.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemShopService {

    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UsersRepository usersRepository;

    // 사용자에게 보여줄 전체 아이템 목록 + 보유 여부 체크
    public List<Item> getAllItemsWithUserInfo(Users user) {
        List<Item> items = itemRepository.findAll();
        // 예: 사용자 보유 아이템과 비교하여 마크업 등 가능
        return items;
    }

    // 관리자 - 전체 아이템 목록
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // 단일 아이템 조회
    public Item getItemById(int id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 아이템이 존재하지 않습니다. ID: " + id));
    }

    // 아이템 추가
    public void addItem(Item item) {
        itemRepository.save(item);
    }

    // 아이템 수정
    public void updateItem(Item item) {
        itemRepository.save(item); // ID가 존재하면 수정
    }

    // 아이템 삭제
    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }

    // 아이템 구매 처리
    @Transactional
    public boolean purchaseItem(Users user, int itemId) {
        Item item = getItemById(itemId);

        if (userItemRepository.existsByUsersAndItem(user, item)) {
            return false;
        }

        if (user.getPoint() < item.getPrice()) {
            return false;
        }

        // 포인트 차감
        user.setPoint(user.getPoint() - item.getPrice());

        // 아이템 저장
        User_Items userItem = new User_Items();
        userItem.setUsers(user);
        userItem.setItem(item);
        userItem.setAcquiredAt(LocalDateTime.now());
        userItem.setEquipped(false);
        userItemRepository.save(userItem);

        //  user 저장 추가
        usersRepository.save(user);

        return true;
    }


    @Transactional
    public void givePointToUser(Users user, int amount) {
        user.setPoint(user.getPoint() + amount);
        usersRepository.save(user);
    }
}