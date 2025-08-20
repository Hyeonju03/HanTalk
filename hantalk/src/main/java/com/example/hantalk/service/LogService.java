package com.example.hantalk.service;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.dto.LogDataDTO;
import com.example.hantalk.dto.UsersDTO;
import com.example.hantalk.entity.LogData;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.LogDataRepository;
import com.example.hantalk.repository.UsersRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogDataRepository logRepository;
    private final UsersRepository usersRepository;

    public void saveLog(int userNo, String uri, String method, String device, long responseTime, int statusCode) {
        LogDataDTO dto = new LogDataDTO();
        dto.setUri(uri);
        dto.setMethod(method);
        dto.setDevice(device);
        dto.setResponseTime(responseTime);
        dto.setStatusCode(statusCode);
        Optional<Users> userOpt = usersRepository.findByUserNo(userNo);
        if (!userOpt.isPresent() || userNo == 0) {
            UsersDTO guest = new UsersDTO();
            guest.setUserId("GUEST");
            guest.setName("게스트");
            dto.setUser(guest);
        } else {
            Users userEntity = userOpt.get();
            UsersDTO safeDto = new UsersDTO();
            safeDto.setUserId(userEntity.getUserId());
            safeDto.setUserNo(userEntity.getUserNo());
            safeDto.setName(userEntity.getName());
            safeDto.setNickname(userEntity.getNickname());
            safeDto.setStatus(userEntity.getStatus());

            dto.setUser(safeDto);
        }
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
        List<LogData> logs = logRepository.findAll();
        List<LogDataDTO> dtoList = new ArrayList<>(logs.size());
        for (LogData e : logs) {
            dtoList.add(toDto(e));
        }
        return dtoList;
    }

    // ✅ 특정 사용자 로그 조회
    public List<LogDataDTO> getLogToUser(int userNo) {
        Optional<List<LogData>> logList = logRepository.findByUserNo(userNo);
        if (logList.isPresent()) {
            List<LogData> logs = logRepository.findByUserNo(userNo).get();
            List<LogDataDTO> dtoList = new ArrayList<>(logs.size());
            for (LogData e : logs) {
                dtoList.add(toDto(e));
            }
            return dtoList;
        }
        return null;
    }

    // ✅ 특정 날짜 로그 조회
    public List<LogDataDTO> getLogToDate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        Optional<List<LogData>> logList = logRepository.findByCreateDateBetween(start, end);
        if (logList.isPresent()) {
            List<LogData> logs = logRepository.findAll();
            List<LogDataDTO> dtoList = new ArrayList<>(logs.size());
            for (LogData e : logs) {
                dtoList.add(toDto(e));
            }
            return dtoList;
        }
        return null;
    }

    // ✅ 특정 사용자 + 날짜 로그 조회
    public List<LogDataDTO> getLogToUserAndDate(int userNo, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        Optional<List<LogData>> logList = logRepository.findByUserNoAndCreateDateBetween(userNo, start, end);
        if (logList.isPresent()) {
            List<LogData> logs = logRepository.findAll();
            List<LogDataDTO> dtoList = new ArrayList<>(logs.size());
            for (LogData e : logs) {
                dtoList.add(toDto(e));
            }
            return dtoList;
        }
        return null;
    }

    private LogDataDTO toDto(LogData entity) {
        LogDataDTO dto = new LogDataDTO();
        dto.setUri(entity.getUri());
        dto.setMethod(entity.getMethod());
        dto.setDevice(entity.getDevice());
        dto.setResponseTime(entity.getResponseTime());
        dto.setStatusCode(entity.getStatusCode());
        dto.setCreateDate(entity.getCreateDate());

        Integer userNo = entity.getUserNo(); // userNo 가져오기

        if (userNo == null || userNo == 0) {
            // 게스트
            UsersDTO guest = new UsersDTO();
            guest.setUserId("GUEST");
            guest.setName("게스트");
            dto.setUser(guest);

        } else if (userNo == 1) {
            // 관리자
            UsersDTO admin = new UsersDTO();
            admin.setUserId("ADMIN");
            admin.setName("관리자");
            dto.setUser(admin);

        } else {
            // 일반 사용자
            Optional<Users> userOpt = usersRepository.findByUserNo(userNo);
            if (userOpt.isPresent()) {
                Users userEntity = userOpt.get();
                UsersDTO safeDto = new UsersDTO();
                safeDto.setUserId(userEntity.getUserId());
                safeDto.setUserNo(userEntity.getUserNo());
                safeDto.setName(userEntity.getName());
                safeDto.setNickname(userEntity.getNickname());
                safeDto.setStatus(userEntity.getStatus());
                dto.setUser(safeDto);
            } else {
                // 혹시 DB에 없는 userNo일 경우 fallback → 게스트 처리
                UsersDTO guest = new UsersDTO();
                guest.setUserId("GUEST");
                guest.setName("게스트");
                dto.setUser(guest);
            }
        }
        return dto;
    }

    private LogData toEntity(LogDataDTO dto) {
        LogData entity = new LogData();
        entity.setUserNo(dto.getUser().getUserNo());
        entity.setUri(dto.getUri());
        entity.setMethod(dto.getMethod());
        entity.setDevice(dto.getDevice());
        entity.setResponseTime(dto.getResponseTime());
        entity.setStatusCode(dto.getStatusCode());
        return entity;
    }
}