package shiqifu.plane.service;



import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;


@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
chatModel = "openAiChatModel",
chatMemoryProvider = "chatMemoryProvider")
public interface CousultantService {
    public String chat(@MemoryId String id,@UserMessage String msg);
}
