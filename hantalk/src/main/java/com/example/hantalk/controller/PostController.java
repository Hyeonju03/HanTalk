package com.example.hantalk.controller;

import com.example.hantalk.dto.AdminDTO;
import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.service.CategoryService;
import com.example.hantalk.service.PostService;
import com.example.hantalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CategoryService categoryService;

    public PostController(PostService postService, UserService userService, CategoryService categoryService) {
        this.postService = postService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    // 게시글 목록
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 10;
        Page<PostDTO> postPage = (Page<PostDTO>) postService.getPagePosts(page, pageSize);

        if (postPage.isEmpty() && page > 1) {
            model.addAttribute("noPage", true);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", 0);
        } else {
            model.addAttribute("list", postPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", postPage.getTotalPages());
        }

        return "post/list";
    }

    // 게시글 상세보기
    @GetMapping("/view/{postId}")
    public String view(@PathVariable("postId") int postId, Model model) {
        PostDTO dto = new PostDTO();
        dto.setPostId(postId);
        PostDTO returnDTO = postService.getSelectOne(dto);
        model.addAttribute("returnDTO", returnDTO);
        return "post/view";
    }

    // 게시글 등록 페이지
    @GetMapping("/chuga")
    public String chuga(HttpSession session) {
        AdminDTO loginAdmin = (AdminDTO) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            return "redirect:/admin/login";
        }
        return "post/chuga";
    }

    // 게시글 등록 처리
    @PostMapping("/chugaProc")
    public String chugaProc(@ModelAttribute PostDTO postDTO, HttpSession session, RedirectAttributes redire) {
        AdminDTO loginAdmin = (AdminDTO) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            redire.addFlashAttribute("msg", "관리자만 등록할 수 있습니다.");
            return "redirect:/post/list";
        }

        postService.setInsert(postDTO);
        redire.addFlashAttribute("msg", "게시글이 등록되었습니다.");
        return "redirect:/post/list";
    }

    // 게시글 수정 페이지
    @GetMapping("/sujung/{postId}")
    public String sujung(@PathVariable("postId") int postId, Model model, HttpSession session) {
        PostDTO dto = new PostDTO();
        dto.setPostId(postId);
        PostDTO returnDTO = postService.getSelectOne(dto);
        model.addAttribute("returnDTO", returnDTO);
        return "post/sujung";
    }

    // 게시글 수정 처리
    @PostMapping("/sujungProc")
    public String sujungProc(@ModelAttribute PostDTO dto, HttpSession session, RedirectAttributes redire) {
        AdminDTO loginAdmin = (AdminDTO) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            redire.addFlashAttribute("msg", "관리자만 수정할 수 있습니다.");
            return "redirect:/post/list";
        }

        postService.setUpdate(dto);
        redire.addFlashAttribute("msg", "게시글이 수정되었습니다.");
        return "redirect:/post/view/" + dto.getPostId();
    }

    // 게시글 삭제 페이지
    @GetMapping("/sakje/{postId}")
    public String sakje(@PathVariable("postId") int postId, Model model, HttpSession session) {
        AdminDTO loginAdmin = (AdminDTO) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            return "redirect:/admin/login";
        }

        PostDTO dto = new PostDTO();
        dto.setPostId(postId);
        PostDTO returnDTO = postService.getSelectOne(dto);
        model.addAttribute("returnDTO", returnDTO);
        return "post/sakje";
    }

    // 게시글 삭제 처리
    @PostMapping("/sakjeProc")
    public String sakjeProc(@ModelAttribute PostDTO dto, HttpSession session, RedirectAttributes redire) {
        AdminDTO loginAdmin = (AdminDTO) session.getAttribute("loginAdmin");

        if (loginAdmin == null) {
            redire.addFlashAttribute("msg", "관리자만 삭제할 수 있습니다.");
            return "redirect:/post/list";
        }

        postService.setDelete(dto);
        redire.addFlashAttribute("msg", "게시글이 삭제되었습니다.");
        return "redirect:/post/list";
    }
}
