package com.example.hantalk.service;

import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.entity.Post;
import com.example.hantalk.repository.PostRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequestMapping
@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 전체 게시글 조회
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 게시글 ID로 조회
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id.intValue()); // Post의 ID가 int일 경우
    }

    // 게시글 등록
    public void setInsert(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setUsers(postDTO.getUsers());
        post.setViewCount(0);
        post.setCreateDate(LocalDateTime.now());
        post.setUpdateDate(LocalDateTime.now());

        postRepository.save(post);
    }

    // 게시글 수정
    public void setUpdate(PostDTO postDTO) {
        Optional<Post> optionalPost = postRepository.findById(postDTO.getPostId());
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setTitle(postDTO.getTitle());
            post.setContent(postDTO.getContent());
            post.setUpdateDate(LocalDateTime.now());

            postRepository.save(post);
        }
    }

    // 게시글 삭제
    public void setDelete(PostDTO postDTO, HttpSession session) {
        postRepository.deleteById(postDTO.getPostId());
    }

    // 조회수 증가
    public void increaseViewCount(Post post) {
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }
}
