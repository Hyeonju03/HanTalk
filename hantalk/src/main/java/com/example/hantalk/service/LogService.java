package com.example.hantalk.service;

import com.example.hantalk.dto.LogDataDTO;
import com.example.hantalk.entity.LogData;
import com.example.hantalk.repository.LogDataRepository;
import com.example.hantalk.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogDataRepository logRepository;
    private final UsersRepository usersRepository;

    public void saveLog(int userNo, String uri, String method, String device, long responseTime, int statusCode) {
        LogDataDTO dto = new LogDataDTO();
        dto.setUserNo(userNo);
        dto.setUri(uri);
        dto.setMethod(method);
        dto.setDevice(device);
        dto.setResponseTime(responseTime);
        dto.setStatusCode(statusCode);
        saveLog(dto);
    }

    public void saveLog(LogDataDTO logDto) {
        LocalDateTime today = LocalDateTime.now();
        LogData log = toEntity(logDto);
        log.setCreateDate(today);
        logRepository.save(log);
    }

    // ✅ 전체 로그 조회 (Entity → DTO 변환 후 반환)
    public List<LogDataDTO> getLog() {
        return logRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ✅ 특정 사용자 로그 조회
    public List<LogDataDTO> getLogToUser(int userNo) {
        Optional<List<LogData>> logList = logRepository.findByUserNo(userNo);
        if(logList.isPresent()){
            return logList.get()
                    .stream()
                    .map(this::toDto)
                    .toList();
        }
        return null;
    }

    // ✅ 특정 날짜 로그 조회
    public List<LogDataDTO> getLogToDate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        Optional<List<LogData>> logList = logRepository.findByCreateDateBetween(start, end);
        if(logList.isPresent()){
            return logList.get()
                    .stream()
                    .map(this::toDto)
                    .toList();
        }
        return null;
    }

    // ✅ 특정 사용자 + 날짜 로그 조회
    public List<LogDataDTO> getLogToUserAndDate(int userNo, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        Optional<List<LogData>> logList = logRepository.findByUserNoAndCreateDateBetween(userNo, start, end);
        if(logList.isPresent()){
            return logList.get()
                    .stream()
                    .map(this::toDto)
                    .toList();
        }
        return null;
    }

    private LogDataDTO toDto(LogData entity) {
        LogDataDTO dto = new LogDataDTO();
        dto.setUserNo(entity.getUserNo());
        dto.setUri(entity.getUri());
        dto.setMethod(entity.getMethod());
        dto.setDevice(entity.getDevice()); // ✅ ip → device
        dto.setResponseTime(entity.getResponseTime());
        dto.setStatusCode(entity.getStatusCode());
        dto.setCreateDate(entity.getCreateDate());
        return dto;
    }

    private LogData toEntity(LogDataDTO dto) {
        LogData entity = new LogData();
        entity.setUserNo(dto.getUserNo());
        entity.setUri(dto.getUri());
        entity.setMethod(dto.getMethod());
        entity.setDevice(dto.getDevice());
        entity.setResponseTime(dto.getResponseTime());
        entity.setStatusCode(dto.getStatusCode());
        return entity;
    }
}