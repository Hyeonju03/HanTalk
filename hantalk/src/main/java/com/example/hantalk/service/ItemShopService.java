package com.example.hantalk.service;

import com.example.hantalk.entity.Item;
import com.example.hantalk.entity.User_Items;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.ItemRepository;
import com.example.hantalk.repository.UserItemRepository;
import com.example.hantalk.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemShopService {

    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;

    public ItemShopService(ItemRepository itemRepository, UserItemRepository userItemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userItemRepository = userItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean purchaseItem(Users user, int itemId) {
        Item item = itemRepository.findById((long) itemId)
                .orElseThrow(() -> new RuntimeException("아이템이 존재하지 않습니다."));

        // 이미 보유한 아이템인지 확인
        boolean alreadyOwned = userItemRepository.existsByUsersAndItem(user, item);
        if (alreadyOwned) return false;

        // 포인트 부족 확인
        if (user.getPoint() < item.getPrice()) return false;

        // 포인트 차감
        user.setPoint(user.getPoint() - item.getPrice());
        userRepository.save(user);

        // User_Items 저장
        User_Items userItem = new User_Items();
        userItem.setUsers(user);
        userItem.setItem(item);
        userItem.setAcquiredAt(LocalDateTime.now());
        userItem.setEquipped(false);
        userItemRepository.save(userItem);

        return true;
    }

    public List<Map<String, Object>> getAllItemsWithUserInfo(Users user) {
        List<Item> allItems = itemRepository.findAll();
        List<User_Items> ownedItems = userItemRepository.findByUsers(user);
        Set<Integer> ownedItemIds = ownedItems.stream()
                .map(ui -> ui.getItem().getItemId())
                .collect(Collectors.toSet());

        // item + 보유 여부 포함된 구조로 반환
        return allItems.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("item", item);
            map.put("owned", ownedItemIds.contains(item.getItemId()));
            return map;
        }).collect(Collectors.toList());
    }
}