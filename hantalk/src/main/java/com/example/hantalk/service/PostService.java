package com.example.hantalk.service;

import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.entity.Category;
import com.example.hantalk.entity.Post;
import com.example.hantalk.repository.CategoryRepository;
import com.example.hantalk.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/postFiles";

    // 게시물 등록
    @Transactional
    public PostDTO createPost(PostDTO dto) {
        Post post = toEntity(dto);
        Post savedPost = postRepository.save(post);
        return toDto(savedPost);
    }

    // 첨부파일을 포함한 게시물 등록
    @Transactional
    public PostDTO createPostWithFile(PostDTO dto, MultipartFile file) {
        String savedFileName = saveFile(file);
        if (savedFileName != null) {
            dto.setArchive(savedFileName);
            // 원본 파일명을 DTO에 저장
            dto.setOriginalFileName(file.getOriginalFilename());
        }
        Post post = toEntity(dto); // DTO를 Entity로 변환하는 메서드
        Post savedPost = postRepository.save(post);
        return toDto(savedPost); // Entity를 DTO로 변환
    }

    // UUID 파일명으로 원본 파일명을 찾아주는 메서드
    public String getOriginalFileName(String savedFileName) {
        return postRepository.findByArchive(savedFileName)
                .map(Post::getOriginalFileName)
                .orElse(null);
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

    // 첨부파일을 포함한 게시물 수정 (파일 삭제 옵션 추가)
    @Transactional
    public PostDTO updatePostWithFile(int postId, PostDTO dto, MultipartFile file, Boolean deleteFile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        // case 1: 새로운 파일이 업로드된 경우
        if (file != null && !file.isEmpty()) {
            // 기존 파일이 있다면 삭제
            if (post.getArchive() != null) {
                deleteFile(post.getArchive());
            }
            // 새 파일을 저장하고 archive와 originalFileName 필드 업데이트
            String savedFileName = saveFile(file);
            post.setArchive(savedFileName);
            post.setOriginalFileName(file.getOriginalFilename());
        }
        // case 2: 파일 삭제 체크박스가 선택된 경우
        else if (Boolean.TRUE.equals(deleteFile)) {
            // 기존 파일이 있다면 삭제
            if (post.getArchive() != null) {
                deleteFile(post.getArchive());
            }
            // archive와 originalFileName 필드를 모두 null로 설정
            post.setArchive(null);
            post.setOriginalFileName(null);
        }
        // case 3: 아무런 파일 변경이 없는 경우
        // 기존 archive 값과 originalFileName 값은 변경하지 않습니다.
        else {
            post.setArchive(dto.getArchive());
        }

        // JPA의 변경 감지(Dirty Checking) 기능으로 인해 postRepository.save(post)는 생략 가능
        return toDto(post);
    }


    // 단일 게시물 조회 및 조회수 증가
    @Transactional // readOnly = true 제거 또는 false로 변경
    public PostDTO getPost(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        // 조회수 증가 로직
        post.setViewCount(post.getViewCount() + 1);

        // 변경 감지(Dirty Checking) 기능으로 인해 postRepository.save(post)는 생략 가능

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
                    posts = postRepository.findByCategory_CategoryIdAndContentContaining(categoryId, keyword, pageable);
                    break;
            }
        }
        return posts.map(this::toDto);
    }

    // 홈 화면에 표시할 최신 게시물 리스트를 가져오는 메서드
    @Transactional(readOnly = true)
    public List<PostDTO> getLatestPosts(int categoryId, int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Post> latestPosts = postRepository.findByCategory_CategoryId(categoryId, pageable);
        return latestPosts.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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
        dto.setOriginalFileName(post.getOriginalFileName());

        if (post.getUsers() != null) {
            dto.setUsers(post.getUsers());
        }
        if (post.getCategory() != null) {
            dto.setCategory(post.getCategory());
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
        post.setUsers(dto.getUsers());
        post.setOriginalFileName(dto.getOriginalFileName());

        // Category 엔티티 조회 및 설정
        if (dto.getCategory().getCategoryId() > 0) {

            Category category = categoryRepository.findById(dto.getCategory().getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));
            post.setCategory(category);
        } else {
            throw new IllegalArgumentException("카테고리 ID가 누락되었습니다.");
        }

        return post;
    }

    // 파일 저장 로직 (ResourceService의 saveFile을 참고)
    private String saveFile(MultipartFile file) {
        try {

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }


            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }


            String savedFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(savedFileName);
            file.transferTo(filePath.toFile());


            return savedFileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 파일 삭제 로직 추가
    private void deleteFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            Path filePath = Paths.get(uploadDir, fileName);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // 로그를 남기거나 예외 처리를 추가할 수 있습니다.
                System.err.println("파일 삭제 실패: " + filePath);
                e.printStackTrace();
            }
        }
    }

}