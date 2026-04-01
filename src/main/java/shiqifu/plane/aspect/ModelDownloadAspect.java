package shiqifu.plane.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import shiqifu.plane.entity.dto.EvaluateOwnDTO;
import shiqifu.plane.annotation.NeedDownloadModel;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class ModelDownloadAspect {
    private final String tmp_dir="D:/java//xiaowebproject//mall//mall//tmp_dir/";

    @Pointcut("@annotation(shiqifu.plane.annotation.NeedDownloadModel)")
    public void downloadModelPointcut() {}

    @Around("downloadModelPointcut()")
    public Object downloadModelAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        NeedDownloadModel  needDownloadModel = methodSignature.getMethod().getAnnotation(NeedDownloadModel.class);
        Object[] args = joinPoint.getArgs();
        if(args==null||needDownloadModel==null){
            return joinPoint.proceed();
        }
        EvaluateOwnDTO evaluateOwnDTO = null;
        for (Object arg : args) {
            if (arg instanceof EvaluateOwnDTO) {
                evaluateOwnDTO = (EvaluateOwnDTO) arg;
                break;
            }
        }
        if(evaluateOwnDTO==null){
                log.warn("未找到 EvaluateOwnDTO 参数，跳过模型下载逻辑");
                return joinPoint.proceed();
        }
        MultipartFile file = evaluateOwnDTO.getModel_file();
        log.info("开始上传文件");
        String file_path=saveFileToLocal(file);
        evaluateOwnDTO.setModel_pth(file_path);
        return joinPoint.proceed(args);
    }
    private String saveFileToLocal(MultipartFile file) throws IOException {
        File file_path=new File(tmp_dir);
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if(originalFilename!=null&&originalFilename.contains(".")){
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString()+suffix;
        File destFile = new File(file_path,fileName);
        file.transferTo(destFile);
        return destFile.getAbsolutePath().replace("\\", "/");
    }
}
