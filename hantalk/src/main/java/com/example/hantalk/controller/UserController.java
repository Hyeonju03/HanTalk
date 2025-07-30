package com.example.hantalk.controller;

import com.example.hantalk.dto.UsersDTO;
import com.example.hantalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    UserService service;

    // 생성
    @GetMapping("/user/signup")
    public String signUp() {

        return "회원가입 페이지";
    }

    @PostMapping("/user/signup")
    public String signUpProc(@ModelAttribute UsersDTO usersDTO) {
        if (!isDTOok(usersDTO)) {
            return "회원가입 폐이지";
        }

        boolean isSuccess = service.signUp(usersDTO);
        if (isSuccess) {
            return "redirect:/로그인 폐이지";
        } else {
            return "회원가입 폐이지";
        }
    }

    //로그인
    @GetMapping("/user/login")
    public String login() {
        return "redirect:/로그인 페이지";
    }

    @PostMapping("/user/login")
    public String loginProc(@RequestParam String userid, @RequestParam String password) {
        if (!isLoginok(userid, password)) {
            return "redirect:/로그인 페이지";
        }
        Map<String, Object> result = service.login(userid, password);
        boolean success = (boolean) result.get("isSuccess");
        String role = (String) result.get("role");
        if (success) {
            // 세션 생성
            return "메인폐이지";
        } else {
            return "redirect:/로그인 페이지";
        }
    }

    //로그아웃
    @GetMapping("/user/logout")
    public String logout() {
        //세션 삭제
        //쿠키 삭제?
        return "redirect:/로그인 페이지";
    }

    //리스트와 상세보기
    @GetMapping("/user/admin/list")
    public String list() {
        //service.getAllUser();
        // 검색 등 기능 쓸경우 키워드나 페이지 받을 변수 필요
        return "유저 목록 페이지";
    }

    @GetMapping("/user/read")
    public String view() {

        return "유저 상세정보 페이지";
    }

    //수정
    @GetMapping("/user/update")
    public String update(@RequestParam String userid) {
        String sessionUserId = "";
        String role = "";

        if (service.isRoleok(userid, sessionUserId, role)) {
            return "유저 정보 수정 페이지";
        }
        return "권한 없음 폐이지";
    }

    @PostMapping("/user/update")
    public String updateProc(@ModelAttribute UsersDTO usersDTO) {
        service.update(usersDTO);
        return "메인페이지";
    }

    //삭제
    @GetMapping("/user/delete")
    public String delete(@RequestParam String userid) {
        String sessionUserId = "";
        String role = "";
        if (service.isRoleok(userid, sessionUserId, role)) {
            service.signOut(userid);
            //세션 삭제
            return "redirect:/로그인 페이지";
        }

        return "권한 없음 페이지";
    }

    //아이디 패스워드 찾기 폐이지
    //아이디찾기
    @PostMapping("/user/findID")
    public ResponseEntity<Map<String, String>> getFindId(@RequestParam String name, @RequestParam String email) {
        String userId = service.findId(name, email);
        Map<String, String> response = new HashMap<>();
        if(userId != null) {
            response.put("status", "success");
            response.put("userId", userId);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "fail");
            return ResponseEntity.status(404).body(response);
        }
    }
    //패스워드찾기
    @PostMapping("/user/findPW")
    public ResponseEntity<Map<String, String>> getFindPw(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String userid) {

        String tempPw = service.findPw(name, email, userid);
        Map<String, String> result = new HashMap<>();

        if (tempPw != null) {
            result.put("status", "success");
            result.put("tempPw", tempPw);
            return ResponseEntity.ok(result);
        } else {
            result.put("status", "fail");
            return ResponseEntity.status(404).body(result);
        }
    }

    
    // 입력값 검증 메서드
    // 중복체크 등 DB 관련은 서비스에서 따로 하고 여기선 입력값 검증
    private boolean isDTOok(UsersDTO userdto) {
        if (userdto == null) return false;
        if (userdto.getUserId() == null || userdto.getUserId().length() < 4) return false;
        if (userdto.getPassword() == null || userdto.getPassword().length() < 6) return false;
        if (userdto.getEmail() == null || !userdto.getEmail().contains("@")) return false;

        return true;
    }

    private boolean isLoginok(String userid, String password) {
        if (userid == null || userid.length() < 4) return false;
        if (password == null || password.length() < 6) return false;
        return true;
    }
}
