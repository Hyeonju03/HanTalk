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
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            return "redirect:/user/login";
        }

        Users user = userRepository.findById(userNo).orElseThrow();
        model.addAttribute("point", user.getPoint());
        model.addAttribute("items", itemShopService.getAllItemsWithUserInfo(user));
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
            // 파일 이름 생성
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

            // 저장할 실제 경로
            String uploadDir = "C:/upload/images/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();

            // 파일 저장
            File saveFile = new File(uploadDir + fileName);
            imageFile.transferTo(saveFile);

            // DB에는 상대 경로만 저장
            item.setItemImage("/images/" + fileName);
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
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            String uploadDir = "C:/upload/images/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();

            File saveFile = new File(uploadDir + fileName);
            imageFile.transferTo(saveFile);

            item.setItemImage("/images/" + fileName);
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