package com.example.hantalk.service;

import com.example.hantalk.entity.Category;
import com.example.hantalk.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public boolean isPresent() {
        if(categoryRepository.count() == 0){
            return false;
        }
        return true;
    }

    public void setCategory() {
        Category category1 = new Category();
        category1.setCategoryName("공지사항");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setCategoryName("커뮤니티");
        categoryRepository.save(category2);

        Category category3 = new Category();
        category3.setCategoryName("문의사항");
        categoryRepository.save(category3);
    }
}
