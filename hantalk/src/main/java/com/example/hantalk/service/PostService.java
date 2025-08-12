package com.example.hantalk.service;


import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.entity.Category;
import com.example.hantalk.entity.Post;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.CategoryRepository;
import com.example.hantalk.repository.CommentRepository;
import com.example.hantalk.repository.PostRepository;
import com.example.hantalk.repository.UsersRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final UsersRepository usersRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;



    public PostService(PostRepository postRepository, ModelMapper modelMapper, UsersRepository usersRepository, CategoryRepository categoryRepository,
                       CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
    }



    public List<PostDTO> getSelectAll() {

        List<Post> entityList = postRepository.findAll();
        List<PostDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(modelMapper.map(entityList.get(i), PostDTO.class));

        }
        return dtoList;
    }

    public PostDTO getSelectOne(PostDTO dto) {
        Optional<Post> on = postRepository.findById(dto.getPostId());
        if (!on.isPresent()) {
            return null;
        }
        Post post = on.get();

        PostDTO postDTO = modelMapper.map(post, PostDTO.class);

        if (post.getUsers() != null) {
            postDTO.setUsername(post.getUsers().getUsername());
            postDTO.setUserNo(post.getUsers().getUserNo());  // userNo도 확실히 넣어주기
        }

        if (post.getCategory() != null) {
            postDTO.setCategoryId(post.getCategory().getCategoryId());
        }

        return postDTO;
    }

    public void setInsert(PostDTO dto) {
        if (dto.getUserNo() == null || dto.getUserNo() == 0) {
            throw new IllegalArgumentException("userNo가 유효하지 않습니다.");
        }

        Users user = usersRepository.findById(dto.getUserNo())
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        Post post = new Post();

        // DTO 필드 하나씩 직접 세팅 (명확하고 문제 없도록)
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setViewCount(dto.getViewCount());
        post.setArchive(dto.getArchive());
        // 작성자 세팅
        post.setUsers(user);

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));
            post.setCategory(category);
        }

        postRepository.save(post);
    }


    public void setUpdate(PostDTO dto) {
        Optional<Post> on = postRepository.findById(dto.getPostId());
        if (!on.isPresent()) return;

        Post post = on.get();

        String updateTitle = dto.getTitle();
        if (!updateTitle.contains("(수정)")) {
            updateTitle += "(수정)";
        }

        post.setTitle(updateTitle);
        post.setContent(dto.getContent());

        if (dto.getCategory() != null) {
            post.setCategory(dto.getCategory());
        }

        postRepository.save(post);
    }

    @Transactional
    public void setDelete(PostDTO dto) {
        Optional<Post> postOpt = postRepository.findById(dto.getPostId());
        postOpt.ifPresent(post -> {
            // 댓글 먼저 삭제
            //commentRepository.deleteByPost(post);

            // 게시글 삭제
            postRepository.delete(post);
        });
    }

    public Page<PostDTO> getPagePosts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page postPage = postRepository.findAll(pageable);

        return postPage.map(post -> modelMapper.map(post, PostDTO.class));
    }

    public Page<PostDTO> getPostsByCategory(int categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> postPage = postRepository.findByCategory_CategoryId(categoryId, pageable);
        return postPage.map(post -> modelMapper.map(post, PostDTO.class));
    }

    public void increaseViewCount(int postId) {
        postRepository.findById(postId).ifPresent(post -> {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        });

    }

    public Page<PostDTO> searchPostsByKeyword(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Post> postPage = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
        return postPage.map(post -> modelMapper.map(post, PostDTO.class));
    }

    public String getOriginalFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        int underscoreIndex = fileName.indexOf("_");
        if (underscoreIndex == -1) {
            return fileName;
        }
        return fileName.substring(underscoreIndex + 1);
    }

    public String getStoredFileNameById(String fileId) {
        return postRepository.findArchiveByFileId(fileId);
    }

    public String getOriginalFileNameById(String fileId) {
        return postRepository.findOriginalNameByFileId(fileId);
    }
    public Collection<Post> getPostsByCategories(List<Integer> categoryIds) {
        return postRepository.findByCategoryIdIn(categoryIds);
    }
    public List<PostDTO> getPostsByCategoryId(int categoryId) {
        List<Post> posts = postRepository.findByCategory_CategoryId(categoryId);

        List<PostDTO> dtoList = new ArrayList<>();
        for (Post post : posts) {
            PostDTO dto = modelMapper.map(post, PostDTO.class);
            dtoList.add(dto);
        }
        return dtoList;
    }


    @Transactional
    public void deletePostsByIds(List<Long> postIds) {
        for (Long postId : postIds) {
            postRepository.findById(Math.toIntExact(postId)).ifPresent(post -> postRepository.delete(post));
        }
    }

    public PostDTO getSelectOneById(int postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new RuntimeException("해당 게시글을 찾을 수 없습니다.");
        }
        Post post = optionalPost.get();
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);

        if (post.getUsers() != null) {
            postDTO.setUserNo(post.getUsers().getUserNo());
            postDTO.setUsername(post.getUsers().getUsername());
        }

        if (post.getCategory() != null) {
            postDTO.setCategoryId(post.getCategory().getCategoryId());
        }

        return postDTO;
    }

}

