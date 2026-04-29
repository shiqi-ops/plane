package shiqifu.plane.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import shiqifu.plane.exception.UploadException;
import shiqifu.plane.service.impl.MinioServiceImpl;

@Slf4j
@RestController
@RequestMapping("/minio")
public class MinioController {
    private MinioServiceImpl minioService;
    public MinioController(MinioServiceImpl minioService) {
        this.minioService = minioService;
    }
    @PostMapping("/update")
    public String update(MultipartFile file)throws Exception{
        log.info("开始上传");
        String path=minioService.update(file);
        if(path==null){
            throw new UploadException("上传异常");
        }
        return path;
    }
    @PostMapping("/download")
    public void download(String filename, HttpServletResponse resp)throws Exception{
        log.info("开始获取");
        minioService.download(filename,resp);
    }
}
