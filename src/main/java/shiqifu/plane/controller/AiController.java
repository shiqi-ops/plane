package shiqifu.plane.controller;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import shiqifu.plane.service.CousultantService;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Autowired
    private CousultantService cousultantService;

    @GetMapping("/chat")
    public String chat(String id, String msg) {
        return cousultantService.chat(id,msg);
    }

    @GetMapping("/chat_stream")
    public Flux<String> chat_stream(String id, String msg) {
        return cousultantService.chat_stream(id,msg);
    }

}
