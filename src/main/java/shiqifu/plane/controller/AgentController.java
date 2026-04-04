package shiqifu.plane.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import shiqifu.plane.entity.entity.AgentResult;
import shiqifu.plane.service.impl.AgentServiceImpl;

@Slf4j
@RestController
@RequestMapping("/agent")
public class AgentController {
    @Autowired
    private AgentServiceImpl agentService;
    @PostMapping("/fast_api")
    public AgentResult fast_api(@RequestParam("file")MultipartFile file) {
        log.info("fast_api开始了");
        try {
            AgentResult agentResult =agentService.fast_api(file);
            if(agentResult!=null){
                return agentResult;
            }else{
                throw new RuntimeException("结果有问题");
            }
        }
        catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException("运行失败");
        }
    }
}
