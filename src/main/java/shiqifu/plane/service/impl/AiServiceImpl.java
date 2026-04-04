package shiqifu.plane.service.impl;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shiqifu.plane.entity.vo.AiReportVO;
import shiqifu.plane.service.CousultantService;

import java.util.Map;

@Service
public class AiServiceImpl {
    @Autowired
    private CousultantService cousultantService;
    public AiReportVO parse(String id,Map<String, Object> result){
        Map<String, Object> parsedData = (Map<String, Object>) result.get("data");

        String jsonData = new Gson().toJson(parsedData);
        AiReportVO aiResult = cousultantService.generateSafetyReport(id, jsonData);

        return aiResult;
    }
}
