package com.example.hantalk.controller;


import com.example.hantalk.dto.CommentDTO;
import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.entity.Admin;
import com.example.hantalk.entity.Category;
import com.example.hantalk.entity.Post;
import com.example.hantalk.entity.Users;
import com.example.hantalk.service.CategoryService;
import com.example.hantalk.service.CommentService;
import com.example.hantalk.service.PostService;
import com.example.hantalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    // 게시글 목록
    @GetMapping("/list")
    public String list(@RequestParam(name = "categoryId", required = false) Integer categoryId,
                       @RequestParam(name = "page", defaultValue = "1") int page,
                       Model model) {
        int pageSize = 10;
        Page<PostDTO> postPage;

        if (categoryId != null) {
            postPage = postService.getPostsByCategory(categoryId, page, pageSize);
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            postPage = postService.getPagePosts(page, pageSize);
        }

        // 게시글 목록, 페이징 모델 추가
        model.addAttribute("list", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        // 카테고리 목록
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        // 선택된 카테고리명 넣기 및 인코딩 처리
        String selectedCategoryName = null;
        String encodedCategoryName = null;

        if (categoryId != null) {
            selectedCategoryName = categoryService.getCategoryNameById(categoryId);
        }
        if (selectedCategoryName == null && !categories.isEmpty()) {
            selectedCategoryName = categories.get(0).getCategoryName();
        }

        if (selectedCategoryName != null) {
            encodedCategoryName = URLEncoder.encode(selectedCategoryName, StandardCharsets.UTF_8);
        }

        model.addAttribute("selectedCategoryName", selectedCategoryName);
        model.addAttribute("encodedCategoryName", encodedCategoryName);

        return "post/list";
    }

    // 게시글 상세보기
    @GetMapping("/view/{postId}")
    public String view(@PathVariable("postId") int postId, Model model) {

        //조회수
        postService.increaseViewCount(postId);

        PostDTO dto = new PostDTO();
        dto.setPostId(postId);
        PostDTO postDTO = postService.getSelectOne(dto);

        List<CommentDTO> commentList = commentService.getCommentsByPostId(postId);

        model.addAttribute("returnDTO", postDTO);
        model.addAttribute("commentList", commentList);

        return "post/view";
    }

    //게시글 추가
    @GetMapping("/{categoryName}/chuga")
    public String chuga(@PathVariable String categoryName,
                        Model model, HttpSession session,
                        RedirectAttributes redire) {

        categoryName = URLDecoder.decode(categoryName, StandardCharsets.UTF_8);

        Object roleObj = session.getAttribute("role");
        String role = (roleObj != null) ? roleObj.toString() : "";

        if (!"ADMIN".equalsIgnoreCase(role)) {
            Integer userNo = (Integer) session.getAttribute("userNo");
            if (userNo == null || userNo == 0) {
                return "redirect:/user/login";
            }
        }

        // DB에서 조회?
        Integer categoryId = categoryService.getCategoryIdByName(categoryName.trim());
        if (categoryId == null) {
            redire.addFlashAttribute("msg", "존재하지 않는 카테고리입니다.");
            return "redirect:/post";
        }

        // ✅ categoryId가 1 (공지사항)일 때만 관리자 권한 체크
        if (categoryId == 1 && !"ADMIN".equalsIgnoreCase(role)) {
            redire.addFlashAttribute("msg", "공지사항은 관리자만 작성할 수 있습니다.");
            String encodedCategoryName = URLEncoder.encode(categoryName, StandardCharsets.UTF_8);
            return "redirect:/post/" + encodedCategoryName;
        }

        PostDTO postDTO = new PostDTO();
        postDTO.setCategoryId(categoryId);

        model.addAttribute("postDTO", postDTO);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryName", categoryName); // 추가

        return "post/chuga";
    }

    // 게시글 등록 처리
    @PostMapping("/chugaProc")
    public String chugaProc(@ModelAttribute PostDTO postDTO,
                            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile,
                            HttpSession session,
                            RedirectAttributes redire) {

        Object roleObj = session.getAttribute("role");
        String role = (roleObj != null) ? roleObj.toString() : "";

        Integer userNo = (Integer) session.getAttribute("userNo");

        //ADMIN전용 통과 지점
        if (!"ADMIN".equalsIgnoreCase(role)) {
            if (userNo == null) {
                redire.addFlashAttribute("msg", "로그인이 필요합니다.");
                return "redirect:/user/login";
            }
            postDTO.setUserNo(userNo);
        } else {
            if (userNo == null) {
                postDTO.setUserNo(1);  //DB에 있어야 함
            } else {
                postDTO.setUserNo(userNo);
            }
        }

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

            } catch (Exception e) {
                e.printStackTrace();
                redire.addFlashAttribute("msg", "파일 업로드 중 오류가 발생했습니다.");
                return "redirect:/post/chuga";
            }
        }
        Integer categoryId = postDTO.getCategoryId();
        String categoryName = categoryService.getCategoryNameById(categoryId);

        if (categoryName == null) {
            redire.addFlashAttribute("msg", "존재하지 않는 카테고리입니다.");
            return "redirect:/post";
        }
        if (postDTO.getContent() == null) {
            redire.addFlashAttribute("msg", "내용을 채워주세요");
            return "redirect:/post/list";
        }
        if (postDTO.getTitle() == null) {
            redire.addFlashAttribute("msg", "제목을 입력하세요");
            return "redirect:/post/list";

        }

        postService.setInsert(postDTO);
        return "redirect:/post/list?categoryId=" + postDTO.getCategoryId();
    }

    // 게시글 수정 페이지
    @GetMapping("/sujung/{postId}")
    public String sujung(@PathVariable("postId") int postId, Model model, HttpSession session) {
        PostDTO dto = new PostDTO();
        dto.setPostId(postId);
        PostDTO returnDTO = postService.getSelectOne(dto);

        //작성자와 수정하려는 이가 일치하는지 확인
        int loginUsers = (int) session.getAttribute("userNo");

        if (loginUsers <= 0) {
            return "redirect:/user/login";
        }
        if (!returnDTO.getUserNo().equals(loginUsers)) {
            return "redirect:/post/list";
        }

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
    public String sakje(@PathVariable("postId") int postId, Model model, HttpSession session) {
        PostDTO dto = new PostDTO();
        dto.setPostId(postId);
        PostDTO returnDTO = postService.getSelectOne(dto);
        model.addAttribute("returnDTO", returnDTO);

        //작성자와 삭제하려는 이가 일치하는지 확인
        int loginUsers = (int) session.getAttribute("userNo");

        if (loginUsers <= 0) {
            return "redirect:/user/login";
        }
        if (!returnDTO.getUserNo().equals(loginUsers)) {
            return "redirect:/post/list";
        }

        return "post/sakje";
    }

    // 게시글 삭제 처리
    @PostMapping("/sakjeProc")
    public String sakjeProc(@RequestParam(value = "postIds", required = false) List<Long> postIds,
                            @ModelAttribute PostDTO dto,
                            HttpSession session,
                            RedirectAttributes redire) {

        Object roleObj = session.getAttribute("role");
        Users loginUser = (Users) session.getAttribute("loginUser");

        String role = (roleObj != null) ? roleObj.toString() : "";
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        if (isAdmin) {
            // ✅ 관리자 다중 삭제 처리
            if (postIds != null && !postIds.isEmpty()) {
                postService.deletePostsByIds(postIds);
                redire.addFlashAttribute("msg", "선택한 게시글이 삭제되었습니다.");
            } else {
                redire.addFlashAttribute("msg", "삭제할 게시글을 선택해주세요.");
            }
            return "redirect:/admin/postAdmin";

        } else {
            // ✅ 일반 사용자 단일 삭제 (작성자 검증 포함)
            if (loginUser == null) {
                redire.addFlashAttribute("msg", "로그인이 필요합니다.");
                return "redirect:/user/login";
            }

            PostDTO targetPost = postService.getSelectOne(dto);
            if (targetPost.getUserNo() != null && targetPost.getUserNo().equals(loginUser.getUserNo())) {
                postService.setDelete(dto);
                redire.addFlashAttribute("msg", "게시글이 삭제되었습니다.");
            } else {
                redire.addFlashAttribute("msg", "삭제 권한이 없습니다.");
            }
            return "redirect:/post/list";
        }
    }
    //검색
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         Model model) {

        int pageSize = 10;
        Page<PostDTO> postPage = postService.searchPostsByKeyword(keyword, page, pageSize);

        model.addAttribute("list", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        model.addAttribute("categories", categoryService.getAllCategories());

        return "post/list";
    }

    //파일 다운
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) throws IOException {
        // 업로드된 경로 설정
        Path filePath = Paths.get("C:/lsy/HanTalk/hantalk/fileUpload/", fileName);
        Resource resource = new UrlResource(filePath.toUri());

        // 파일 존재 여부 확인
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        // 한글 파일명 깨짐 방지
        String encodedFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    //관리자가 글 모아보기
    @GetMapping("/postAdmin")
    public String adminAllPosts(@RequestParam(value = "category", required = false) Integer categoryId, Model model, HttpSession session) {
        // 관리자만 접근 가능하도록
        Object roleObj = session.getAttribute("role");
        String role = (roleObj != null) ? roleObj.toString() : "";
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/user/login";  // 관리자 아니면 로그인 페이지로
        }

        List<PostDTO> posts;

        if (categoryId == null) {
            // 카테고리 선택 없으면 전체 게시글 조회
            List<PostDTO> noticePosts = postService.getPostsByCategoryId(1);
            List<PostDTO> communityPosts = postService.getPostsByCategoryId(2);
            List<PostDTO> inquiryPosts = postService.getPostsByCategoryId(3);

            posts = new ArrayList<>();
            posts.addAll(noticePosts);
            posts.addAll(communityPosts);
            posts.addAll(inquiryPosts);
        } else {
            // 카테고리별 게시글만 조회
            posts = postService.getPostsByCategoryId(categoryId);
        }


        model.addAttribute("list", posts);
        model.addAttribute("selectedCategory", categoryId);

        return "post/postAdmin";
    }

    @GetMapping("/{categoryName}")
    public String listByCategory(@PathVariable String categoryName,
                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                 Model model,
                                 RedirectAttributes redire,
                                 HttpSession session) {

        // ✅ 로그인 체크

        String r =(String) session.getAttribute("role");

        // ✅ 관리자 여부 체크
        boolean isAdmin = "ADMIN".equalsIgnoreCase(r);

        // ✅ 카테고리 처리
        Integer categoryId = categoryService.getCategoryIdByName(categoryName);
        if (categoryId == null) {
            redire.addFlashAttribute("msg", "존재하지 않는 카테고리입니다.");
            if (isAdmin) {
                return "redirect:/post/postAdmin";
            } else {
                return "redirect:/post/list";
            }
        }
        int pageSize = 10;
        Page<PostDTO> postPage = postService.getPostsByCategory(categoryId, page, pageSize);

        model.addAttribute("list", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryName", categoryName);
        model.addAttribute("encodedCategoryName", URLEncoder.encode(categoryName, StandardCharsets.UTF_8));

        return isAdmin ? "post/postAdmin" : "post/list";
    }
}