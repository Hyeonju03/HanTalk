package com.example.hantalk;

import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    private static final String LOGIN_USER = "userId";
    private static final String LOGIN_USER_NO = "userNo";
    private static final String LOGIN_ROLE = "role";

    // ✅ 로그인 여부 확인
    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(LOGIN_USER) != null;
    }

    // ✅ 로그인 사용자 NO 가져오기
    public static Integer getLoginUserNo(HttpSession session) {
        Object userNo = session.getAttribute(LOGIN_USER_NO);
        if (userNo instanceof Integer) {
            return (Integer) userNo;
        } else if (userNo != null) {
            try {
                return Integer.parseInt(userNo.toString());
            } catch (NumberFormatException e) {
                return null; // 변환 실패 시 null 반환
            }
        }
        return null;
    }
    // ✅ 로그인 사용자 ID 가져오기
    public static String getLoginUserId(HttpSession session) {
        Object userId = session.getAttribute(LOGIN_USER);
        return (userId != null) ? userId.toString() : null;
    }

    // ✅ 로그인 사용자의 역할(Role) 가져오기
    public static String getRole(HttpSession session) {
        Object role = session.getAttribute(LOGIN_ROLE);
        return (role != null) ? role.toString() : null;
    }

    // ✅ 특정 역할(Role) 보유 여부 확인
    public static boolean hasRole(HttpSession session, String requiredRole) {
        Object role = session.getAttribute(LOGIN_ROLE);
        return (role != null) && role.equals(requiredRole);
    }
}
