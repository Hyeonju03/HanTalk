package com.example.hantalk.service;

import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.entity.Category;
import com.example.hantalk.entity.Post;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.CategoryRepository;
import com.example.hantalk.repository.PostRepository;
import com.example.hantalk.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;

    // 게시물 등록
    @Transactional
    public PostDTO createPost(PostDTO dto) {
        Post post = toEntity(dto);
        Post savedPost = postRepository.save(post);
        return toDto(savedPost);
    }

    // 게시물 수정
    @Transactional
    public PostDTO updatePost(int postId, PostDTO dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setArchive(dto.getArchive());
        // `postRepository.save(post)`는 생략 가능 (JPA 변경 감지 기능)
        return toDto(post);
    }

    // 단일 게시물 조회
    @Transactional(readOnly = true)
    public PostDTO getPost(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));
        return toDto(post);
    }

    // 게시물 삭제
    @Transactional
    public void deletePost(int postId) {
        postRepository.deleteById(postId);
    }

    // 카테고리 ID로 게시물 검색 (List → Page로 변경)
    @Transactional(readOnly = true)
    public Page<PostDTO> searchByCategory(int categoryId, Pageable pageable) {
        Page<Post> posts = postRepository.findByCategory_CategoryId(categoryId, pageable);
        return posts.map(this::toDto);
    }

    // 키워드, 검색 타입, 카테고리로 검색 (통합 메서드)
    @Transactional(readOnly = true)
    public Page<PostDTO> searchPosts(int categoryId, String keyword, String searchType, Pageable pageable) {
        Page<Post> posts;
        if (keyword == null || keyword.trim().isEmpty()) {
            posts = postRepository.findByCategory_CategoryId(categoryId, pageable);
        } else {
            switch (searchType) {
                case "title":
                    posts = postRepository.findByCategory_CategoryIdAndTitleContaining(categoryId, keyword, pageable);
                    break;
                case "content":
                    posts = postRepository.findByCategory_CategoryIdAndContentContaining(categoryId, keyword, pageable);
                    break;
                case "author":
                    posts = postRepository.findByCategory_CategoryIdAndUsers_UsernameContaining(categoryId, keyword, pageable);
                    break;
                default: // 제목 또는 내용으로 검색
                    posts = postRepository.findByCategory_CategoryIdAndTitleContainingOrCategory_CategoryIdAndContentContaining(categoryId, keyword, categoryId, keyword, pageable);
                    break;
            }
        }
        return posts.map(this::toDto);
    }

    // Entity → DTO 변환
    private PostDTO toDto(Post post) {
        PostDTO dto = new PostDTO();
        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setArchive(post.getArchive());
        dto.setViewCount(post.getViewCount());
        dto.setCreateDate(post.getCreateDate());
        dto.setUpdateDate(post.getUpdateDate());
        if (post.getUsers() != null) {
            dto.setUserNo(post.getUsers().getUserNo());
  //          dto.setUsername(post.getUsers().getUsername());
        }
        if (post.getCategory() != null) {
            dto.setCategoryId(post.getCategory().getCategoryId());
        }
        return dto;
    }

    // DTO → Entity 변환
    private Post toEntity(PostDTO dto) {
        Post post = new Post();
        post.setPostId(dto.getPostId());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setArchive(dto.getArchive());

        // userNo가 0보다 큰 값인지 확인
        if (dto.getUserNo() > 0) {
            Users users = usersRepository.findById(dto.getUserNo())
                    .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
            post.setUsers(users);
        }

        // Category 엔티티 조회 및 설정
        if (dto.getCategoryId() > 0) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));
            post.setCategory(category);
        } else {
            throw new IllegalArgumentException("카테고리 ID가 누락되었습니다.");
        }

        return post;
    }
}