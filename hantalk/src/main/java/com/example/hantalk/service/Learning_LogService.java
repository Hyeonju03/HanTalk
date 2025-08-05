package com.example.hantalk.service;

import com.example.hantalk.entity.Learning_Log;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.Learning_LogRepository;
import com.example.hantalk.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Learning_LogService {
    private final UsersRepository userRepository;
    private final Learning_LogRepository learningLogRepository;

    public void setLearningLog(String userId) {
        Optional<Users> getUserOpt = userRepository.findByUserId(userId);
        if (getUserOpt.isEmpty()) {
            System.out.println("학습 로그 생성 실패: 유저를 찾을 수 없음 (" + userId + ")");
            return;
        }

        Users userEntity = getUserOpt.get();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Optional<Learning_Log> logOpt = learningLogRepository
                .findByUsers_UserNoAndLearningDateBetween(userEntity.getUserNo(), startOfDay, endOfDay);

        if (logOpt.isEmpty()) {
            Learning_Log newLog = new Learning_Log();
            newLog.setUsers(userEntity);
            newLog.setLearningDate(now);
            learningLogRepository.save(newLog);
            System.out.println("오늘 학습 로그 생성: " + userEntity.getUserId());
        } else {
            System.out.println("오늘 이미 학습 로그가 존재: " + userEntity.getUserId());
        }
    }


    public  void updateLearning_Log(String userId, int lessonNo){

    }
}
