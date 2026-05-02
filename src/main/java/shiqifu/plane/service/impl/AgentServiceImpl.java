package shiqifu.plane.service.impl;

import com.google.gson.Gson;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shiqifu.plane.entity.entity.AgentResult;
import shiqifu.plane.entity.vo.AgentVO;
import shiqifu.plane.util.AgentUtil;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class AgentServiceImpl {
    @Autowired
    private AgentUtil agentUtil;

    @Autowired
    private MinioClient minioClient;

    private OkHttpClient client=null;
    private Gson gson =null;
    public AgentServiceImpl(){
        client=new OkHttpClient();
        gson=new Gson();
    }
    public AgentResult fast_api(MultipartFile file) throws Exception{
        byte[] fileBytes = file.getBytes();
        String fileName = file.getOriginalFilename();


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(fileBytes, MediaType.parse("image/jpeg")))
                .build();


        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/detect")
                .post(requestBody)
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                AgentVO agentVO = gson.fromJson(responseBody, AgentVO.class);
                log.info("FastAPI 响应: " + responseBody);
                AgentResult result=agentUtil.entry(agentVO);
                Document doc = new Document();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PdfWriter.getInstance(doc, outputStream);
                doc.open();

                String fontPath = "C://Windows//Fonts//simhei.ttf";
                BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font titleFont = new Font(bf, 18, Font.BOLD);
                Font headingFont = new Font(bf, 14, Font.BOLD);
                Font normalFont = new Font(bf, 11, Font.NORMAL);
                int smallGap = 2;

                Paragraph title=new Paragraph("AI 安全检测报告",titleFont);
                title.setAlignment(Paragraph.ALIGN_CENTER);
                title.setSpacingAfter(10);
                doc.add(title);

                String id = "DRONE-AI-SEC-"+new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                Paragraph p1=new Paragraph("报告编号: "+id,normalFont);
                p1.setSpacingBefore(smallGap);
                p1.setSpacingAfter(smallGap);
                doc.add(p1);

                Paragraph p2=new Paragraph("检测对象：无人机视觉模型输入图像");
                p2.setSpacingBefore(smallGap);
                p2.setSpacingAfter(smallGap);
                doc.add(p2);
                Image image=Image.getInstance(file.getBytes());
                image.scaleToFit(340, 240);
                image.setAlignment(Image.ALIGN_CENTER);

                image.setSpacingBefore(0);
                image.setSpacingAfter(4);
                doc.add(image);

                Paragraph p3=new Paragraph("检测方法：Feature Squeezing 多尺度一致性检测\n" +
                        "风险等级："+result.getRisk_level(),normalFont);
                p3.setSpacingBefore(smallGap);
                p3.setSpacingAfter(smallGap);
                doc.add(p3);

                Paragraph section=new Paragraph("1. 检测结果摘要",headingFont);
                section.setSpacingBefore(smallGap);
                section.setSpacingAfter(smallGap);
                doc.add(section);

                Paragraph p4=new Paragraph("风险评分: "+result.getRisk_score()+" \n" +
                        "风险等级: "+result.getRisk_level()+" \n",normalFont);
                p4.setSpacingBefore(smallGap);
                p4.setSpacingAfter(smallGap);
                doc.add(p4);

                Paragraph section2=new Paragraph("2. 多维度检测分析",headingFont);
                section2.setSpacingBefore(smallGap);
                section2.setSpacingAfter(smallGap);
                doc.add(section2);

                Paragraph p5=new Paragraph("检测方法：Feature Squeezing 多尺度压缩一致性检测\n" +
                        "检测原理：通过多尺度图像压缩（如降低色彩深度、平滑滤波），对比原始图像与压缩图像\n" +
                        "检查总结："+result.getAgent1_analysis(),normalFont);
                p5.setSpacingBefore(smallGap);
                p5.setSpacingAfter(smallGap);
                doc.add(p5);

                Paragraph p6=new Paragraph("2.2 场景风险分析\n" +
                        "攻击来源总结推断:\n"+result.getAgent2_analysis(),normalFont);
                p6.setSpacingBefore(smallGap);
                p6.setSpacingAfter(smallGap);
                doc.add(p6);

                Paragraph section3=new Paragraph("3. 最终风险报告",headingFont);
                section3.setSpacingBefore(smallGap);
                section3.setSpacingAfter(smallGap);
                doc.add(section3);

                Paragraph p7=new Paragraph(result.getFinal_report()+"\n" +
                        "此类攻击在无人机应用场景中，常见形式为恶意贴纸攻击或红外干扰，会直接导致模型\n" +
                        "决策不可信。为保障飞行安全，需立即触发人工接管，并对飞行环境进行复核，排除物理干\n" +
                        "扰源",normalFont);
                p7.setSpacingBefore(smallGap);
                p7.setSpacingAfter(smallGap);
                doc.add(p7);

                doc.close();

                byte[]bytes=outputStream.toByteArray();
                String random= UUID.randomUUID().toString();
                String file_name = "path/"+random + ".pdf";
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket("shiqifu")
                                .object(file_name)
                                .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                                .contentType("application/pdf")
                                .build()
                );
                result.setDownload_url(file_name);
                return result;
            } else {
                log.error("FastAPI 请求失败，状态码: " + response.code());
                return null;
            }
        } catch (IOException e) {
            log.error("请求 FastAPI 时发生异常: " + e.getMessage());
            throw e;
        }
    }
}
