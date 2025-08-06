package com.example.hantalk.controller;

import com.example.hantalk.dto.AdminDTO;
import com.example.hantalk.dto.CommentDTO;
import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.dto.UsersDTO;
import com.example.hantalk.service.CategoryService;
import com.example.hantalk.service.CommentService;
import com.example.hantalk.service.PostService;
import com.example.hantalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
    public String list(@RequestParam(name = "categoryId", required = false) String categoryIdStr,
                       @RequestParam(name = "page", defaultValue = "1") int page,
                       Model model) {


        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.equals("null") && categoryIdStr.matches("\\d+")) {
            categoryId = Integer.parseInt(categoryIdStr);
        }


        int pageSize = 10;
        Page<PostDTO> postPage;

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

        // 카테고리 목록도 넘기기
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
    public String chuga(@RequestParam(name = "categoryId", required = false) String categoryIdStr,
                        Model model, HttpSession session) {

        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null || userNo == 0) {
            return "redirect:/user/login";
        }

        PostDTO postDTO = new PostDTO();

        if (categoryIdStr != null && categoryIdStr.matches("\\d+")) {
            postDTO.setCategoryId(Integer.parseInt(categoryIdStr));
        }

        model.addAttribute("postDTO", postDTO);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "post/chuga";
    }

    // 게시글 등록 처리
    @PostMapping("/chugaProc")
    public String chugaProc(@ModelAttribute PostDTO postDTO,
                            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile,
                            HttpSession session,
                            RedirectAttributes redire) {

        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            redire.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        postDTO.setUserNo(userNo);

         //파일 업로드 기능 추가
        if (uploadFile != null && !uploadFile.isEmpty()) {
            // 저장 경로
            String uploadDir = "C:/lsy/HanTalk/hantalk/fileUpload";

            String originalFilename = uploadFile.getOriginalFilename();
            String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            try {
                // 파일 저장
                Path filepath = Paths.get(uploadDir, newFilename);
                uploadFile.transferTo(filepath.toFile());

                // DTO에 파일명 저장 (DB에 저장용)
                postDTO.setArchive(newFilename);

               ;

            } catch (Exception e) {
                e.printStackTrace();
                redire.addFlashAttribute("msg", "파일 업로드 중 오류가 발생했습니다.");
                return "redirect:/post/chuga";
            }
        }

        postService.setInsert(postDTO);
        return "redirect:/post/list?categoryId=" + postDTO.getCategoryId();
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

