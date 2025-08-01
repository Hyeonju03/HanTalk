package com.example.hantalk.service;

import com.example.hantalk.dto.Learning_LogDTO;
import com.example.hantalk.entity.Learning_Log;
import com.example.hantalk.repository.Learning_LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class Learning_LogService {
    private final Learning_LogRepository repository;

    private Learning_LogDTO entityToDto(Learning_Log learningLog){
        Learning_LogDTO dto = new Learning_LogDTO();

        dto.setLearningId(learningLog.getLearningId());
        dto.setLearningDate(learningLog.getLearningDate());
        dto.setLearning1Count(learningLog.getLearning1Count());
        dto.setLearning2Count(learningLog.getLearning2Count());
        dto.setLearning3Count(learningLog.getLearning3Count());
        dto.setLearning4Count(learningLog.getLearning4Count());
        dto.setUsers(learningLog.getUsers());

        return dto;
    }

    private Learning_Log dtoToEntity(Learning_LogDTO dto){
        Learning_Log learningLog = new Learning_Log();

        learningLog.setLearningId(dto.getLearningId());
        learningLog.setLearningDate(dto.getLearningDate());
        learningLog.setLearning1Count(dto.getLearning1Count());
        learningLog.setLearning2Count(dto.getLearning2Count());
        learningLog.setLearning3Count(dto.getLearning3Count());
        learningLog.setLearning4Count(dto.getLearning4Count());
        learningLog.setUsers(dto.getUsers());

        return learningLog;
    }

    public void learning4Plus(){
        //userNo는 세션에서 가져와야함. session에 어떻게 저장이 되어있는지 확인 필요.
        //merge후 작성 진행해야할거같음.
        //repository.findByUserUserNo(userNo);
    }
}
