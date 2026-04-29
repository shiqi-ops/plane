package shiqifu.plane.service.impl;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shiqifu.plane.exception.DownloadException;

import java.io.InputStream;
import java.util.UUID;


@Service
public class MinioServiceImpl {
    private final MinioClient minioClient;
    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }
    public String update(MultipartFile file) throws Exception{
        try (InputStream inputStream = file.getInputStream()){
            String random=UUID.randomUUID().toString();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("shiqifu")
                            .object("path/"+random+file.getOriginalFilename())
                            .stream(inputStream,file.getSize(),-1)
                            .contentType(file.getContentType())
                            .build()
            );
            return random+file.getOriginalFilename();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void download(String filename, HttpServletResponse resp) throws Exception{
       GetObjectResponse inputStream=minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("shiqifu")
                        .object(filename)
                        .build()
        );
        if(inputStream==null){
            throw new DownloadException("下载失败，请输入正确文件名");
        }
        resp.reset();
        resp.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(filename, "UTF-8"));
        resp.setContentType("application/octet-stream");

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            resp.getOutputStream().write(buffer, 0, bytesRead);
        }
        inputStream.close();
    }
}
