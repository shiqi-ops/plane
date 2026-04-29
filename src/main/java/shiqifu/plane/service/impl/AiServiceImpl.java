package shiqifu.plane.service.impl;

import com.google.gson.Gson;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import shiqifu.plane.entity.dto.ChatDTO;
import shiqifu.plane.entity.vo.AiReportVO;
import shiqifu.plane.service.CousultantService;
import shiqifu.plane.util.PdfUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class AiServiceImpl {
    private final MinioClient minioClient;
    public AiServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }
    @Autowired
    private CousultantService cousultantService;
    private AiReportVO parse(String id,Map<String, Object> result){
        Map<String, Object> parsedData = (Map<String, Object>) result.get("data");
        List<String>parse = (List<String>) parsedData.get("imagePaths");
        List<byte[]>image=new ArrayList<>();
        if(parse!=null){
            for(String path:parse){
                if(path.contains(".")){
                    path=path.split(",")[1];
                }
                byte[]tmp= Base64.getDecoder().decode(path);
                image.add(tmp);
            }
        }
        String jsonData = new Gson().toJson(parsedData);
        AiReportVO aiResult = cousultantService.generateSafetyReport(id,jsonData,image);
        return aiResult;
    }
    public AiReportVO parse(String id,String path) throws Exception{
        InputStream inputs =minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("shiqifu")
                        .object(path)
                        .build()
        );
        String name= UUID.randomUUID().toString();
        File file_temp= File.createTempFile("minio",name);
        file_temp.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(file_temp)){
            int len;
            byte[] buffer = new byte[1024];
            while((len = inputs.read(buffer)) != -1){
                fos.write(buffer,0,len);
            }
            fos.flush();
        }
        finally {
            inputs.close();
        }
        String temp_path=file_temp.getAbsolutePath();
        Map<String, Object>mp= PdfUtil.parseByUrl(temp_path);

        return parse(id,mp);
    }
    public Flux<String>chat_stream(ChatDTO chatDTO){
        String id = chatDTO.getId();

        String messages = chatDTO.getMessages();
        return cousultantService.chat_stream(id,messages)
                .flatMap(content -> {
                    String[] chars = content.split("");
                    return Flux.fromArray(chars);
                });
    }
}
