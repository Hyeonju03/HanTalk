package com.example.hantalk.config.interceptor;


import com.example.hantalk.SessionUtil;
import com.example.hantalk.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private LogService logService;

    // ✅ 1. 컨트롤러 실행 이전
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("startTime", System.currentTimeMillis());
        String uri = request.getRequestURI();

        // 로그인 필요 없는 URL은 통과
        if (uri.startsWith("/user/login") || uri.startsWith("/user/signup") || uri.startsWith("/user/findIDPW")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || !SessionUtil.isLoggedIn(session)) {
            response.sendRedirect("/user/login");
            return false;
        }
        if (session == null || !SessionUtil.hasRole(session,"ADMIN")) {
            response.sendRedirect("/user/login");
            return false;
        }

        return true;
    }

    // ✅ 3. 요청 완료 후 (예외 포함)
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception{
        long startTime = (long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        HttpSession session = request.getSession(false);
        int userNo = 0;
        if (session != null) {
            Integer loginNo = SessionUtil.getLoginUserNo(session);
            if (loginNo != null) {
                userNo = loginNo;
            }
        }
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String device = getDeviceType(request);
        int status = response.getStatus();

        logService.saveLog(userNo, uri, method, device, duration, status);

        if (ex != null) {
            System.err.println("🚨 예외 발생: " + ex.getMessage());
        }
    }

    private String getDeviceType(HttpServletRequest request) throws Exception{
        String ua = request.getHeader("User-Agent");
        if (ua == null) return "UNKNOWN";
        ua = ua.toLowerCase();

        if (ua.contains("iphone") || ua.contains("ipad")) return "iOS";
        if (ua.contains("android")) return "Android";
        if (ua.contains("windows") || ua.contains("macintosh") || ua.contains("linux")) return "PC";
        return "Other";
    }
}