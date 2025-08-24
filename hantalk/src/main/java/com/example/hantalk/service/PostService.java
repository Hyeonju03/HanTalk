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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/postFiles";

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>();
    static {
        ALLOWED_EXTENSIONS.add("pdf"); ALLOWED_EXTENSIONS.add("doc"); ALLOWED_EXTENSIONS.add("docx");
        ALLOWED_EXTENSIONS.add("xls"); ALLOWED_EXTENSIONS.add("xlsx"); ALLOWED_EXTENSIONS.add("ppt");
        ALLOWED_EXTENSIONS.add("pptx"); ALLOWED_EXTENSIONS.add("hwp"); ALLOWED_EXTENSIONS.add("jpg");
        ALLOWED_EXTENSIONS.add("jpeg"); ALLOWED_EXTENSIONS.add("png"); ALLOWED_EXTENSIONS.add("gif");
        ALLOWED_EXTENSIONS.add("zip"); ALLOWED_EXTENSIONS.add("rar"); ALLOWED_EXTENSIONS.add("txt");
    }

    @Transactional
    public PostDTO createPost(PostDTO dto) {
        Post post = toEntity(dto);
        Post savedPost = postRepository.save(post);
        return toDto(savedPost);
    }

    @Transactional
    public PostDTO createPostWithFile(PostDTO dto, MultipartFile file) {
        String savedFileName = saveFile(file);
        if (savedFileName != null) {
            dto.setArchive(savedFileName);
            dto.setOriginalFileName(file.getOriginalFilename());
        }
        Post post = toEntity(dto);
        Post savedPost = postRepository.save(post);
        return toDto(savedPost);
    }

    public String getOriginalFileName(String savedFileName) {
        return postRepository.findByArchive(savedFileName)
                .map(Post::getOriginalFileName)
                .orElse(null);
    }

    @Transactional
    public PostDTO updatePost(int postId, PostDTO dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setArchive(dto.getArchive());
        return toDto(post);
    }

    @Transactional
    public PostDTO updatePostWithFile(int postId, PostDTO dto, MultipartFile file, Boolean deleteFile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        if (file != null && !file.isEmpty()) {
            if (post.getArchive() != null) {
                deleteFile(post.getArchive());
            }
            String savedFileName = saveFile(file);
            post.setArchive(savedFileName);
            post.setOriginalFileName(file.getOriginalFilename());
        } else if (Boolean.TRUE.equals(deleteFile)) {
            if (post.getArchive() != null) {
                deleteFile(post.getArchive());
            }
            post.setArchive(null);
            post.setOriginalFileName(null);
        } else {
            post.setArchive(dto.getArchive());
            post.setOriginalFileName(dto.getOriginalFileName());
        }
        return toDto(post);
    }

    @Transactional
    public PostDTO getPost(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));
        post.setViewCount(post.getViewCount() + 1);
        return toDto(post);
    }

    @Transactional
    public void deletePost(int postId) {
        postRepository.deleteById(postId);
    }

    @Transactional(readOnly = true)
    public Page<PostDTO> searchByCategory(Integer categoryId, Pageable pageable) {
        Page<Post> posts = postRepository.findByCategory_CategoryId(categoryId, pageable);
        return posts.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PostDTO> searchPosts(Integer categoryId, String keyword, String searchType, Integer userNo, boolean isAdmin, Pageable pageable) {
        Page<Post> posts;
        if (categoryId != null && categoryId == 3 && userNo != null && !isAdmin) {
            if (keyword == null || keyword.trim().isEmpty()) {
                posts = postRepository.findByCategory_CategoryIdAndUsers_UserNo(categoryId, userNo, pageable);
            } else {
                switch (searchType) {
                    case "title":
                        posts = postRepository.findByCategory_CategoryIdAndUsers_UserNoAndTitleContaining(categoryId, userNo, keyword, pageable);
                        break;
                    case "content":
                        posts = postRepository.findByCategory_CategoryIdAndUsers_UserNoAndContentContaining(categoryId, userNo, keyword, pageable);
                        break;
                    case "author":
                        posts = postRepository.searchByUserAndAuthor(categoryId, userNo, keyword, pageable);
                        break;
                    default:
                        posts = postRepository.searchByTitleOrContentAndUser(categoryId, userNo, keyword, pageable);
                        break;
                }
            }
        }
        else if (categoryId == null && isAdmin) {
            if (keyword == null || keyword.trim().isEmpty()) {
                posts = postRepository.findAll(pageable);
            } else {
                switch (searchType) {
                    case "title":
                        posts = postRepository.findByTitleContaining(keyword, pageable);
                        break;
                    case "content":
                        posts = postRepository.findByContentContaining(keyword, pageable);
                        break;
                    case "author":
                        posts = postRepository.findByUsers_NicknameContaining(keyword, pageable);
                        break;
                    default:
                        posts = postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
                        break;
                }
            }
        }
        else {
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
                        posts = postRepository.findByCategory_CategoryIdAndUsers_NicknameContaining(categoryId, keyword, pageable);
                        break;
                    default:
                        posts = postRepository.searchByTitleOrContent(categoryId, keyword, pageable);
                        break;
                }
            }
        }
        return posts.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getLatestPosts(int categoryId, int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Post> latestPosts = postRepository.findByCategory_CategoryId(categoryId, pageable);
        return latestPosts.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

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

    private Post toEntity(PostDTO dto) {
        Post post = new Post();
        post.setPostId(dto.getPostId());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setArchive(dto.getArchive());
        post.setUsers(dto.getUsers());
        post.setOriginalFileName(dto.getOriginalFileName());

        if (dto.getCategory().getCategoryId() > 0) {
            Category category = categoryRepository.findById(dto.getCategory().getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));
            post.setCategory(category);
        } else {
            throw new IllegalArgumentException("카테고리 ID가 누락되었습니다.");
        }

        return post;
    }

    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return null;
            }
            String fileExtension = getExtension(originalFilename);
            if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다: " + fileExtension);
            }
            String savedFileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(savedFileName);
            file.transferTo(filePath.toFile());
            return savedFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }

    private void deleteFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            Path filePath = Paths.get(uploadDir, fileName);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("파일 삭제 실패: " + filePath);
                e.printStackTrace();
            }
        }
    }
}
