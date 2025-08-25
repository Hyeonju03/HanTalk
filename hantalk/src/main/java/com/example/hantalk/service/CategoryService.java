package com.example.hantalk.service;

import com.example.hantalk.entity.Category;
import com.example.hantalk.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        // 모든 카테고리 목록을 조회하여 반환합니다.
        return categoryRepository.findAll();
    }

    public boolean isPresent() {
        // 카테고리 데이터가 이미 존재하는지 확인합니다.
        return categoryRepository.count() > 0;
    }

    @Transactional
    public void setCategory() {
        // 초기 카테고리 데이터를 생성하고 saveAll()을 사용하여 한 번에 저장합니다.
        // ID를 명시적으로 설정하면 다른 로직에서 해당 ID를 참조할 때 일관성을 유지할 수 있습니다.
        Category notice = new Category();
        notice.setCategoryName("공지사항");

        Category community = new Category();
        community.setCategoryName("커뮤니티");

        Category inquiry = new Category();
        inquiry.setCategoryName("문의사항");

        categoryRepository.saveAll(Arrays.asList(notice, community, inquiry));
    }
}
