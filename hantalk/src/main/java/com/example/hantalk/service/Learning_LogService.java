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

    private final UsersRepository usersRepository;
    private final Learning_LogRepository learningLogRepository;

    public void setLearningLog(String userId) {
        Optional<Users> getUserOpt = usersRepository.findByUserId(userId);
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

    public void updateLearning_Log(String userId, int lessonNo) {
        Optional<Users> ou = usersRepository.findByUserId(userId);
        if(ou.isEmpty()){
            System.out.println("학습 로그 업데이트 실패: 유저를 찾을 수 없음 (" + userId + ")");
            return;
        }

        if(ou.isPresent()) {
            Users userEntity = ou.get();
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);

            Optional<Learning_Log> logOpt = learningLogRepository
                    .findByUsers_UserNoAndLearningDateBetween(userEntity.getUserNo(), startOfDay, endOfDay);

            Learning_Log log = logOpt.get();

            log.setUsers(ou.get());

            if (lessonNo != 0) {
                switch (lessonNo) {
                    case 1 -> log.setLearning1Count(log.getLearning1Count() + 1);
                    case 2 -> log.setLearning2Count(log.getLearning2Count() + 1);
                    case 3 -> log.setLearning3Count(log.getLearning3Count() + 1);
                    case 4 -> log.setLearning4Count(log.getLearning4Count() + 1);
                }
            }

            learningLogRepository.save(log);
            System.out.println("학습 로그 업데이트 성공: " + userEntity.getUserId() + ", 레슨 " + lessonNo);
        }

    }
}
