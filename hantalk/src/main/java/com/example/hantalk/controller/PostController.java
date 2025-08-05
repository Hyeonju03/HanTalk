package com.example.hantalk.controller;

import com.example.hantalk.dto.AdminDTO;
import com.example.hantalk.dto.CommentDTO;
import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.service.CategoryService;
import com.example.hantalk.service.CommentService;
import com.example.hantalk.service.PostService;
import com.example.hantalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    public PostController(PostService postService, UserService userService, CategoryService categoryService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.commentService = commentService;
    }

    // 게시글 목록
    @GetMapping("/list")
    public String list(@RequestParam(required = false) Integer categoryId,
                       @RequestParam(defaultValue = "1") int page,
                       Model model) {

        int pageSize = 10;
        Page<PostDTO> postPage; // ← Optional 제거

        // 카테고리 필터링 적용
        if (categoryId != null) {
            postPage = postService.getPostsByCategory(categoryId, page, pageSize);
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            postPage = postService.getPagePosts(page, pageSize);
        }

        // 페이지 처리
        if (postPage.isEmpty() && page > 1) {
            model.addAttribute("noPage", true);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", 0);
        } else {
            model.addAttribute("list", postPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", postPage.getTotalPages());
        }

        // 카테고리 목록도 넘기기 (상단 메뉴나 필터용)
        model.addAttribute("categories", categoryService.getAllCategories());

        return "post/list";
    }


    // 게시글 상세보기
    @GetMapping("/view/{postId}")
    public String view(@PathVariable("postId") int postId, Model model) {
        PostDTO dto = new PostDTO();
        dto.setPostId(postId);
        PostDTO postDTO = postService.getSelectOne(dto);
        List<CommentDTO> commentList = commentService.getCommentsByPostId(postId);

        model.addAttribute("returnDTO", postDTO);
        model.addAttribute("commentList", commentList);

        return "post/view";
    }


    //게시글 등록
    @GetMapping("/chuga")
    public String chuga(Model model, HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        System.out.println("chuga 진입, userNo=" + userNo);
        if (userNo == null || userNo == 0) {
            System.out.println("userNo 없어서 로그인 페이지로 리다이렉트");
            return "redirect:/user/login";
        }
        model.addAttribute("postDTO", new PostDTO());

        model.addAttribute("categories", categoryService.getAllCategories());//카테고리

        return "post/chuga";
    }

    // 게시글 등록 처리
    @PostMapping("/chugaProc")
    public String chugaProc(@ModelAttribute PostDTO postDTO, HttpSession session, RedirectAttributes redire) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            redire.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        postDTO.setUserNo(userNo);
        postService.setInsert(postDTO);
        return "redirect:/post/list";
    }

    // 게시글 수정 페이지
    @GetMapping("/sujung/{postId}")
    public String sujung(@PathVariable("postId") int postId, Model model) {
        PostDTO dto = new PostDTO();
        dto.setPostId(postId);
        PostDTO returnDTO = postService.getSelectOne(dto);
        model.addAttribute("returnDTO", returnDTO);
        return "post/sujung";
    }

    // 게시글 수정 처리
    @PostMapping("/sujungProc")
    public String sujungProc(@ModelAttribute PostDTO dto, RedirectAttributes redire) {
        postService.setUpdate(dto);
        redire.addFlashAttribute("msg", "게시글이 수정되었습니다.");
        return "redirect:/post/view/" + dto.getPostId();
    }

    // 게시글 삭제 페이지
    @GetMapping("/sakje/{postId}")
    public String sakje(@PathVariable("postId") int postId, Model model) {
        PostDTO dto = new PostDTO();
        dto.setPostId(postId);
        PostDTO returnDTO = postService.getSelectOne(dto);
        model.addAttribute("returnDTO", returnDTO);
        return "post/sakje";
    }

    // 게시글 삭제 처리
    @PostMapping("/sakjeProc")
    public String sakjeProc(@ModelAttribute PostDTO dto, RedirectAttributes redire) {
        postService.setDelete(dto);
        redire.addFlashAttribute("msg", "게시글이 삭제되었습니다.");
        return "redirect:/post/list";
    }
}

