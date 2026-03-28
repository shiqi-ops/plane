package shiqifu.plane.util;


import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shiqifu.plane.entity.entity.AgentResult;
import shiqifu.plane.entity.vo.AgentVO;
import shiqifu.plane.service.CousultantService;


@Component
public class AgentUtil {
    private final String defaultID="-1";
    @Autowired
    private CousultantService cousultantService;
    public AgentResult entry(AgentVO agentVO ){
        String json=new Gson().toJson(agentVO);
        String agent1_analysis=cousultantService.agent1(defaultID,json);
        String agent2_analysis= cousultantService.agent2(defaultID,json,
                agent1_analysis);
        String agent3_analysis=cousultantService.agent3(defaultID,json,
                agent1_analysis,
                agent2_analysis);
        return AgentResult.builder()
                .risk_score(agentVO.getRiskScore())
                .risk_level(agentVO.getRiskLevel())
                .agent1_analysis(agent1_analysis)
                .agent2_analysis(agent2_analysis)
                .final_report(agent3_analysis)
                .build();
    }
}
