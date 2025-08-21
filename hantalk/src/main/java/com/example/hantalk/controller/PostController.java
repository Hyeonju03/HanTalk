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
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CategoryService categoryService;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/postFiles";

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }

    private boolean isImage(String fileName) {
        return getExtension(fileName).matches("png|jpg|jpeg|gif|bmp|webp");
    }

    private boolean isTextFile(String fileName) {
        return getExtension(fileName).matches("txt|csv|log|md|java|xml|json|html|css|js");
    }

    // DB에 있는 CATEGORY 테이블의 ID를 상수로 정의 (실제 ID와 맞게 수정 필요)
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

    // 공통: 게시물 목록 페이지 (카테고리별 조회)
    @GetMapping("/list/{categoryId}")
    public String listPost(
            @PathVariable int categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "searchType", defaultValue = "title") String searchType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/user/login";
        }

        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");

        Pageable pageable = PageRequest.of(page, 7, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<PostDTO> postPage = postService.searchPosts(categoryId, keyword, searchType, pageable);

        model.addAttribute("postPage", postPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("role", SessionUtil.getRole(session));
        model.addAttribute("isEmpty", postPage.isEmpty());
        model.addAttribute("categoryId", categoryId);

        model.addAttribute("isAdmin", isAdmin);

        return "post/list";
    }

    // 공통: 게시물 상세 페이지
    @GetMapping("/view/{postId}")
    public String viewPost(@PathVariable int postId, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/user/login";
        }
        PostDTO post = postService.getPost(postId);
        try {
            model.addAttribute("post", post);
            model.addAttribute("role", SessionUtil.getRole(session));

            Integer loginUserNo = SessionUtil.getLoginUserNo(session);
            model.addAttribute("loginUserNo", loginUserNo);

            model.addAttribute("isAdmin", SessionUtil.hasRole(session, "ADMIN"));
        } catch (IllegalArgumentException e) {
            return "redirect:/post/list/" + post.getCategory().getCategoryId() + "?error=notfound";
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
        return "post/insert";
    }

    @PostMapping("/insertProc/{categoryId}")
    public String insertProcWithFile(@PathVariable int categoryId,
                                     @ModelAttribute PostDTO postDTO,
                                     @RequestParam(value = "file", required = false) MultipartFile file,
                                     HttpSession session) {
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

                // 파일을 포함하여 게시물 등록 서비스를 호출
                createdPost = postService.createPostWithFile(postDTO, file);
            } else {

                // 파일이 없는 경우 게시물 등록 서비스를 호출
                createdPost = postService.createPost(postDTO);
            }

            // 서비스에서 파일 정보가 담긴 PostDTO를 반환하므로,
            // 이 반환값을 사용하면 DB에 파일 정보가 올바르게 저장됩니다.
            return "redirect:/post/view/" + createdPost.getPostId();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/post/list/" + categoryId + "?error=create_failed";
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

        if (loginUserNo == null) {
            return "redirect:/user/login";
        }

        if (post.getCategory().getCategoryId() == NOTICE_CATEGORY_ID && !isAdmin) {
            return "redirect:/post/view/" + postId + "?error=no_permission";
        }
        if ((post.getCategory().getCategoryId() == COMMUNITY_CATEGORY_ID || post.getCategory().getCategoryId() == INQUIRY_CATEGORY_ID)
                && !isAdmin && !loginUserNo.equals(post.getUsers().getUserNo())) {
            return "redirect:/post/view/" + postId + "?error=no_permission";
        }

        model.addAttribute("post", post);
        return "post/update";
    }

    // 게시물 수정 처리 (파일 첨부, 파일 삭제 옵션 추가)
    @PostMapping("/updateProc/{postId}")
    public String updateProcWithFile(
            @PathVariable int postId,
            @ModelAttribute PostDTO postDTO,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "deleteFile", required = false) Boolean deleteFile,
            HttpSession session) {

        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/user/login";
        }

        PostDTO existingPost = postService.getPost(postId);
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);
        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");

        if (loginUserNo == null) {
            return "redirect:/user/login";
        }

        if (existingPost.getCategory().getCategoryId() == NOTICE_CATEGORY_ID && !isAdmin) {
            return "redirect:/post/view/" + postId + "?error=no_permission";
        }
        if ((existingPost.getCategory().getCategoryId() == COMMUNITY_CATEGORY_ID || existingPost.getCategory().getCategoryId() == INQUIRY_CATEGORY_ID)
                && !isAdmin && !loginUserNo.equals(existingPost.getUsers().getUserNo())) {
            return "redirect:/post/view/" + postId + "?error=no_permission";
        }

        // 파일이 존재하고 비어있지 않은 경우, DTO에 원본 파일명 추가
        if (file != null && !file.isEmpty()) {
            postDTO.setOriginalFileName(file.getOriginalFilename());
        }
        // 파일 삭제 옵션이 선택된 경우, 원본 파일명 null로 설정
        else if (Boolean.TRUE.equals(deleteFile)) {
            postDTO.setOriginalFileName(null);
        }
        // 기존 파일이 유지되는 경우, 기존 원본 파일명 유지
        else if (existingPost.getArchive() != null) {
            postDTO.setOriginalFileName(existingPost.getOriginalFileName());
        }

        postService.updatePostWithFile(postId, postDTO, file, deleteFile);
        return "redirect:/post/view/" + postId;
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

        if ((existingPost.getCategory().getCategoryId() == NOTICE_CATEGORY_ID || existingPost.getCategory().getCategoryId() == INQUIRY_CATEGORY_ID)
                && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
        if (existingPost.getCategory().getCategoryId() == COMMUNITY_CATEGORY_ID && !isAdmin && !loginUserNo.equals(existingPost.getUsers().getUserNo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
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

        // PostService를 통해 원본 파일명을 가져오도록 수정
        String originalFileName = postService.getOriginalFileName(fileName);
        if (originalFileName == null) {
            // 원본 파일명이 없을 경우를 대비하여 저장된 파일명을 사용
            originalFileName = fileName;
        }

        String encodedFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + originalFileName + "\"; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // HomeController에서 호출될 최신 게시물 조회용 메서드
    public List<PostDTO> getLatestPostsByCategory(int categoryId, int count) {
        // Pageable 객체를 사용하여 최신순으로 'count'개만 가져오도록 설정
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<PostDTO> postPage = postService.searchPosts(categoryId, null, "title", pageable);
        return postPage.getContent();
    }

}