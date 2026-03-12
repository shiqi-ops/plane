package shiqifu.plane.handle;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LogoutHandle extends SimpleUrlLogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication!=null&&authentication.getPrincipal()!=null){
            String username = authentication.getName();
            System.out.println("✅ 用户 [" + username + "] 已安全退出！");
        }
        this.setDefaultTargetUrl("/login");

        super.onLogoutSuccess(request, response, authentication);
    }
}
