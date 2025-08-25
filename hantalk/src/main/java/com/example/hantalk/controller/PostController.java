package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.entity.Category;
import com.example.hantalk.entity.Users;
import com.example.hantalk.service.CategoryService;
import com.example.hantalk.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CategoryService categoryService;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/postFiles";

    private static final int NOTICE_CATEGORY_ID = 1;
    private static final int COMMUNITY_CATEGORY_ID = 2;
    private static final int INQUIRY_CATEGORY_ID = 3;

    public void setCategory() {
        if (!categoryService.isPresent()) {
            System.out.println("카테고리 데이터를 생성합니다.");
            categoryService.setCategory();
        } else {
            System.out.println("카테고리가 이미 존재합니다.");
        }
    }

    // 게시물 목록 페이지 (카테고리별 조회)
    // URL: /post/list/{categoryId}
    @GetMapping("/list/{categoryId}")
    public String listPost(
            @PathVariable int categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "searchType", defaultValue = "titleAndContent") String searchType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/user/login";
        }

        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);

        Pageable pageable = PageRequest.of(page, 7, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<PostDTO> postPage = postService.searchPosts(categoryId, keyword, searchType, loginUserNo, isAdmin, pageable);

        model.addAttribute("postPage", postPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("role", SessionUtil.getRole(session));
        model.addAttribute("isEmpty", postPage.isEmpty());
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("isAdmin", isAdmin);

        return "post/list";
    }

    // 관리자 전용: 모든 게시물 목록 페이지
    @GetMapping("/admin")
    public String adminListPost(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "searchType", defaultValue = "titleAndContent") String searchType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        if (!SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/user/login?error=no_permission";
        }

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<PostDTO> postPage = postService.searchPosts(categoryId, keyword, searchType, null, true, pageable);

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        model.addAttribute("postPage", postPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("isAdmin", true);
        model.addAttribute("isEmpty", postPage.isEmpty());
        model.addAttribute("categoryId", categoryId);

        return "post/admin";
    }

    // 게시물 상세 페이지
    @GetMapping("/view/{postId}")
    public String viewPost(@PathVariable int postId, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/user/login";
        }
        try {
            PostDTO post = postService.getPost(postId);
            model.addAttribute("post", post);
            model.addAttribute("role", SessionUtil.getRole(session));
            Integer loginUserNo = SessionUtil.getLoginUserNo(session);
            model.addAttribute("loginUserNo", loginUserNo);
            model.addAttribute("isAdmin", SessionUtil.hasRole(session, "ADMIN"));
        } catch (IllegalArgumentException e) {
            return "redirect:/error/404";
        }
        return "post/view";
    }

    // 게시물 등록 폼
    @GetMapping("/insert/{categoryId}")
    public String insertForm(@PathVariable int categoryId, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/user/login";
        }
        if (categoryId == NOTICE_CATEGORY_ID && !SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/post/list/" + categoryId + "?error=no_permission";
        }

        PostDTO postDTO = new PostDTO();
        Category category = new Category();
        category.setCategoryId(categoryId);
        postDTO.setCategory(category);
        model.addAttribute("post", postDTO);

        // isAdmin 정보를 모델에 담아 HTML로 전달
        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");
        model.addAttribute("isAdmin", isAdmin);

        return "post/insert";
    }

    // 게시물 등록 처리 (파일 첨부, 예외 처리 추가)
    @PostMapping("/insertProc/{categoryId}")
    public String insertProcWithFile(@PathVariable int categoryId,
                                     @ModelAttribute PostDTO postDTO,
                                     @RequestParam(value = "file", required = false) MultipartFile file,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");
            if (categoryId == NOTICE_CATEGORY_ID && !isAdmin) {
                return "redirect:/post/list/" + categoryId + "?error=no_permission";
            }

            Users users = new Users();
            Integer loginUserNo = SessionUtil.getLoginUserNo(session);
            if (loginUserNo == null) {
                return "redirect:/user/login";
            }
            users.setUserNo(loginUserNo);
            postDTO.setUsers(users);

            Category category = new Category();
            category.setCategoryId(categoryId);
            postDTO.setCategory(category);

            PostDTO createdPost;
            if (file != null && !file.isEmpty()) {
                createdPost = postService.createPostWithFile(postDTO, file);
            } else {
                createdPost = postService.createPost(postDTO);
            }

            // 관리자 여부에 따라 다른 페이지로 리다이렉트
            if (isAdmin) {
                return "redirect:/post/admin";
            } else {
                return "redirect:/post/view/" + createdPost.getPostId();
            }

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "파일 업로드 오류: " + e.getMessage());
            return "redirect:/post/insert/" + categoryId;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "게시물 등록 중 오류가 발생했습니다.");
            return "redirect:/post/list/" + categoryId;
        }
    }

    // 게시물 수정 폼
    @GetMapping("/update/{postId}")
    public String updateForm(@PathVariable int postId, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/user/login";
        }
        PostDTO post = postService.getPost(postId);
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);
        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");
        model.addAttribute("isAdmin", isAdmin);

        if (loginUserNo == null) {
            return "redirect:/user/login";
        }

        if (!isAdmin && !loginUserNo.equals(post.getUsers().getUserNo())) {
            // 관리자가 아니면서 작성자가 아닌 경우
            if (post.getCategory().getCategoryId() == NOTICE_CATEGORY_ID || post.getCategory().getCategoryId() == INQUIRY_CATEGORY_ID) {
                return "redirect:/post/view/" + postId + "?error=no_permission";
            }
        }

        model.addAttribute("post", post);
        return "post/update";
    }

    // 게시물 수정 처리 (파일 첨부, 파일 삭제 옵션, 예외 처리 추가)
    @PostMapping("/updateProc/{postId}")
    public String updateProcWithFile(
            @PathVariable int postId,
            @ModelAttribute PostDTO postDTO,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "deleteFile", required = false) Boolean deleteFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            if (!SessionUtil.isLoggedIn(session)) {
                return "redirect:/user/login";
            }

            PostDTO existingPost = postService.getPost(postId);
            Integer loginUserNo = SessionUtil.getLoginUserNo(session);
            boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");

            if (loginUserNo == null) {
                return "redirect:/user/login";
            }

            if (!isAdmin && !loginUserNo.equals(existingPost.getUsers().getUserNo())) {
                if (existingPost.getCategory().getCategoryId() == NOTICE_CATEGORY_ID || existingPost.getCategory().getCategoryId() == INQUIRY_CATEGORY_ID) {
                    return "redirect:/post/view/" + postId + "?error=no_permission";
                }
            }

            if (file != null && !file.isEmpty()) {
                postDTO.setOriginalFileName(file.getOriginalFilename());
            } else if (Boolean.TRUE.equals(deleteFile)) {
                postDTO.setOriginalFileName(null);
            } else if (existingPost.getArchive() != null) {
                postDTO.setOriginalFileName(existingPost.getOriginalFileName());
            }

            postService.updatePostWithFile(postId, postDTO, file, deleteFile);

            // 관리자 여부에 따라 다른 페이지로 리다이렉트
            if (isAdmin) {
                return "redirect:/post/admin";
            } else {
                return "redirect:/post/view/" + postId;
            }

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "파일 업로드 오류: " + e.getMessage());
            return "redirect:/post/update/" + postId;
        }
    }

    // 게시물 삭제 처리 (DELETE)
    @DeleteMapping("/deleteProc/{postId}")
    public ResponseEntity<String> deleteProc(@PathVariable int postId, HttpSession session) {
        if (!SessionUtil.isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        PostDTO existingPost = postService.getPost(postId);
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);
        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");

        if (loginUserNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 유효하지 않습니다.");
        }

        if (!isAdmin && !loginUserNo.equals(existingPost.getUsers().getUserNo())) {
            // 관리자가 아니면서 작성자가 아닌 경우
            if (existingPost.getCategory().getCategoryId() == NOTICE_CATEGORY_ID || existingPost.getCategory().getCategoryId() == INQUIRY_CATEGORY_ID) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
            }
        }

        postService.deletePost(postId);
        return ResponseEntity.ok("삭제 성공");
    }

    // 파일 다운로드
    @GetMapping("/download/file/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        if (fileName.contains("..")) {
            return ResponseEntity.badRequest().build();
        }

        Path path = Paths.get(uploadDir).resolve(fileName).normalize();
        UrlResource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String originalFileName = postService.getOriginalFileName(fileName);
        if (originalFileName == null) {
            originalFileName = fileName;
        }

        String encodedFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // HomeController에서 호출될 최신 게시물 조회용 메서드
    public List<PostDTO> getLatestPostsByCategory(int categoryId, int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<PostDTO> postPage = postService.searchPosts(categoryId, null, "title", null, false, pageable);
        return postPage.getContent();
    }
}
