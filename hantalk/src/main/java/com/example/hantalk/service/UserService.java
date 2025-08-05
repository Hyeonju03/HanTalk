package com.example.hantalk.service;

import com.example.hantalk.dto.UsersDTO;
import com.example.hantalk.entity.Admin;
import com.example.hantalk.entity.Learning_Log;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.AdminRepository;
import com.example.hantalk.repository.Learning_LogRepository;
import com.example.hantalk.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UsersRepository userRepository;
    private final AdminRepository adminRepository;
    private final Learning_LogRepository learningLogRepository;

    public List<UsersDTO> getUserList() {
        List<Users> users = userRepository.findAll();
        List<UsersDTO> userList = new ArrayList<UsersDTO>();
        for (Users user : users) {
            UsersDTO dto = toDTO(user);
            userList.add(dto);
        }
        return userList;
    }

    public UsersDTO getUserOne(String userId) {
        Optional<Users> userOpt = userRepository.findByUserId(userId);

        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            return toDTO(user);
        } else {
            return null;
        }
    }

    public boolean signUp(UsersDTO usersDTO) {
        Users users = toEntity(usersDTO);
        //이미 존재하는 아이디인지 체크
        //-회원체크
        if (userRepository.existsByUserId(users.getUserId())) {
            return false;
        }
        //-관리자체크
        else if (adminRepository.existsByAdminId(users.getUserId())) {
            return false;
        }

        //둘다 통과하면
        users.setStatus("ACTIVE");
        users.setPassword(encode(users.getPassword()));
        users.setJoinDate(LocalDateTime.now());
        users.setLastLogin(LocalDateTime.now());

        userRepository.save(users);

        return true;
    }

    public Map<String, Object> login(String userid, String password) {
        Map<String, Object> result = new HashMap<>();

        //회원 체크
        Optional<Users> userOpt = userRepository.findByUserId(userid);
        if (userOpt.isPresent()) {
            Users users = userOpt.get();

            System.out.println(users.getUserId());

            if (users.getPassword().equals(encode(password))) {
                if ("active".equalsIgnoreCase(users.getStatus())) {
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
        Optional<Admin> adminOpt = adminRepository.findByAdminId(userid);
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

    public void setLearningLog(String userId, int lessonNo) {
        Optional<Users> getUserOpt = userRepository.findByUserId(userId);
        LocalDateTime today = LocalDateTime.now();
        if (getUserOpt.isEmpty()) {
            return;
        }

        Users user = getUserOpt.get();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Optional<Learning_Log> logOpt = learningLogRepository
                .findByUsers_UserNoAndLearningDateBetween(user.getUserNo(), startOfDay, endOfDay);

        Learning_Log log;

        if (logOpt.isPresent()) {
            log = logOpt.get();
        } else {
            log = new Learning_Log();
            log.setUsers(user);
            log.setLearningDate(LocalDateTime.now());
            log.setLearning1Count(0);
            log.setLearning2Count(0);
            log.setLearning3Count(0);
            log.setLearning4Count(0);
        }
        
        if (lessonNo != 0) {
            switch (lessonNo) {
                case 1 -> log.setLearning1Count(log.getLearning1Count() + 1);
                case 2 -> log.setLearning2Count(log.getLearning2Count() + 1);
                case 3 -> log.setLearning3Count(log.getLearning3Count() + 1);
                case 4 -> log.setLearning4Count(log.getLearning4Count() + 1);
            }
        }

        learningLogRepository.save(log);
    }


    public void update(UsersDTO usersDTO) {
        Optional<Users> userOpt = userRepository.findByUserId(usersDTO.getUserId());
        if (userOpt.isPresent()) {
            Users users = userOpt.get();

            users.setName(usersDTO.getName());
            users.setEmail(usersDTO.getEmail());
            users.setNickname(usersDTO.getNickname());
            users.setProfileImage(usersDTO.getProfileImage());
            users.setBirth(usersDTO.getBirth());
            users.setStatus(usersDTO.getStatus());
            users.setPoint(usersDTO.getPoint());

            if (usersDTO.getPassword() != null && !usersDTO.getPassword().isEmpty()) {
                users.setPassword(encode(usersDTO.getPassword()));
            }

            userRepository.save(users);
        }
    }

    public void signOut(String userid) {
        Optional<Users> userOpt = userRepository.findByUserId(userid);
        if (userOpt.isPresent()) {
            Users users = (Users) userOpt.get();
            users.setStatus("SignOut"); // 상태값 변경
            userRepository.save(users);
        }
    }

    public String findId(String name, String email) {
        Optional<Users> userOpt = userRepository.findByNameAndEmail(name, email); // ✅ 새로운 메서드 필요
        return userOpt.map(Users::getUserId).orElse(null); // 없으면 null 반환
    }

    public String findPw(String name, String email, String userid) {
        Optional<Users> userOpt = userRepository.findByUserId(userid);
        if (userOpt.isPresent()) {
            Users users = userOpt.get();
            if (users.getName().equals(name) && users.getEmail().equals(email)) {
                String tempPw = generateTempPw();
                users.setPassword(encode(tempPw));
                userRepository.save(users);
                return tempPw;
            }
        }
        return null;
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

    public boolean isRoleOk(String targetUserId, String sessionUserId, String role) {
        return "ADMIN".equals(role) || targetUserId.equals(sessionUserId);
    }

    private String generateTempPw() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String allChars = upper + lower + digits;

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));

        for (int i = 3; i < 10; i++) {
            sb.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        char[] arr = sb.toString().toCharArray();
        for (int i = 0; i < arr.length; i++) {
            int j = random.nextInt(arr.length);
            char temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return new String(arr);
    }

    public boolean isAdminThere() {
        return adminRepository.count() > 0;
    }

    public void createDefaultAdmin() {
        Admin admin = new Admin();
        admin.setAdminId("admin");
        admin.setPassword(encode("11111"));
        admin.setNickname("관리자");
        admin.setEmail("admin@ggggg.com");
        adminRepository.save(admin);
    }

    public boolean isIdAvail(String userId) {
        return !(userRepository.existsByUserId(userId) || adminRepository.existsByAdminId(userId));
    }

    public boolean isEmailAvail(String email) {
        return !(userRepository.existsByEmail(email) || adminRepository.existsByEmail(email));
    }
    private Users toEntity(UsersDTO dto) {
        if (dto == null) return null;

        Users users = new Users();
        users.setUserNo(dto.getUserNo());
        users.setUserId(dto.getUserId());
        users.setName(dto.getName());
        users.setEmail(dto.getEmail());
        users.setPassword(dto.getPassword());
        users.setNickname(dto.getNickname());
        users.setProfileImage(dto.getProfileImage());
        users.setJoinDate(dto.getJoinDate());
        users.setBirth(dto.getBirth());
        users.setStatus(dto.getStatus());
        users.setPoint(dto.getPoint());
        return users;
    }

    private UsersDTO toDTO(Users users) {
        if (users == null) return null;

        UsersDTO dto = new UsersDTO();
        dto.setUserNo(users.getUserNo());
        dto.setUserId(users.getUserId());
        dto.setName(users.getName());
        dto.setEmail(users.getEmail());
        dto.setPassword(users.getPassword());
        dto.setNickname(users.getNickname());
        dto.setProfileImage(users.getProfileImage());
        dto.setJoinDate(users.getJoinDate());
        dto.setBirth(users.getBirth());
        dto.setStatus(users.getStatus());
        dto.setPoint(users.getPoint());
        return dto;
    }
}
