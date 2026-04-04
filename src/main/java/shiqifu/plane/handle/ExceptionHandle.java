package shiqifu.plane.handle;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shiqifu.plane.exception.PasswordErrorException;
import shiqifu.plane.exception.UserNotFoundException;
import shiqifu.plane.exception.VerificationCodeErrorException;
import shiqifu.plane.exception.VerificationCodeNullException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionHandle {
    @ExceptionHandler
    public void handleException(VerificationCodeNullException exception, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        Map<String,Object> map = new HashMap<>();
        map.put("code",409);
        map.put("message","验证码为空，请重新输入验证码");
        log.info(exception.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(map));
    }
    @ExceptionHandler
    public void handleException(VerificationCodeErrorException exception, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        Map<String,Object> map = new HashMap<>();
        map.put("code",409);
        map.put("message","验证码错误,重新输入");
        log.info(exception.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(map));
    }
    @ExceptionHandler
    public void handleException(PasswordErrorException exception, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        Map<String,Object> map = new HashMap<>();
        map.put("code",400);
        map.put("message","密码错误，重新输入");
        log.info(exception.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(map));
    }
    @ExceptionHandler
    public void handleException(UserNotFoundException exception, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        Map<String,Object> map = new HashMap<>();
        map.put("code",400);
        map.put("message","没有找到用户,请重新输入");
        log.info(exception.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(map));
    }
}
