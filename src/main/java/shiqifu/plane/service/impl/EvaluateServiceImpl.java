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
import org.springframework.stereotype.Service;
import shiqifu.plane.entity.entity.AttackResult;
import shiqifu.plane.entity.entity.Result;
import shiqifu.plane.entity.entity.ResultMore;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EvaluateServiceImpl {
    private final MinioClient minioClient;
    public  EvaluateServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }
    private final Gson gson = new Gson();
    private final String pythonExePath = "\\src\\main\\resources\\script\\python_env\\scripts\\python.exe";
    private final String script_Path = "\\src\\main\\resources\\script\\attack_engine\\evaluate_one.py";
    private final String workingDirectory = "\\src\\main\\resources\\script\\attack_engine";
    private final String script_PathMore = "\\src\\main\\resources\\script\\attack_engine\\evaluate_more.py";
    private final String script_PathOwn = "\\src\\main\\resources\\script\\attack_engine\\evaluate_own.py";
    private final String image_1 = "\\src\\main\\resources\\script\\results\\attack_bar.png";
    private final String image_2 = "\\src\\main\\resources\\script\\results\\robustness_curve.png";
    private final String image_3 = "\\src\\main\\resources\\script\\results\\attack_heatmap.png";
    private final String image_4 = "\\src\\main\\resources\\script\\results\\attack_bubble.png";
    private final String image_5 = "\\src\\main\\resources\\script\\results\\compare.png";
    private final String image_6 = "\\src\\main\\resources\\script\\results\\curve.png";
    public Result one(String model,String attack,String dataset,String eps) throws Exception{
        String root=System.getProperty("user.dir")+"\\plane";
        List<String> command=new ArrayList<>();
        command.add(root+pythonExePath);
        command.add(root+script_Path);
        command.add("--model");
        command.add(model);
        command.add("--dataset");
        command.add(dataset);
        command.add("--attack");
        command.add(attack);
        command.add("--eps");
        command.add(String.valueOf(eps));

        ProcessBuilder pb=new ProcessBuilder(command);
        pb.directory(new File(root+workingDirectory));
        pb.redirectErrorStream(true);
        Process p=pb.start();
        Result result=new Result();
        boolean jsonFind=false;
        try (BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line=br.readLine())!=null) {
                System.out.println(line);
                String trimLine=line.trim();
                if (trimLine.startsWith("Model:")) {
                    result.setModel(trimLine.substring(trimLine.indexOf(":") + 2));
                }
                else if (trimLine.startsWith("Dataset:")) {
                    result.setDataset(trimLine.substring(trimLine.indexOf(":") + 2));
                }
                else if (trimLine.startsWith("Attack:")) {
                    result.setAttack(trimLine.substring(trimLine.indexOf(":") + 2));
                }
                else if (trimLine.startsWith("Eps:")) {
                    String val = trimLine.substring(trimLine.indexOf(":") + 1).trim();
                    result.setEps(Double.parseDouble(val));
                }
                else if (trimLine.startsWith("clean accuracy:")) {
                    String val = trimLine.substring(trimLine.indexOf(":") + 1).trim();
                    result.setCleanAccuracy(Double.parseDouble(val));
                }
                else if (trimLine.startsWith("adv accuracy:")) {
                    String val = trimLine.substring(trimLine.indexOf(":") + 1).trim();
                    result.setAdvAccuracy(Double.parseDouble(val));
                }
                else if (trimLine.startsWith("accuracy drop:")) {
                    String val = trimLine.substring(trimLine.indexOf(":") + 1).trim();
                    result.setAccuracyDrop(Double.parseDouble(val));
                }
                else if (trimLine.startsWith("robustness level:")) {
                    result.setRobustLevel(trimLine.substring(trimLine.indexOf(":") + 2));
                }
                else if (trimLine.startsWith("robust score:")) {
                    String val = trimLine.substring(trimLine.indexOf(":") + 1).trim();
                    result.setRobustScore(Double.parseDouble(val));
                }
                jsonFind=true;
            }
            byte[]file1= Files.readAllBytes(Paths.get(root+image_5));
            String base1=Base64.getEncoder().encodeToString(file1);
            String finalBase1="data:" + "image1/png" + ";base64," + base1;
            result.setComparePath(finalBase1);
            byte[]file2=Files.readAllBytes(Paths.get(root+image_6));
            String base2=Base64.getEncoder().encodeToString(file2);
            String finalBase2="data:" + "image2/png" + ";base64," + base2;
            result.setCurvePath(finalBase2);
            if(jsonFind) {
                result=gson.fromJson(gson.toJson(result), Result.class);
            }
        }
        int exitCode=p.waitFor();
        if(exitCode!=0){
            throw new RuntimeException("Python 脚本执行失败，退出码: " + exitCode);
        }
        if(result==null){
            throw new RuntimeException("Python 脚本执行成功但未返回有效的 JSON 结果");
        }


        Document doc = new Document();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, outputStream);
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

        Image image1 = Image.getInstance(root+image_5);

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

        Image image2 = Image.getInstance(root+image_6);

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

        byte[]bytes=outputStream.toByteArray();
        String random= UUID.randomUUID().toString();
        String fileName = "path/"+random + ".pdf";
        minioClient.putObject(
          PutObjectArgs.builder()
                  .bucket("shiqifu")
                  .object(fileName)
                  .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                  .contentType("application/pdf")
                  .build()
        );
        result.setDownloadUrl(fileName);
        return result;
    }
    public ResultMore more(String model, String attack_group, String dataset, String eps) throws Exception{
        String root=System.getProperty("user.dir")+"\\plane";
        List<String> command=new ArrayList<>();
        command.add(root+pythonExePath);
        command.add(root+script_PathMore);
        command.add("--model");
        command.add(model);
        command.add("--dataset");
        command.add(dataset);
        command.add("--attack_group");
        command.add(attack_group);
        command.add("--eps");
        command.add(String.valueOf(eps));

        ProcessBuilder pb=new ProcessBuilder(command);
        pb.directory(new File(root+workingDirectory));
        pb.redirectErrorStream(true);
        Process p=pb.start();
        ResultMore result=null;
        boolean jsonFind=false;
        boolean jsonIn=false;
        StringBuilder jsonBuffer = new StringBuilder();
        Integer count=0;
        try (BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line=br.readLine())!=null) {
                System.out.println(line);
                if(line.startsWith("{")) {
                    if(count==0){
                        jsonIn=true;
                        count=1;
                        jsonBuffer.append(line);
                        jsonFind=true;
                    }else{
                        count++;
                        jsonBuffer.append(line);
                    }
                }else if(jsonIn==true){
                    jsonBuffer.append(line);
                }else if(line.endsWith("}")) {
                    if(count==1){
                        jsonIn=false;
                        count=0;
                        jsonBuffer.append(line);
                    }else{
                        count--;
                        jsonBuffer.append(line);
                    }
                }
            }
            if(jsonFind) {
                result = gson.fromJson(jsonBuffer.toString(), ResultMore.class);
                byte[]file1= Files.readAllBytes(Paths.get(root+image_1));
                String base1=Base64.getEncoder().encodeToString(file1);
                String finalBase1="data:" + "image1/png" + ";base64," + base1;
                result.setAttackBar(finalBase1);
                byte[]file2= Files.readAllBytes(Paths.get(root+image_2));
                String base2=Base64.getEncoder().encodeToString(file2);
                String finalBase2="data:" + "image2/png" + ";base64," + base2;
                result.setRobustnessCurve(finalBase2);
                byte[]file3= Files.readAllBytes(Paths.get(root+image_3));
                String base3=Base64.getEncoder().encodeToString(file3);
                String finalBase3="data:" + "image3/png" + ";base64," + base3;
                result.setAttackHeatmap(finalBase3);
                byte[]file4= Files.readAllBytes(Paths.get(root+image_4));
                String base4=Base64.getEncoder().encodeToString(file4);
                String finalBase4="data:" + "image4/png" + ";base64," + base4;
                result.setAttackBubble(finalBase4);
            }
        }
        int exitCode=p.waitFor();
        if(exitCode!=0){
            throw new RuntimeException("Python 脚本执行失败，退出码: " + exitCode);
        }
        if(!jsonFind){
            throw new RuntimeException("Python 脚本执行成功但没有找到文件位置");
        }
        if(result==null){
            throw new RuntimeException("Python 脚本执行成功但未返回有效的 JSON 结果");
        }

        Document doc = new Document();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc,bos);
        doc.open();

        String fontPath = "C://Windows//Fonts//simhei.ttf";
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font headingFont = new Font(bf, 14, Font.BOLD);
        Font normalFont = new Font(bf, 11, Font.NORMAL);
        int smallGap = 15;
        List<AttackResult>list = result.getAttackResults();
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
        Paragraph content2=new Paragraph("模型名称："+result.getModel()+"\n" +
                "测试数据集："+result.getDataset()+"\n" +
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
                "清洁准确率（cleanaccuracy）："+result.getCleanAccuracy()+"\n" +
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

        Image image1=Image.getInstance(image_1);
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

        Image image2=Image.getInstance(image_2);
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

        Image image3=Image.getInstance(image_3);

        image3.scaleToFit(340, 240);
        image3.setAlignment(Image.ALIGN_CENTER);

        image3.setSpacingBefore(0);
        image3.setSpacingAfter(4);
        doc.add(image3);

        doc.close();
        String random=UUID.randomUUID().toString();
        String fileName = random + ".pdf";
        byte[]bytes=bos.toByteArray();
        minioClient.putObject(
          PutObjectArgs.builder()
                  .bucket("shiqifu")
                  .object(fileName)
                  .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                  .contentType("application/pdf")
                  .build()
        );
        return result;
    }
    public Result own(String model_path,String attack,String dataset,String eps) throws Exception{
        String root=System.getProperty("user.dir")+"\\plane";
        List<String> command=new ArrayList<>();
        command.add(root+pythonExePath);
        command.add(root+script_PathOwn);
        command.add("--model_path");
        command.add(model_path);
        command.add("--dataset");
        command.add(dataset);
        command.add("--attack");
        command.add(attack);
        command.add("--eps");
        command.add(String.valueOf(eps));

        ProcessBuilder pb=new ProcessBuilder(command);
        pb.directory(new File(root+workingDirectory));
        pb.redirectErrorStream(true);
        Process p=pb.start();
        Result result=null;
        boolean jsonFind=false;
        boolean jsonIn=false;
        StringBuilder jsonBuffer = new StringBuilder();
        Integer count=0;
        try (BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line=br.readLine())!=null) {
                System.out.println(line);
                if(line.startsWith("{")) {
                    if(count==0){
                        jsonIn=true;
                        count=1;
                        jsonBuffer.append(line);
                        jsonFind=true;
                    }else{
                        count++;
                        jsonBuffer.append(line);
                    }
                }else if(jsonIn==true){
                    jsonBuffer.append(line);
                }else if(line.endsWith("}")) {
                    if(count==1){
                        jsonIn=false;
                        count=0;
                        jsonBuffer.append(line);
                    }else{
                        count--;
                        jsonBuffer.append(line);
                    }
                }
            }
            if(jsonFind) {
                result = gson.fromJson(jsonBuffer.toString(), Result.class);
            }
        }
        int exitCode=p.waitFor();
        if(exitCode!=0){
            throw new RuntimeException("Python 脚本执行失败，退出码: " + exitCode);
        }
        if(!jsonFind){
            throw new RuntimeException("Python 脚本执行成功但没有找到文件位置");
        }
        if(result==null){
            throw new RuntimeException("Python 脚本执行成功但未返回有效的 JSON 结果");
        }
        Document doc = new Document();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, outputStream);
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

        Image image1 = Image.getInstance(root+image_5);

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

        Image image2 = Image.getInstance(root+image_6);

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

        byte[]bytes=outputStream.toByteArray();
        String random= UUID.randomUUID().toString();
        String fileName = "path/"+random + ".pdf";
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("shiqifu")
                        .object(fileName)
                        .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                        .contentType("application/pdf")
                        .build()
        );
        result.setDownloadUrl(fileName);
        return result;
    }
}
