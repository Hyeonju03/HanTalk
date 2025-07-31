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

    @GetMapping("/user/test")
    public String test() {

        return "UserTestPage";
    }

    public void testUserspawn() {
        if (!service.isAdminThere()) {
            service.createDefaultAdmin();
            System.out.println("✅ 테스트용 기본 관리자 생성 완료");
            System.out.println("✅ id : admin  / pw : 11111");
        }
        if (service.getUserList().isEmpty()) {
            UsersDTO dto = new UsersDTO();
            dto.setUserId("user0");
            dto.setPassword("user1234");
            dto.setName("유저");
            dto.setEmail("admin@example.com");
            dto.setNickname("유저닉네임");
            dto.setBirth(19900505);          // 테스트용 생년
            dto.setStatus("ACTIVE");     // 상태 지정
            dto.setPoint(0);             // 초기 포인트

            dto.setProfileImage(null);

            service.signUp(dto);
            System.out.println("✅ 테스트용 기본 유저 생성 완료");
            System.out.println("✅ id : user0  / pw : user1234");
        }
    }


    // 생성
    @GetMapping("/user/signup")
    public String signUp() {
        //세션 있는지 체크해서 있으면(로그인상태이면) return 매인폐이지
        return "UserLoginPage";
    }

    @PostMapping("/user/signup")
    public String signUpProc(@ModelAttribute UsersDTO usersDTO) {
        if (!isDTOok(usersDTO)) {
            return "UserLoginPage";
        }

        boolean isSuccess = service.signUp(usersDTO);
        return isSuccess ? "redirect:/user/login" : "UserLoginPage";
    }

    //로그인
    @GetMapping("/user/login")
    public String login() {
        return "UserLoginPage";
    }

    @PostMapping("/user/login")
    public String loginProc(@RequestParam String userid, @RequestParam String password) {
        if (!isLoginok(userid, password)) {
            return "redirect:/user/login";
        }
        Map<String, Object> result = service.login(userid, password);
        boolean success = (boolean) result.get("isSuccess");
        String role = (String) result.get("role");
        if (success) {
            return "MainPage"; // ✅ templates/MainPage.html 필요
        } else {
            return "redirect:/user/login";
        }
    }

    //로그아웃
    @GetMapping("/user/logout")
    public String logout() {
        //세션 삭제
        //쿠키 삭제?
        return "redirect:/user/login";
    }

    //리스트와 상세보기
    @GetMapping("/user/admin/list")
    public String list() {
        //service.getAllUser();
        // 검색 등 기능 쓸경우 키워드나 페이지 받을 변수 필요
        return "UserListPage";
    }

    @GetMapping("/user/read")
    public String view(@RequestParam String userId) {

        return "UserDetailPage";
    }

    //수정
    @GetMapping("/user/update")
    public String update(@RequestParam String userid) {
        String sessionUserId = "";
        String role = "";

        if (service.isRoleok(userid, sessionUserId, role)) {
            return "UserUpdatePage";
        }
        return "권한 없음 폐이지";
    }

    @PostMapping("/user/update")
    public String updateProc(@ModelAttribute UsersDTO usersDTO) {
        service.update(usersDTO);
        return "MainPage";
    }

    //삭제
    @GetMapping("/user/delete")
    public String delete(@RequestParam String userid) {
        String sessionUserId = "";
        String role = "";
        if (service.isRoleok(userid, sessionUserId, role)) {
            service.signOut(userid);
            //세션 삭제
            return "redirect:/UserLoginPage";
        }

        return "권한 없음 페이지";
    }

    //아이디 패스워드 찾기 폐이지
    //아이디찾기
    @PostMapping("/user/findID")
    public ResponseEntity<Map<String, String>> getFindId(@RequestParam String name, @RequestParam String email) {
        String userId = service.findId(name, email);
        Map<String, String> response = new HashMap<>();
        if (userId != null) {
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
