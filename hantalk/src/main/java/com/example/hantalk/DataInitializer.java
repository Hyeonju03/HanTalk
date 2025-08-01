package com.example.hantalk;

import com.example.hantalk.controller.UserController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserController userController;

    public DataInitializer(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void run(String... args) {
        System.out.println("🚀 서버 시작 시 테스트 데이터 확인 및 생성...");
        userController.testUserspawn();
    }
}