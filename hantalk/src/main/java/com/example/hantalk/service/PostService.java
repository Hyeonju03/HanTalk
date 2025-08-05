package com.example.hantalk.service;


import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.entity.Category;
import com.example.hantalk.entity.Post;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.CategoryRepository;
import com.example.hantalk.repository.PostRepository;
import com.example.hantalk.repository.UsersRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final UsersRepository usersRepository;
    private final CategoryRepository categoryRepository;  // 추가



    public PostService(PostRepository postRepository, ModelMapper modelMapper, UsersRepository userRepository, UsersRepository usersRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
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
        return modelMapper.map(post, PostDTO.class);
    }

    public void setInsert(PostDTO dto) {
        if (dto.getUserNo() == null || dto.getUserNo() == 0) {
            throw new IllegalArgumentException("userNo가 유효하지 않습니다.");
        }

        Users user = usersRepository.findById(dto.getUserNo())
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        Post post = modelMapper.map(dto, Post.class);


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

    public void setDelete(PostDTO dto) {
        Optional<Post> postOpt = postRepository.findById(dto.getPostId());
        postOpt.ifPresent(post -> postRepository.delete(post));
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
}

