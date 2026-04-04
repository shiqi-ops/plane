package shiqifu.plane.service.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shiqifu.plane.entity.entity.AgentResult;
import shiqifu.plane.entity.vo.AgentVO;
import shiqifu.plane.util.AgentUtil;


import java.io.IOException;

@Slf4j
@Service
public class AgentServiceImpl {
    @Autowired
    private AgentUtil agentUtil;

    private OkHttpClient client=null;
    private Gson gson =null;
    public AgentServiceImpl(){
        client=new OkHttpClient();
        gson=new Gson();
    }
    public AgentResult fast_api(MultipartFile file) throws IOException {
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
                return agentUtil.entry(agentVO);
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
