package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.dto.UsersDTO;
import com.example.hantalk.entity.Users;
import com.example.hantalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    UserService service;

    @GetMapping("/user/test")
    public String test() {
        return "userPage/UserTestPage"; //기능 테스트 폐이지
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
            dto.setBirth(19900505);
            dto.setStatus("ACTIVE");
            dto.setPoint(0);

            dto.setProfileImage(null);

            service.signUp(dto);
            System.out.println("✅ 테스트용 기본 유저 생성 완료");
            System.out.println("✅ id : user0  / pw : user1234");
        }     
    }

    // 생성
    @GetMapping("/user/signup")
    public String signUp(HttpSession session) {
        if (SessionUtil.isLoggedIn(session)) {
            System.out.println("이미 로그인한 상태입니다.");
        }
        return "userPage/UserLoginPage";
    }

    @PostMapping("/user/signup")
    public String signUpProc(@ModelAttribute UsersDTO usersDTO, HttpSession session, @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile) {
        if (SessionUtil.isLoggedIn(session)) {
            System.out.println("이미 로그인한 상태입니다.");
            session.invalidate();
        }
        if (!isDTOOk(usersDTO)) {
            return "userPage/UserLoginPage";
        }
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            String fileName = profileImageFile.getOriginalFilename();
            usersDTO.setProfileImage(fileName);
        }

        boolean isSuccess = service.signUp(usersDTO);

        return isSuccess ? "redirect:/user/login" : "userPage/UserLoginPage";
    }

    //로그인
    @GetMapping("/user/login")
    public String login(HttpSession session) {

        if (SessionUtil.isLoggedIn(session)) {
            System.out.println("이미 로그인한 상태입니다.");
            session.invalidate();
        }
        return "userPage/UserLoginPage";
    }

    @PostMapping("/user/login")
    public String loginProc(@RequestParam String userId, @RequestParam String password, HttpSession session) {
        if (!isLoginOk(userId, password)) {
            return "redirect:/user/login";
        }
        Map<String, Object> result = service.login(userId, password);
        boolean success = (boolean) result.get("isSuccess");
        String role = (String) result.get("role");
        if (success) {
            session.setAttribute("userId", userId);
            Users userEntity = service.getUserEntity(userId);

            if (role.equals("ADMIN")) {
                session.setAttribute("role", "ADMIN");
            } else {
                UsersDTO user = service.getUserOne(userId);
                session.setAttribute("userNo", user.getUserNo()); 
                session.setAttribute("role", "USER");
                session.setAttribute("loginUser", userEntity);
            }
            return "userPage/UserTestPage"; // ✅ templates/MainPage.html 필요
        } else {
            return "redirect:/user/login";
        }
    }

    //로그아웃
    @GetMapping("/user/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }

    //리스트와 상세보기
    @GetMapping("/user/admin/list")
    public String list(Model model, HttpSession session) {
        String userid = "0";
        String sessionUserId = SessionUtil.getLoginUserId(session);
        String role = SessionUtil.getRole(session);
        if (service.isRoleOk(userid, sessionUserId, role)) {
            List<UsersDTO> userlist = service.getUserList();
            model.addAttribute("userList", userlist);
            return "userPage/UserListPage";
        }
        return "RoleERROR";
    }

    @GetMapping("/user/admin/read")
    public String view(@RequestParam String userId, Model model, HttpSession session) {
        String userid = "0";
        String sessionUserId = SessionUtil.getLoginUserId(session);
        String role = SessionUtil.getRole(session);
        if (service.isRoleOk(userid, sessionUserId, role)) {
            UsersDTO user = service.getUserOne(userId);
            model.addAttribute("user", user);

            return "userPage/UserDetailPage";
        }
        return "RoleERROR";
    }

    //수정
    @GetMapping("/user/admin/update")
    public String update(@RequestParam String userId, HttpSession session, Model model) {
        String sessionUserId = SessionUtil.getLoginUserId(session);
        String role = SessionUtil.getRole(session);

        if (service.isRoleOk(userId, sessionUserId, role)) {
            UsersDTO user = service.getUserOne(userId);
            model.addAttribute("user", user);
            return "userPage/UserUpdatePage";
        }
        return "RoleERROR";
    }

    @PostMapping("/user/admin/update")
    public String updateProc(@ModelAttribute UsersDTO usersDTO) {
        service.update(usersDTO);
        return "userPage/UserTestPage";
    }

    //삭제
    @GetMapping("/user/admin/delete")
    public String delete(@RequestParam String userId, HttpSession session) {
        String sessionUserId = SessionUtil.getLoginUserId(session);
        String role = SessionUtil.getRole(session);
        if (service.isRoleOk(userId, sessionUserId, role)) {
            service.signOut(userId);
            session.invalidate();
            return "redirect:/user/login";
        }

        return "RoleERROR";
    }

    //아이디 패스워드 찾기 폐이지
    //페이지
    @GetMapping("/user/findIDPW")
    public String getFind() {
        return "userPage/UserFindStat";
    }

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
            @RequestParam String userId) {

        String tempPw = service.findPw(name, email, userId);
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
    private boolean isDTOOk(UsersDTO userdto) {
        if (userdto == null) return false;
        if (userdto.getUserId() == null || userdto.getUserId().length() < 4) return false;
        if (userdto.getPassword() == null || userdto.getPassword().length() < 3) return false;
        if (userdto.getEmail() == null || !userdto.getEmail().contains("@")) return false;

        return true;
    }

    private boolean isLoginOk(String userid, String password) {
        if (userid == null || userid.length() < 4) return false;
        if (password == null || password.length() < 3) return false;
        return true;
    }

    // ======== 비동기처리 =======
    @GetMapping("/user/isIdAvail")
    @ResponseBody
    public boolean isIdAvail(@RequestParam String userId) {
        return service.isIdAvail(userId);
    }

    @GetMapping("user/isEmailAvail")
    @ResponseBody
    public boolean isEmailAvail(@RequestParam String email) {
        return service.isEmailAvail(email);
    }
}
