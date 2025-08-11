package com.example.hantalk.service;

import com.example.hantalk.entity.Category;
import com.example.hantalk.repository.CategoryRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @PostConstruct
    public void initCategories() {
        if (categoryRepository.count() == 0) {
            Category c1 = new Category();
            c1.setCategoryName("공지사항");
            categoryRepository.save(c1);

            Category c2 = new Category();
            c2.setCategoryName("커뮤니티");
            categoryRepository.save(c2);

            Category c3 = new Category();
            c3.setCategoryName("문의사항");
            categoryRepository.save(c3);
        }
    }


    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    //카테고리 페이지 나누는 거 관련
    public Integer getCategoryIdByName(String categoryName) {
        return categoryRepository.findByCategoryNameIgnoreCase(categoryName.trim())
                .map(Category::getCategoryId)
                .orElse(null);
    }

    public String getCategoryNameById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getCategoryName)
                .orElse(null);
    }
}