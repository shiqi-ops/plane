package shiqifu.plane.util;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

@Component
public class AiUtil {
    public OpenAiChatModel build(){
         return OpenAiChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey("sk-64fcb9d69a284eea8619d4893c851902")
                .modelName("qwen-plus")
                 .logRequests(true)
                 .logResponses(true)
                .build();
    }
}
