package shiqifu.plane.service.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shiqifu.plane.Entity.Result;
import shiqifu.plane.Entity.ResultMore;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EvaluateServiceImpl {
    private final Gson gson = new Gson();
    private final String pythonExePath = "D://download//drone-robustness-platform//Scripts//python.exe";
    private final String scriptPath = "D://python//drone-robustness-platform//attack_engine//evaluate_one.py";
    private final String workingDirectory = "D:/python//drone-robustness-platform//attack_engine";
    private final String scriptPathMore="D://python//drone-robustness-platform//attack_engine//evaluate_more.py";
    private final String scriptPathOwn="D://python//drone-robustness-platform//attack_engine//evaluate_own.py";
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
        Result result=null;
        boolean jsonFind=false;
        try (BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            List<String> lines=new ArrayList<>();
            while ((line=br.readLine())!=null) {
                System.out.println(line);
                jsonFind=true;
                lines.add(line);
            }
            if(jsonFind){
                for(int i=0;i<lines.size();i++){
                    String s=lines.get(i);
                    String[]part=s.split(":");
                }
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
