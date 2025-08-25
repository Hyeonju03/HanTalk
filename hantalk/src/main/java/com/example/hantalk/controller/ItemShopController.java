package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.entity.Item;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.UsersRepository;
import com.example.hantalk.service.ItemShopService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        // 로그인 여부 확인
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/user/login";
        }

        String role = SessionUtil.getRole(session);
        // role이 null이거나 USER / ADMIN 둘 다 아니면 접근 차단
        if (role == null || !(role.equalsIgnoreCase("USER") || role.equalsIgnoreCase("ADMIN"))) {
            return "redirect:/user/login";
        }

        // ADMIN인 경우
        if ("ADMIN".equalsIgnoreCase(role)) {
            model.addAttribute("point", 0);
            // ✅ 수정: 모든 아이템(프레임 + 이미지)을 다 불러와서 뷰에 전달
            model.addAttribute("items", itemShopService.getAllItems());
            model.addAttribute("isAdmin", true);
            return "item/shop";
        }

        // USER인 경우
        Integer userNo = SessionUtil.getLoginUserNo(session);
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
    public String manageItems(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<Item> itemPage = itemShopService.getItemList(page);
        model.addAttribute("itemPage", itemPage);
        model.addAttribute("items", itemPage.getContent());

        // 페이지네이션 숫자 범위 계산 (현재 페이지를 중심으로 5개씩)
        int startPage = Math.max(0, itemPage.getNumber() - 2);
        int endPage = Math.min(itemPage.getTotalPages() - 1, itemPage.getNumber() + 2);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

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
    public String sujungItem(@ModelAttribute Item formItem,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) throws IOException {

        Item existingItem = itemShopService.getItemById(formItem.getItemId());

        // 이미지 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            String baseDir = System.getProperty("user.dir");

            // 타입에 따라 저장 폴더 결정
            String subDir = "images/";
            if ("frame".equals(existingItem.getItemType())) {
                subDir = "frames/";
            }
            String uploadDir = baseDir + "/uploads/" + subDir;

            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();

            File saveFile = new File(uploadDir + fileName);
            imageFile.transferTo(saveFile);

            existingItem.setItemImage(fileName); // 파일명만 저장
        }

        existingItem.setItemName(formItem.getItemName());
        existingItem.setItemDescription(formItem.getItemDescription());
        existingItem.setPrice(formItem.getPrice());

        itemShopService.updateItem(existingItem);

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