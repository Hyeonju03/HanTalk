package com.example.hantalk.controller;

import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.UsersRepository;
import com.example.hantalk.service.ItemShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/item")
public class ItemShopController {

    private final ItemShopService itemShopService;
    private final UsersRepository userRepository;

    @GetMapping
    public String showShop(Model model, Principal principal) {
        Users user = userRepository.findByUserId(principal.getName()).orElseThrow();
        model.addAttribute("point", user.getPoint());
        model.addAttribute("items", itemShopService.getAllItemsWithUserInfo(user));
        return "shop";
    }

    @PostMapping("/purchase")
    public String purchaseItem(@RequestParam("itemId") int itemId, Principal principal, RedirectAttributes redirectAttributes) {
        Users user = userRepository.findByUserId(principal.getName()).orElseThrow();
        boolean result = itemShopService.purchaseItem(user, itemId);

        if (result) {
            redirectAttributes.addFlashAttribute("message", "아이템을 성공적으로 구매했습니다!");
        } else {
            redirectAttributes.addFlashAttribute("error", "구매 실패: 포인트 부족 혹은 이미 보유한 아이템입니다.");
        }

        return "redirect:/shop";
    }
}

