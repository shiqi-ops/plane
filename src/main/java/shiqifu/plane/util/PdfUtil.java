package shiqifu.plane.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import shiqifu.plane.entity.entity.AttackResult;
import shiqifu.plane.entity.entity.Result;
import shiqifu.plane.entity.entity.ResultMore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfUtil {
    private final static String path = "D:/java//xiaowebproject//mall//mall//tmp_dir/";
    private final static String path1="D:/python/drone-robustness-platform/results/attack_bar.png";
    private final static String path2="D:/python/drone-robustness-platform/results/robustness_curve.png";
    private final static String path3="D:/python/drone-robustness-platform/results/attack_heatmap.png";
    private final static String path4="D:/python/drone-robustness-platform/results/attack_bubble.png";
    public static String pdf(Result result) throws IOException {

        Document doc = new Document();

        String random=UUID.randomUUID().toString();
        String fileName = path + random + ".pdf";
        PdfWriter.getInstance(doc, new FileOutputStream(fileName));
        doc.open();

        String fontPath = "C://Windows//Fonts//simhei.ttf";
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font headingFont = new Font(bf, 14, Font.BOLD);
        Font normalFont = new Font(bf, 11, Font.NORMAL);


        Paragraph title = new Paragraph("无人机视觉模型鲁棒性检测报告", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(10);
        doc.add(title);

        Paragraph section1 = new Paragraph("一、评测基本信息", headingFont);
        section1.setSpacingBefore(5);
        section1.setSpacingAfter(2);
        doc.add(section1);

        Paragraph content1 = new Paragraph("本次评测对象为无人机视觉模型，采用标准测试数据集进行鲁棒性检测。", normalFont);
        content1.setSpacingAfter(4);
        doc.add(content1);


        int smallGap = 2;
        Paragraph subHeading = new Paragraph("测试基本信息：", normalFont);
        subHeading.setSpacingBefore(smallGap);
        subHeading.setSpacingAfter(smallGap);
        doc.add(subHeading);

        Paragraph content2 = new Paragraph("模型名称：" + result.getModel(), normalFont);
        content2.setSpacingBefore(smallGap);
        content2.setSpacingAfter(smallGap);
        doc.add(content2);

        Paragraph content3 = new Paragraph("测试数据集：" + result.getDataset(), normalFont);
        content3.setSpacingBefore(smallGap);
        content3.setSpacingAfter(smallGap);
        doc.add(content3);

        Paragraph content4 = new Paragraph("攻击方法：" + result.getAttack(), normalFont);
        content4.setSpacingBefore(smallGap);
        content4.setSpacingAfter(smallGap);
        doc.add(content4);

        Paragraph content5 = new Paragraph("扰动强度（Eps）：" + result.getEps(), normalFont);
        content5.setSpacingBefore(smallGap);
        content5.setSpacingAfter(6);
        doc.add(content5);

        Paragraph section2 = new Paragraph("二、核心评测结果", headingFont);
        section2.setSpacingBefore(5);
        section2.setSpacingAfter(2);
        doc.add(section2);

        Paragraph subHeading2 = new Paragraph("1. 模型准确率变化", normalFont);
        subHeading2.setSpacingBefore(4);
        subHeading2.setSpacingAfter(2);
        doc.add(subHeading2);

        Paragraph content6 = new Paragraph("清洁准确率（cleanaccuracy）：" + result.getCleanAccuracy(), normalFont);
        content6.setSpacingBefore(smallGap);
        content6.setSpacingAfter(smallGap);
        doc.add(content6);

        Paragraph content7 = new Paragraph("对抗样本准确率（advaccuracy）：" + result.getAdvAccuracy(), normalFont);
        content7.setSpacingBefore(smallGap);
        content7.setSpacingAfter(smallGap);
        doc.add(content7);

        Paragraph content8 = new Paragraph("准确率下降幅度：" + result.getAccuracyDrop(), normalFont);
        content8.setSpacingBefore(smallGap);
        content8.setSpacingAfter(6);
        doc.add(content8);

        Paragraph subHeading3 = new Paragraph("2. 鲁棒性等级评定", normalFont);
        subHeading3.setSpacingBefore(4);
        subHeading3.setSpacingAfter(2);
        doc.add(subHeading3);

        Paragraph content9 = new Paragraph("综合检测结果，模型鲁棒性等级评定为：" + result.getRobustLevel() + "级", normalFont);
        content9.setSpacingBefore(smallGap);
        content9.setSpacingAfter(smallGap);
        doc.add(content9);


        Paragraph content10 = new Paragraph("该等级表明模型在面对强对抗扰动时，抗干扰能力一般，仍需通过优化数据增强、调整模型结构等方式进一步提升稳定性。", normalFont);
        content10.setSpacingBefore(smallGap);
        content10.setSpacingAfter(6);
        doc.add(content10);


        Paragraph section3 = new Paragraph("三、图像对抗效果检测", headingFont);

        section3.setSpacingBefore(4);
        section3.setSpacingAfter(2);
        doc.add(section3);

        Paragraph content11 = new Paragraph("原始图像（original）vs 对抗样本图像（adversarial）", normalFont);
        content11.setSpacingAfter(2);
        doc.add(content11);

        Paragraph content12 = new Paragraph("左为未受攻击的标准输入样本，右为扰动后生成的对抗样本。", normalFont);

        content12.setSpacingAfter(2);
        doc.add(content12);

        Image image1 = Image.getInstance("D:/python/drone-robustness-platform/results/compare.png");

        image1.scaleToFit(340, 240);
        image1.setAlignment(Image.ALIGN_CENTER);

        image1.setSpacingBefore(0);
        image1.setSpacingAfter(4);
        doc.add(image1);


        Paragraph section4 = new Paragraph("四、鲁棒性性能曲线分析", headingFont);
        section4.setSpacingBefore(4);
        section4.setSpacingAfter(2);
        doc.add(section4);

        Paragraph content13 = new Paragraph("横轴为扰动强度（eps），纵轴为模型准确率。", normalFont);
        content13.setSpacingBefore(2);
        content13.setSpacingAfter(2);
        doc.add(content13);

        Image image2 = Image.getInstance("D:/python/drone-robustness-platform/results/curve.png");

        image2.scaleToFit(350, 240);
        image2.setAlignment(Image.ALIGN_CENTER);
        image2.setSpacingBefore(0);
        image2.setSpacingAfter(4);
        doc.add(image2);


        Paragraph content14 = new Paragraph("曲线分析显示：随着扰动强度逐步提升，模型准确率呈现下降趋势，说明模型在强对抗环境下的鲁棒性表现不足，对抗强度越高，模型的预测偏差越明显。", normalFont);
        content14.setSpacingBefore(2);
        content14.setSpacingAfter(5);
        doc.add(content14);

        doc.close();
        return "/"+random+".pdf";
    }

    public static String pdf(ResultMore resultMore) throws IOException{
        Document doc = new Document();

        String random=UUID.randomUUID().toString();
        String fileName = path + random + ".pdf";
        PdfWriter.getInstance(doc, new FileOutputStream(fileName));
        doc.open();

        String fontPath = "C://Windows//Fonts//simhei.ttf";
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font headingFont = new Font(bf, 14, Font.BOLD);
        Font normalFont = new Font(bf, 11, Font.NORMAL);
        int smallGap = 15;
        List<AttackResult>list = resultMore.getAttackResults();
        AttackResult one=list.get(0);
        AttackResult two=list.get(1);
        AttackResult three=list.get(2);
        String attack=one.getAttack()+","+two.getAttack()+","+three.getAttack();

        Paragraph title = new Paragraph("无人机视觉模型鲁棒性检测报告",titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(10);
        doc.add(title);

        Paragraph section1=new Paragraph("一、评测基本信息",headingFont);
        section1.setSpacingBefore(5);
        section1.setSpacingAfter(2);
        doc.add(section1);

        Paragraph content1=new Paragraph("本次评测对象为无人机视觉模型，采用标准测试数据集进行鲁棒性检测。\n" +
                "测试基本信息：",normalFont);
        content1.setSpacingBefore(smallGap);
        content1.setSpacingAfter(smallGap);
        doc.add(content1);
        Paragraph content2=new Paragraph("模型名称："+resultMore.getModel()+"\n" +
                "测试数据集："+resultMore.getDataset()+"\n" +
                "攻击方法："+attack+"\n" +
                "扰动强度（Eps）：0.03",normalFont);
        content2.setSpacingBefore(smallGap);
        content2.setSpacingAfter(smallGap);
        doc.add(content2);

        Paragraph section2=new Paragraph("二、核心评测结果",headingFont);
        section2.setSpacingBefore(5);
        section2.setSpacingAfter(2);
        doc.add(section2);

        Paragraph content3=new Paragraph("1. 模型准确率变化\n" +
                "清洁准确率（cleanaccuracy）："+resultMore.getCleanAccuracy()+"\n" +
                "不同攻击方法下对抗样本准确率（advaccuracy）及下降幅度：",normalFont);
        content3.setSpacingBefore(smallGap);
        content3.setSpacingAfter(smallGap);
        doc.add(content3);

        Paragraph content4=new Paragraph(one.getAttack()+"\n"
                +"对抗样本准确率 "+one.getAdvAccuracy()+"\n"+
                "准确率下降幅度 "+one.getAccuracyDrop()+"\n"+
                "攻击成功率 "+one.getAttackSuccessRate(),normalFont);
        content4.setSpacingBefore(smallGap);
        content4.setSpacingAfter(smallGap);
        doc.add(content4);

        Paragraph content5=new Paragraph(one.getAttack()+"\n"
                +"对抗样本准确率 "+two.getAdvAccuracy()+"\n"+
                "准确率下降幅度 "+two.getAccuracyDrop()+"\n"+
                "攻击成功率 "+two.getAttackSuccessRate(),normalFont);
        content5.setSpacingBefore(smallGap);
        content5.setSpacingAfter(smallGap);
        doc.add(content5);

        Paragraph content6=new Paragraph(one.getAttack()+"\n"
                +"对抗样本准确率 "+three.getAdvAccuracy()+"\n"+
                "准确率下降幅度 "+three.getAccuracyDrop()+"\n"+
                "攻击成功率 "+three.getAttackSuccessRate(),normalFont);
        content6.setSpacingBefore(smallGap);
        content6.setSpacingAfter(smallGap);
        doc.add(content6);

        Paragraph content7=new Paragraph("2. 鲁棒性等级评定\n" +
                "综合检测结果，模型鲁棒性等级评定为：D 级\n" +
                "该等级表明模型在面对强对抗扰动时，抗干扰能力一般，仍需通过优化数据增强、调整模型结构等方\n" +
                "式进一步提升稳定性",normalFont);
        content7.setSpacingBefore(smallGap);
        content7.setSpacingAfter(smallGap);
        doc.add(content7);

        Paragraph section3=new Paragraph("三、可视化结果说明",headingFont);
        section3.setAlignment(5);
        section3.setSpacingAfter(2);
        doc.add(section3);

        Paragraph content8=new Paragraph("1. Attack Bar（攻击鲁棒性对比柱状图）\n" +
                "本图以柱状图形式直观展示了三种攻击方法下模型的对抗样本准确率，可清晰对比不同攻击对模型性\n" +
                "能的影响程度",normalFont);
        content8.setSpacingBefore(smallGap);
        content8.setSpacingAfter(smallGap);
        doc.add(content8);

        Image image1=Image.getInstance(path1);
        image1.scaleToFit(340, 240);
        image1.setAlignment(Image.ALIGN_CENTER);

        image1.setSpacingBefore(0);
        image1.setSpacingAfter(4);
        doc.add(image1);

        Paragraph content9=new Paragraph("2. Robustness Curve（鲁棒性曲线）\n" +
                "本图以折线图形式呈现了模型在不同扰动强度（Eps）下的准确率变化趋势，反映了模型随扰动增强\n" +
                "时的鲁棒性衰减规律",normalFont);
        content9.setSpacingBefore(smallGap);
        content9.setSpacingAfter(smallGap);
        doc.add(content9);

        Image image2=Image.getInstance(path2);
        image2.scaleToFit(340, 240);
        image2.setAlignment(Image.ALIGN_CENTER);

        image2.setSpacingBefore(0);
        image2.setSpacingAfter(4);
        doc.add(image2);

        Paragraph content10=new Paragraph("3. Heatmap（热力图）\n" +
                "本图以热力图形式展示了不同扰动强度与攻击方法组合下的模型准确率分布，通过颜色深浅直观体现\n" +
                "准确率差异，便于快速定位高风险扰动- 攻击组合。",normalFont);
        content10.setSpacingBefore(smallGap);
        content10.setSpacingAfter(smallGap);
        doc.add(content10);

        Image image3=Image.getInstance(path3);

        image3.scaleToFit(340, 240);
        image3.setAlignment(Image.ALIGN_CENTER);

        image3.setSpacingBefore(0);
        image3.setSpacingAfter(4);
        doc.add(image3);

        doc.close();
        return "/"+random+".pdf";
    }

    public static Map<String, Object> parseByUrl(String sourceUrlOrPath, String imageSaveDir) {
        Map<String, Object> result = new HashMap<>();
        byte[] pdfBytes = null;

        try {
            // 1. 获取文件字节流 (区分本地和网络)
            if (sourceUrlOrPath.startsWith("http")) {
                pdfBytes = downloadFileFromWeb(sourceUrlOrPath);
            } else {
                pdfBytes = readFileFromLocal(sourceUrlOrPath);
            }

            if (pdfBytes == null || pdfBytes.length == 0) {
                result.put("error", "文件内容为空");
                return result;
            }

            // 2. 创建图片保存目录
            createDirectories(imageSaveDir);

            // 3. 使用 PDFBox 3.x 正确加载文档
            // 关键修复点：使用 RandomAccessReadBuffer 包装 ByteArrayInputStream
            try (ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes);
                 RandomAccessReadBuffer randomAccessRead = new RandomAccessReadBuffer(bais);
                 PDDocument document = Loader.loadPDF(randomAccessRead)) {

                System.out.println("✅ 文档加载成功，共 " + document.getNumberOfPages() + " 页");

                // 4. 提取图片
                extractImages(document, imageSaveDir);

                // 5. 提取并解析文本
                String text = extractText(document);
                Map<String, String> parsedData = parseFlightData(text);

                result.put("text", text);
                result.put("data", parsedData);
                result.put("status", "success");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    // --- 辅助方法 ---

    /** 读取本地文件 */
    private static byte[] readFileFromLocal(String path) throws IOException {
        Path filePath = Paths.get(path);
        return Files.readAllBytes(filePath);
    }

    /** 下载网络文件 (使用 Java 11+ HttpClient) */
    private static byte[] downloadFileFromWeb(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() == 200) {
            return response.body();
        }
        return null;
    }

    /** 创建目录 */
    private static void createDirectories(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /** 提取文本 */
    private static String extractText(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }

    /** 提取图片 */
    private static void extractImages(PDDocument document, String imageSaveDir) throws IOException {
        PDFRenderer renderer = new PDFRenderer(document);
        int pageCount = document.getNumberOfPages();
        for (int i = 0; i < pageCount; i++) {
            BufferedImage image = renderer.renderImageWithDPI(i, 300, ImageType.RGB);
            String fileName = "page_" + (i + 1) + ".jpg";
            File outputFile = new File(imageSaveDir, fileName);
            ImageIO.write(image, "jpg", outputFile);
        }
    }

    /** 解析数据 */
    private static Map<String, String> parseFlightData(String text) {
        Map<String, String> data = new HashMap<>();
        if (text == null) return data;
        // 你的正则逻辑...
        Pattern datePattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
        Matcher dateMatcher = datePattern.matcher(text);
        if (dateMatcher.find()) {
            data.put("flight_date", dateMatcher.group(1));
        }
        return data;
    }
}