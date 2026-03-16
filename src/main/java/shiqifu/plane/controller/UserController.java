package shiqifu.plane.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import shiqifu.plane.Entity.User;
import shiqifu.plane.Entity.UserLoginDTO;
import shiqifu.plane.service.impl.UserServiceImpl;
import shiqifu.plane.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/login")
    public Map<String,String> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("登录");
        String username=userLoginDTO.getUsername();
        String password=userLoginDTO.getPassword();
        UserDetails userDetails;
        try{
            userDetails=userService.loadUserByUsername(username);
        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException(e.getMessage());
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new RuntimeException("用户名或密码错误");
        }
        Map<String,Object> claims=new HashMap<>();
        claims.put("role","admin");
        String token=jwtUtil.generateToken(username,claims);
        Map<String,String> map=new HashMap<>();
        map.put("token",token);
        map.put("username",username);
        return map;
    }
    @PostMapping("/register")
    public void register(@RequestBody User user){
        userService.save(user);
    }
}
