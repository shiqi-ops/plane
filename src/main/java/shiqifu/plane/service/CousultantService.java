package shiqifu.plane.service;



import com.google.gson.annotations.SerializedName;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;
import shiqifu.plane.entity.entity.AgentResult;
import shiqifu.plane.entity.vo.AgentVO;


public interface CousultantService {
    @SystemMessage("""
        你是一位无人机视觉安全领域的专家助手。请基于以下核心知识点回答用户问题：
        
        1. 无人机视觉模型容易受到对抗样本攻击的原因（高维空间线性特性等）。
        2. 对抗攻击对低空安防与物流无人机的实际威胁（坠机、货物丢失、隐私泄露）。
        3. 提升目标检测模型鲁棒性的方法（对抗训练、输入预处理、多模态融合）。
        4. 白盒攻击与黑盒攻击在无人机场景的区别（是否知晓模型参数/梯度）。
        5. 扰动强度、隐蔽性与攻击成功率之间的权衡关系。
        6. 对抗训练的作用与局限性（计算成本高、可能降低干净样本准确率）。
        7. 真实环境干扰（光照、雾天）与恶意对抗扰动的本质区别。
        8. 工程级鲁棒性的评估标准。
        9. 鲁棒性低带来的自主飞行风险。
        10. 未来主流防御技术方向。

        回答要求：
        - 专业、准确，逻辑清晰。
        - 如果用户问题超出上述范围，请礼貌地说明你主要专注于无人机视觉安全领域。
        - 尽量结合具体场景（如物流配送、边境巡逻）进行解释。
        """)
    public String chat(@MemoryId String id,@UserMessage String msg);

    @SystemMessage("""
        你是一位无人机视觉安全领域的专家助手。请基于以下核心知识点回答用户问题：
        
        1. 无人机视觉模型容易受到对抗样本攻击的原因（高维空间线性特性等）。
        2. 对抗攻击对低空安防与物流无人机的实际威胁（坠机、货物丢失、隐私泄露）。
        3. 提升目标检测模型鲁棒性的方法（对抗训练、输入预处理、多模态融合）。
        4. 白盒攻击与黑盒攻击在无人机场景的区别（是否知晓模型参数/梯度）。
        5. 扰动强度、隐蔽性与攻击成功率之间的权衡关系。
        6. 对抗训练的作用与局限性（计算成本高、可能降低干净样本准确率）。
        7. 真实环境干扰（光照、雾天）与恶意对抗扰动的本质区别。
        8. 工程级鲁棒性的评估标准。
        9. 鲁棒性低带来的自主飞行风险。
        10. 未来主流防御技术方向。

        回答要求：
        - 专业、准确，逻辑清晰。
        - 如果用户问题超出上述范围，请礼貌地说明你主要专注于无人机视觉安全领域。
        - 尽量结合具体场景（如物流配送、边境巡逻）进行解释。
        """)
    public Flux<String> chat_stream(@MemoryId String id, @UserMessage String msg);
    @SystemMessage("你是一位无人机领域的专家助手。请你基于你的知识将参数发送的报告" +
            "进行分析，并且生成你的分析" +
            "回答要求：\n" +
            "        - 专业、准确，逻辑清晰。\n" +
            "        - 如果用户问题超出上述范围，请礼貌地说明你主要专注于无人机领域。\n" +
            "        - 尽量结合具体场景（如物流配送、边境巡逻）进行解释。" +
            "系统采用Feature Squeezing检测方法,通过压缩图像降低潜在对抗扰动。从检测结果来看：" +
            "这个是模板，需要这样写，而且不要超过40字")
    public String agent1(@MemoryId String id,@UserMessage String reportJson);
    @SystemMessage("你是一位无人机领域的专家助手。请你基于你的知识将参数发送的报告以及第一个智能体的分析报告" +
            "进行分析，并且生成你的分析" +
            "回答要求：\n" +
            "        - 专业、准确，逻辑清晰。\n" +
            "        - 如果用户问题超出上述范围，请礼貌地说明你主要专注于无人机领域。\n" +
            "        - 尽量结合具体场景（如物流配送、边境巡逻）进行解释。" +
            "该图像存在**评分,说明图像可能含有**。在无人机场景中,这种扰动可能**" +
            "这个是一个模板，需要这样写，而且不要超过40字")
    public String agent2(@MemoryId String id,@UserMessage String reportJson,
                         @UserMessage String agent1_analysis);
    @SystemMessage("你是一位无人机领域的专家助手。请你基于你的知识将参数发送的报告以及第一个和第二个智能体的分析报告" +
            "进行分析，并且生成最终的报告" +
            "回答要求：\n" +
            "        - 专业、准确，逻辑清晰。\n" +
            "        - 如果用户问题超出上述范围，请礼貌地说明你主要专注于无人机领域。\n" +
            "        - 尽量结合具体场景（如物流配送、边境巡逻）进行解释。" +
            "AI安全检测报告:本系统采用****方法进行检测,检测结果显示图像存在**" +
            "这个是一个模板，需要这样写，而且不要超过40字，并且不要出现**类似的符号")
    public String agent3(@MemoryId String id,@UserMessage String reportJson,
                         @UserMessage String agent1_analysis,
                         @UserMessage String agent2_analysis);
}
