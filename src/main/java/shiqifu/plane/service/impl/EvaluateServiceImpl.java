package shiqifu.plane.service.impl;

import com.lowagie.text.*;
import com.google.gson.Gson;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shiqifu.plane.Entity.Result;
import shiqifu.plane.Entity.ResultMore;
import shiqifu.plane.util.PdfUtil;

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
    private final Gson gson = new Gson();
    private final String pythonExePath = "D://download//drone-robustness-platform//Scripts//python.exe";
    private final String scriptPath = "D://python//drone-robustness-platform//attack_engine//evaluate_one.py";
    private final String workingDirectory = "D:/python//drone-robustness-platform//attack_engine";
    private final String scriptPathMore="D://python//drone-robustness-platform//attack_engine//evaluate_more.py";
    private final String scriptPathOwn="D://python//drone-robustness-platform//attack_engine//evaluate_own.py";
    private final String image1="D:/python/drone-robustness-platform/results/attack_bar.png";
    private final String image2="D:/python/drone-robustness-platform/results/robustness_curve.png";
    private final String image3="D:/python/drone-robustness-platform/results/attack_heatmap.png";
    private final String image4="D:/python/drone-robustness-platform/results/attack_bubble.png";
    public Result one(String model,String attack,String dataset,String eps) throws IOException, InterruptedException {
        List<String> command=new ArrayList<>();
        command.add(pythonExePath);
        command.add(scriptPath);
        command.add("--model");
        command.add(model);
        command.add("--dataset");
        command.add(dataset);
        command.add("--attack");
        command.add(attack);
        command.add("--eps");
        command.add(String.valueOf(eps));

        ProcessBuilder pb=new ProcessBuilder(command);
        pb.directory(new File(workingDirectory));
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
            byte[]file1= Files.readAllBytes(Paths.get("D:/python/drone-robustness-platform/results/compare.png"));
            String base1=Base64.getEncoder().encodeToString(file1);
            String finalBase1="data:" + "image1/png" + ";base64," + base1;
            result.setComparePath(finalBase1);
            byte[]file2=Files.readAllBytes(Paths.get("D:/python/drone-robustness-platform/results/curve.png"));
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
        PdfUtil.pdf(result);
        return result;
    }
    public ResultMore more(String model, String attack_group, String dataset, String eps) throws IOException, InterruptedException{
        List<String> command=new ArrayList<>();
        command.add(pythonExePath);
        command.add(scriptPathMore);
        command.add("--model");
        command.add(model);
        command.add("--dataset");
        command.add(dataset);
        command.add("--attack_group");
        command.add(attack_group);
        command.add("--eps");
        command.add(String.valueOf(eps));

        ProcessBuilder pb=new ProcessBuilder(command);
        pb.directory(new File(workingDirectory));
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
                byte[]file1= Files.readAllBytes(Paths.get(image1));
                String base1=Base64.getEncoder().encodeToString(file1);
                String finalBase1="data:" + "image1/png" + ";base64," + base1;
                result.setAttackBar(finalBase1);
                byte[]file2= Files.readAllBytes(Paths.get(image2));
                String base2=Base64.getEncoder().encodeToString(file2);
                String finalBase2="data:" + "image2/png" + ";base64," + base2;
                result.setRobustnessCurve(finalBase2);
                byte[]file3= Files.readAllBytes(Paths.get(image3));
                String base3=Base64.getEncoder().encodeToString(file3);
                String finalBase3="data:" + "image3/png" + ";base64," + base3;
                result.setAttackHeatmap(finalBase3);
                byte[]file4= Files.readAllBytes(Paths.get(image4));
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
        PdfUtil.pdf(result);
        return result;
    }
    public Result own(String model_path,String attack,String dataset,String eps) throws IOException, InterruptedException{
        List<String> command=new ArrayList<>();
        command.add(pythonExePath);
        command.add(scriptPathOwn);
        command.add("--model_path");
        command.add(model_path);
        command.add("--dataset");
        command.add(dataset);
        command.add("--attack");
        command.add(attack);
        command.add("--eps");
        command.add(String.valueOf(eps));

        ProcessBuilder pb=new ProcessBuilder(command);
        pb.directory(new File(workingDirectory));
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
        return result;
    }
}
