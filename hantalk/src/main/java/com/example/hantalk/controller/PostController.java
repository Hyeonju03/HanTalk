package com.example.hantalk.controller;


import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.entity.Post;
import com.example.hantalk.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/post")
public class PostController {
    private final PostService postService;


    public PostController(PostService postService) {
        this.postService = postService;
    }
    //게시글 목록
    @GetMapping("/list")
    public String listPosts(Model model){
        model.addAttribute("post", postService.getAllPosts());
        return "post/list";
    }

    //게시글 상세보기
    @GetMapping("/view")
    public String view(@RequestParam("id")Long postId, Model model){
        Optional<Post> postDTO = postService.getPostById(postId);
        model.addAttribute("post", postDTO);
        return "posts/view";
    }

    //게시글 등록
    @GetMapping("/chuga")
    public String chuga(Model model){
        List<Post> postList = postService.getAllPosts();
        model.addAttribute("postList", postList);
        model.addAttribute("post", new PostDTO());
        return "post/chuga";
    }

    //게시글 등록처리
    @PostMapping("/chugaProc")
    public String chugaProc(@ModelAttribute PostDTO postDTO) {
        postService.setInsert(postDTO);
        return "redirect:/post/list";
    }

    //게시글 수정
    @GetMapping("/sujung")
    public String sujung(@RequestParam("id") Long postId, Model model){
        Optional<Post> postDTO = postService.getPostById(postId);
        model.addAttribute("post", postDTO);
        return "posts/sujung";
    }

    //게시글 수정처리
    @PostMapping("/sujungProc")
    public String sujungProc(@ModelAttribute PostDTO postDTO) {
        postService.setUpdate(postDTO);
        return "redirect:/post/list";
    }


    //게시글 삭제
    @GetMapping("/sakje")
    public String sake(@RequestParam("id") Long postId, Model model){
        Optional<Post> postDTO = postService.getPostById(postId);
        model.addAttribute("post", postDTO);
        return "posts/sakje";
    }


    //게시글 삭제 처리
    @PostMapping("/sakjeProc")
    public String sakjeProc(@ModelAttribute PostDTO postDTO, HttpSession session) {
        postService.setDelete(postDTO, session);
        return "redirect:/post/list";
    }
}
