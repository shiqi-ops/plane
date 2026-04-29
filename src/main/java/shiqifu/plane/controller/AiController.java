package shiqifu.plane.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import shiqifu.plane.entity.dto.ChatDTO;
import shiqifu.plane.entity.vo.AiReportVO;
import shiqifu.plane.exception.AiReportException;
import shiqifu.plane.service.CousultantService;
import shiqifu.plane.service.impl.AiServiceImpl;
import shiqifu.plane.util.PdfUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Autowired
    private CousultantService cousultantService;
    @Autowired
    private AiServiceImpl aiService;

    @PostMapping("/chat")
    public String chat(@RequestBody ChatDTO chatDTO) {
        String id = chatDTO.getId();
        String messages = chatDTO.getMessages();
        return cousultantService.chat(id,messages);
    }

    @PostMapping(value = "/chat_stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat_stream(@RequestBody ChatDTO chatDTO) {
        return aiService.chat_stream(chatDTO);
    }

    @PostMapping("/parse")
    public AiReportVO history(@RequestBody ChatDTO chatDTO) throws Exception {
        String id = chatDTO.getId();
        String url=chatDTO.getMessages();

        AiReportVO aiReportVO = aiService.parse(url,id);
        if(aiReportVO==null){
            throw new AiReportException();
        }
        return null;
    }

}
