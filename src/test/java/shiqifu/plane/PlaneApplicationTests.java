package shiqifu.plane;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;


@SpringBootTest
class PlaneApplicationTests {
    @Autowired
    private OpenAiChatModel openAiChatModel;
    @Test
    void contextLoads() {
        String result=openAiChatModel.chat("你是谁");
        System.out.println(result);
    }
    @Test
    void contextLoads2() throws Exception{
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream("D:/java/xiaowebproject/mall/mall/tmp_dir/simple"));
        doc.open();

        String fontPath = "C://Windows//Fonts//simhei.ttf";
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(bf, 12);

        Chunk chunk1 = new Chunk("你好，这是一个简单的 PDF！", font);
        Paragraph p1 = new Paragraph();
        p1.add(chunk1);

        Chunk chunk2 = new Chunk("\n生成时间：2026-03-17", font);
        Paragraph p2 = new Paragraph();
        p2.add(chunk2);

        String imagePath="C:/Users/31818/OneDrive/图片/Camera imports/2025-08-26/1737783674526.jpeg";
        Image image = Image.getInstance(imagePath);
        image.scaleToFit(300, 500);
        image.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        doc.add(image);
        doc.add(new Paragraph("\n", font));

        doc.add(p1);
        doc.add(p2);
        doc.close();

        System.out.println("✅ 生成成功！");
    }

}
