package com.example.hantalk.service;

import com.example.hantalk.dto.UserDTO;
import com.example.hantalk.entity.Admin;
import com.example.hantalk.entity.User;
import com.example.hantalk.repository.AdminRepository;
import com.example.hantalk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AdminRepository adminRepository;

    public boolean signUp(UserDTO userDTO) {
        User user = toEntity(userDTO);
        //이미 존재하는 아이디인지 체크
        //-회원체크
        if (userRepository.existsByUserId(user.getUserId())) {
            return false;
        }
        //-관리자체크
        else if (adminRepository.existsByUserId(user.getUserId())) {
            return false;
        }

        //둘다 통과하면
        user.setPassword(encode(user.getPassword()));
        user.setJoinDate(LocalDateTime.now());

        userRepository.save(user);

        return true;
    }

    public Map<String, Object> login(String userid, String password) {
        Map<String, Object> result = new HashMap<>();

        //회원 체크
        Optional<User> userOpt = userRepository.findByUserId(userid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(encode(password))) {
                if ("active".equalsIgnoreCase(user.getStatus())) {
                    result.put("isSuccess", true);
                    result.put("role", "USER");
                } else {
                    result.put("isSuccess", false);
                    result.put("role", "BLOCKED"); // 상태가 비활성화된 경우 구분 가능
                }
                return result;
            }
        }

        // 관리자 체크
        Optional<Admin> adminOpt = adminRepository.findByUserId(userid);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (admin.getPassword().equals(encode(password))) {
                result.put("isSuccess", true);
                result.put("role", "ADMIN");
                return result;
            }
        }

        result.put("isSuccess", false);
        result.put("role", "NONE");

        return result;
    }

    public void update(UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findByUserId(userDTO.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            user.setNickname(userDTO.getNickname());
            user.setProfileImage(userDTO.getProfileImage());
            user.setBirth(userDTO.getBirth());
            user.setStatus(userDTO.getStatus());
            user.setPoint(userDTO.getPoint());

            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(encode(userDTO.getPassword()));
            }

            userRepository.save(user);
        }
    }

    public void signOut(String userid) {
        Optional userOpt = userRepository.findByUserId(userid);
        if (userOpt.isPresent()) {
            User user = (User)userOpt.get();
            user.setStatus("SignOut"); // 상태값 변경
            userRepository.save(user);
        }
    }

    public boolean isRoleok(String targetUserId, String sessionUserId, String role) {
        return "ADMIN".equals(role) || targetUserId.equals(sessionUserId);
    }

    private User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setUserNo(dto.getUserNo());
        user.setUserId(dto.getUserId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setNickname(dto.getNickname());
        user.setProfileImage(dto.getProfileImage());
        user.setJoinDate(dto.getJoinDate());
        user.setBirth(dto.getBirth());
        user.setStatus(dto.getStatus());
        user.setPoint(dto.getPoint());
        return user;
    }

    private UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setUserNo(user.getUserNo());
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setNickname(user.getNickname());
        dto.setProfileImage(user.getProfileImage());
        dto.setJoinDate(user.getJoinDate());
        dto.setBirth(user.getBirth());
        dto.setStatus(user.getStatus());
        dto.setPoint(user.getPoint());
        return dto;
    }


    private String encode(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String findId(String name, String email) {
        //찾은 아이디 리턴
        return "작성중";
    }

    public String findPw(String name, String email, String userid) {
        //성공하면 임시비번 발급
        return "작성중";
    }
}
