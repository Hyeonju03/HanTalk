package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // DB에 있는 CATEGORY 테이블의 ID를 상수로 정의 (실제 ID와 맞게 수정 필요)
    private static final int NOTICE_CATEGORY_ID = 1;
    private static final int COMMUNITY_CATEGORY_ID = 2;
    private static final int INQUIRY_CATEGORY_ID = 3;

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
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<PostDTO> postPage = postService.searchPosts(categoryId, keyword, searchType, pageable);

        model.addAttribute("postPage", postPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("role", SessionUtil.getRole(session));
        model.addAttribute("isEmpty", postPage.isEmpty());
        model.addAttribute("categoryId", categoryId);

        return "post/list";
    }

    // 공통: 게시물 상세 페이지
    @GetMapping("/view/{postId}")
    public String viewPost(@PathVariable int postId, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }
        try {
            PostDTO post = postService.getPost(postId);
            model.addAttribute("post", post);
            model.addAttribute("role", SessionUtil.getRole(session));
            model.addAttribute("loginUserNo", SessionUtil.getLoginUserNo(session));
            model.addAttribute("isAdmin", SessionUtil.hasRole(session, "ADMIN"));
        } catch (IllegalArgumentException e) {
            // 게시물이 없을 경우, 카테고리별 목록으로 리다이렉트
            return "redirect:/post/list/" + INQUIRY_CATEGORY_ID + "?error=notfound";
        }
        return "post/view";
    }

    // 게시물 등록 폼
    @GetMapping("/insert/{categoryId}")
    public String insertForm(@PathVariable int categoryId, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        // 💡 공지사항(Notice)는 ADMIN만 등록 가능
        if (categoryId == NOTICE_CATEGORY_ID && !SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/post/list/" + categoryId + "?error=no_permission";
        }

        PostDTO postDTO = new PostDTO();
        postDTO.setCategoryId(categoryId);
        model.addAttribute("post", postDTO);
        return "post/insert";
    }

    // 게시물 등록 처리
    @PostMapping("/insertProc/{categoryId}")
    public String insertProc(@PathVariable int categoryId, @ModelAttribute PostDTO postDTO, HttpSession session) {
        System.out.println("PostDTO: " + postDTO.toString());
        try {
            if (!SessionUtil.isLoggedIn(session)) {
                return "redirect:/login";
            }
            // 💡 공지사항(Notice)는 ADMIN만 등록 가능
            if (categoryId == NOTICE_CATEGORY_ID && !SessionUtil.hasRole(session, "ADMIN")) {
                return "redirect:/post/list/" + categoryId + "?error=no_permission";
            }
            System.out.println("로그인 상태: " + SessionUtil.isLoggedIn(session));
            Integer loginUserNo = SessionUtil.getLoginUserNo(session);
            if (loginUserNo == null) {
                return "redirect:/login";
            }
            postDTO.setUserNo(loginUserNo);
            postDTO.setCategoryId(categoryId);
            System.out.println("createPost() 메서드 호출 전");
            PostDTO createdPost = postService.createPost(postDTO);
            return "redirect:/post/view/" + createdPost.getPostId();
        } catch(Exception e) {
            e.printStackTrace(); // 예외 내용 출력
            // 예외 발생 시 목록 페이지로 리다이렉트하도록 return 문 추가
            return "redirect:/post/list/" + categoryId + "?error=create_failed";
        }
    }

    // 게시물 수정 폼
    @GetMapping("/update/{postId}")
    public String updateForm(@PathVariable int postId, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }
        PostDTO post = postService.getPost(postId);
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);
        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");

        // 💡 공지사항(Notice): ADMIN만 수정 가능
        if (post.getCategoryId() == NOTICE_CATEGORY_ID && !isAdmin) {
            return "redirect:/post/view/" + postId + "?error=no_permission";
        }
        // 💡 커뮤니티/문의사항(Community/Inquiry): 작성자 또는 ADMIN만 수정 가능
        if ((post.getCategoryId() == COMMUNITY_CATEGORY_ID || post.getCategoryId() == INQUIRY_CATEGORY_ID)
                && !isAdmin && !loginUserNo.equals(post.getUserNo())) {
            return "redirect:/post/view/" + postId + "?error=no_permission";
        }

        model.addAttribute("post", post);
        return "post/update";
    }

    // 게시물 수정 처리
    @PostMapping("/updateProc/{postId}")
    public String updateProc(@PathVariable int postId, @ModelAttribute PostDTO postDTO, HttpSession session) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }
        PostDTO existingPost = postService.getPost(postId);
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);
        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");

        // 💡 공지사항(Notice): ADMIN만 수정 가능
        if (existingPost.getCategoryId() == NOTICE_CATEGORY_ID && !isAdmin) {
            return "redirect:/post/view/" + postId + "?error=no_permission";
        }
        // 💡 커뮤니티/문의사항(Community/Inquiry): 작성자 또는 ADMIN만 수정 가능
        if ((existingPost.getCategoryId() == COMMUNITY_CATEGORY_ID || existingPost.getCategoryId() == INQUIRY_CATEGORY_ID)
                && !isAdmin && !loginUserNo.equals(existingPost.getUserNo())) {
            return "redirect:/post/view/" + postId + "?error=no_permission";
        }

        postService.updatePost(postId, postDTO);
        return "redirect:/post/view/" + postId;
    }

    //--------------------------------------------------------------

    // 게시물 삭제 처리 (DELETE)
    @DeleteMapping("/deleteProc/{postId}")
    public ResponseEntity<String> deleteProc(@PathVariable int postId, HttpSession session) {
        if (!SessionUtil.isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        PostDTO existingPost = postService.getPost(postId);
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);
        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");

        // 💡 공지사항(Notice)/문의사항(Inquiry): ADMIN만 삭제 가능
        if ((existingPost.getCategoryId() == NOTICE_CATEGORY_ID || existingPost.getCategoryId() == INQUIRY_CATEGORY_ID)
                && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
        // 💡 커뮤니티(Community): 작성자 또는 ADMIN만 삭제 가능
        if (existingPost.getCategoryId() == COMMUNITY_CATEGORY_ID && !isAdmin && !loginUserNo.equals(existingPost.getUserNo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        postService.deletePost(postId);
        return ResponseEntity.ok("삭제 성공");
    }
}