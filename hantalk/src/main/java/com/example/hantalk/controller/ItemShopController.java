package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
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
        Integer userNo = SessionUtil.getLoginUserNo(session);
        String role = SessionUtil.getRole(session);

        if (userNo == null || role == null) {
            return "redirect:/user/login";
        }

        if ("ADMIN".equals(role)) {
            model.addAttribute("point", 0);
            model.addAttribute("items", itemShopService.getAllItems());
            model.addAttribute("isAdmin", true);
            return "item/shop";
        }

        // USER 권한인 경우
        Users user = userRepository.findById(userNo).orElseThrow();
        model.addAttribute("point", user.getPoint());
        model.addAttribute("items", itemShopService.getAllItemsWithUserInfo(user));
        model.addAttribute("isAdmin", false);
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
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

            // 아이템 종류에 따라 폴더 분리
            String baseDir = System.getProperty("user.dir");
            String uploadDir;

            if ("frame".equalsIgnoreCase(item.getItemType())) {
                uploadDir = baseDir + "/uploads/frames/";
            } else {
                uploadDir = baseDir + "/uploads/images/";
            }

            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();

            File saveFile = new File(uploadDir + fileName);
            imageFile.transferTo(saveFile);

            item.setItemImage(fileName);
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
            // 새 이미지가 업로드되었을 경우 → 새 파일로 교체
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            String baseDir = System.getProperty("user.dir");
            String uploadDir = baseDir + "/uploads/images/"; // 기본 디렉토리 (프레임과 구분 안 함)

            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();

            File saveFile = new File(uploadDir + fileName);
            imageFile.transferTo(saveFile);

            item.setItemImage(fileName); // 새 이미지 설정
        } else {
            // 새 이미지가 업로드되지 않은 경우 → 기존 이미지 유지
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