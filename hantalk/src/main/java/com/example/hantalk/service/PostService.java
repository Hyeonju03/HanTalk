package com.example.hantalk.service;


import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.entity.Post;
import com.example.hantalk.repository.PostRepository;
import org.hibernate.query.Page;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public PostService(PostRepository postRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
    }

    public List<PostDTO> getSelectAll() {

        List<Post> entityList = postRepository.findAll();
        List<PostDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(modelMapper.map(entityList.get(i), PostDTO.class));

        }
        return dtoList;

    }
    public PostDTO getSelectOne(PostDTO dto){
        Optional<Post> on = postRepository.findById(dto.getPostId());
        if (!on.isPresent()){
            return  null;
        }
        Post post = on.get();
        return modelMapper.map(post, PostDTO.class);
    }

    public void setInsert(PostDTO dto){
        Post post = modelMapper.map(dto, Post.class);

        postRepository.save(modelMapper.map(dto, Post.class));
    }

    public void setUpdate(PostDTO dto){
        Optional<Post> on = postRepository.findById(dto.getPostId());
        if (!on.isPresent()) return;

        Post post = on.get();

        String updateTitle = dto.getTitle();
        if (!updateTitle.contains("(수정)")){
            updateTitle += "(수정)";

        post.setTitle(updateTitle);
        post.setContent(dto.getContent());
        post.setCategory(dto.getCategory());

        postRepository.save((post));
        }
    }

    public void setDelete(PostDTO dto){
        Post post = modelMapper.map(dto, Post.class);

        postRepository.delete(modelMapper.map(dto, Post.class));
    }

    public Page getPagePosts(int page, int i) {
        return null;
    }
}

