package shiqifu.plane.handle;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shiqifu.plane.exception.*;

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
    @ExceptionHandler
    public void handelException(UploadException exception,HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        Map<String,Object> map = new HashMap<>();
        map.put("code",400);
        map.put("message","上传失败，重新尝试");
        log.info(exception.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(map));
    }

    @ExceptionHandler
    public void handelException(DownloadException exception,HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        Map<String,Object> map = new HashMap<>();
        map.put("code",400);
        map.put("message","获取失败，请输入正确文件名");
        log.info(exception.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(map));
    }

    @ExceptionHandler
    public void handelException(DataErrorException exception,HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        Map<String,Object> map = new HashMap<>();
        map.put("code",400);
        map.put("message","数据错误，请输入正确的数据");
        log.info(exception.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(map));
    }
    @ExceptionHandler
    public void handelException(AiReportException exception,HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        Map<String,Object> map = new HashMap<>();
        map.put("code",400);
        map.put("message","报告生成失败，请重新尝试");
        log.info(exception.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(map));
    }
}
