package shiqifu.plane.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shiqifu.plane.Entity.Result;

@RestController
@RequestMapping("/evaluate")
public class EvaluateController {
    @PostMapping("/one")
    public Result one(String model,String attack,String dataset,String eps){
        return null;
    }
    @PostMapping("/more")
    public Result more(){
        return null;
    }
    @PostMapping("/own")
    public Result own(){
        return null;
    }

}
