package shiqifu.plane.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shiqifu.plane.Entity.*;
import shiqifu.plane.annotation.NeedDownloadModel;
import shiqifu.plane.service.impl.EvaluateServiceImpl;

@RestController
@Slf4j
@RequestMapping("/evaluate")
public class EvaluateController {
    @Autowired
    private EvaluateServiceImpl evaluateService;
    @PostMapping("/one")
    public Result one(@RequestBody EvaluateDTO evaluateDTO){
        try {
            log.info("开始测试");
            String model = evaluateDTO.getModel();
            String attack=evaluateDTO.getAttack();
            String dataset = evaluateDTO.getDataset();
            String eps = evaluateDTO.getEps();
            Result result=evaluateService.one(model,attack,dataset,eps);
            log.info(result.toString());
            System.out.println("结果"+result.toString());
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("评估失败: " + e.getMessage());
        }
    }
    @PostMapping("/more")
    public ResultMore more(@RequestBody EvaluateMoreDTO evaluateMoreDTO){
        try {
            log.info("开始测试");
            String model = evaluateMoreDTO.getModel();
            String attack_group=evaluateMoreDTO.getAttack_group();
            String dataset = evaluateMoreDTO.getDataset();
            String eps = evaluateMoreDTO.getEps();
            ResultMore result=evaluateService.more(model,attack_group,dataset,eps);
            System.out.println("结果"+result.toString());
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("评估失败: " + e.getMessage());
        }
    }
    @NeedDownloadModel
    @PostMapping("/own")
    public Result own(EvaluateOwnDTO evaluateOwnDTO){
        try {
            log.info("开始测试");
            String model_pth = evaluateOwnDTO.getModel_pth();
            String attack=evaluateOwnDTO.getAttack();
            String dataset = evaluateOwnDTO.getDataset();
            String eps = evaluateOwnDTO.getEps();
            Result result=evaluateService.own(model_pth,attack,dataset,eps);
            System.out.println("结果"+result.toString());
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("评估失败: " + e.getMessage());
        }
    }

}
