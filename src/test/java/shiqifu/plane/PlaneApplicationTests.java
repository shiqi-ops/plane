package shiqifu.plane;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;


@SpringBootTest
class PlaneApplicationTests {
    @Autowired
    private OpenAiChatModel openAiChatModel;

    private MinioClient client;
    @Test
    void contextLoads() {
        String root=System.getProperty("user.dir");
        System.out.println(root);
    }
    @Test
    void contextLoads2() throws Exception{
        String path="path/my-apps.json";

        GetObjectArgs getObjectArgs=GetObjectArgs.builder()
                .bucket("shiqifu")
                .object(path)
                .build();

        GetObjectResponse resp=client.getObject(getObjectArgs);
        resp.transferTo(System.out);
    }
    @BeforeEach
    void beforeEach() throws Exception{
        client=MinioClient.builder()
                .credentials("admin","12345678")
                .endpoint("http://127.0.0.1:9000")
                .build();
    }
    @AfterEach
    void afterEach() throws Exception{
        client.close();
    }
}
