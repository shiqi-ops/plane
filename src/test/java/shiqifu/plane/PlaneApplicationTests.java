package shiqifu.plane;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shiqifu.plane.util.AiUtil;

@SpringBootTest
class PlaneApplicationTests {
    @Autowired
    private OpenAiChatModel openAiChatModel;
    @Test
    void contextLoads() {
        String result=openAiChatModel.chat("你是谁");
        System.out.println(result);
    }

}
