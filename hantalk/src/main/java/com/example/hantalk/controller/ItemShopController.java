package com.example.hantalk.controller;

import com.example.hantalk.entity.Item;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.UsersRepository;
import com.example.hantalk.service.ItemShopService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemShopController {

    private final ItemShopService itemShopService;
    private final UsersRepository userRepository;

    // 아이템 상점 페이지 조회
    @GetMapping("/shop")
    public String showShop(HttpSession session, Model model) {
        // 사용자 or 관리자 체크
        Integer userNo = (Integer) session.getAttribute("userNo");
        String adminId = (String) session.getAttribute("adminId");

        if (userNo == null && adminId == null) {
            return "redirect:/user/login"; // 로그인 안 되어있으면 리디렉션
        }

        // 관리자일 경우 (adminId가 존재하면)
        if (adminId != null) {
            model.addAttribute("point", 0); // 관리자에게 포인트는 의미 없음
            model.addAttribute("items", itemShopService.getAllItems()); // 모든 아이템 목록만 보여줌
            model.addAttribute("isAdmin", true); // 뷰에서 분기용
            return "item/shop";
        }

        // 사용자일 경우
        Users user = userRepository.findById(userNo).orElseThrow();
        model.addAttribute("point", user.getPoint());
        model.addAttribute("items", itemShopService.getAllItemsWithUserInfo(user));
        model.addAttribute("isAdmin", false); // 뷰에서 분기용

        return "item/shop";
    }


    // 아이템 구매 처리
    @PostMapping("/purchase")
    public String purchaseItem(@RequestParam("itemId") int itemId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            return "redirect:/user/login";
        }
        Users user = userRepository.findById(userNo).orElseThrow();
        boolean result = itemShopService.purchaseItem(user, itemId);

        if (result) {
            redirectAttributes.addFlashAttribute("message", "아이템을 성공적으로 구매했습니다!");
        } else {
            redirectAttributes.addFlashAttribute("error", "구매 실패: 포인트 부족 또는 이미 보유한 아이템입니다.");
        }

        return "redirect:/item/shop";
    }

    @GetMapping("/test-give-point")
    public String testGivePoint(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) return "redirect:/user/login";

        Users user = userRepository.findById(userNo).orElseThrow();
        itemShopService.givePointToUser(user, 1000);

        redirectAttributes.addFlashAttribute("message", "1000 포인트가 지급되었습니다.");
        return "redirect:/item/shop";
    }


    // 관리자 - 아이템 목록 보기
    @GetMapping("/admin")
    public String manageItems(Model model) {
        model.addAttribute("items", itemShopService.getAllItems());
        return "item/admin";
    }

    // 공통 로직 메서드로 추출해도 좋음
    private String saveImageFile(MultipartFile imageFile, String itemType) throws IOException {
        String originalFileName = imageFile.getOriginalFilename();
        String baseDir = System.getProperty("user.dir");
        String uploadDir = "images/";
        if ("frame".equalsIgnoreCase(itemType)) {
            uploadDir = "frames/";
        }
        File uploadPath = new File(baseDir + "/" + uploadDir);
        if (!uploadPath.exists()) uploadPath.mkdirs();

        File saveFile = new File(uploadPath, originalFileName);

        if (!saveFile.exists()) {
            imageFile.transferTo(saveFile);
        }
        return originalFileName;
    }

    // 아이템 추가 폼
    @GetMapping("/chuga")
    public String chugaForm(Model model) {
        model.addAttribute("item", new Item());
        return "item/chuga";
    }

    // 아이템 추가 처리
    @PostMapping("/chugaProc")
    public String chugaItem(@ModelAttribute Item item,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            RedirectAttributes redirectAttributes) throws IOException {

        if (!imageFile.isEmpty()) {
            String savedFileName = saveImageFile(imageFile, item.getItemType());
            item.setItemImage(savedFileName);
        }

        itemShopService.addItem(item);
        redirectAttributes.addFlashAttribute("message", "아이템이 추가되었습니다.");
        return "redirect:/item/admin";
    }


    // 아이템 수정 폼
    @GetMapping("/sujung/{id}")
    public String sujungForm(@PathVariable("id") int id, Model model) {
        Item item = itemShopService.getItemById(id);
        model.addAttribute("item", item);
        return "item/sujung";
    }

    // 아이템 수정 처리
    @PostMapping("/sujungProc")
    public String sujungItem(@ModelAttribute Item item,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            String savedFileName = saveImageFile(imageFile, item.getItemType());
            item.setItemImage(savedFileName);
        } else {
            Item existingItem = itemShopService.getItemById(item.getItemId());
            item.setItemImage(existingItem.getItemImage());
        }

        itemShopService.updateItem(item);
        redirectAttributes.addFlashAttribute("message", "아이템이 수정되었습니다.");
        return "redirect:/item/admin";
    }


    // 아이템 삭제 확인 페이지
    @GetMapping("/sakje/{id}")
    public String sakjeForm(@PathVariable("id") int id, Model model) {
        Item item = itemShopService.getItemById(id);
        model.addAttribute("item", item);
        return "item/sakje";
    }

    // 아이템 삭제 처리
    @PostMapping("/sakjeProc/{id}")
    public String sakjeItem(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        itemShopService.deleteItem(id);
        redirectAttributes.addFlashAttribute("message", "아이템이 삭제되었습니다.");
        return "redirect:/item/admin";
    }

}