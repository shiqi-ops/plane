package shiqifu.plane.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import shiqifu.plane.annotation.CleanData;
import shiqifu.plane.entity.dto.EvaluateDTO;
import shiqifu.plane.entity.dto.EvaluateMoreDTO;
import shiqifu.plane.exception.DataErrorException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Slf4j
@Component
public class CleanDataAspect {
    @Pointcut("@annotation(shiqifu.plane.annotation.CleanData)")
    public void downloadModelPointcut() {}

    @Around("downloadModelPointcut()")
    public Object downloadModelAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CleanData cleanData = signature.getMethod().getAnnotation(CleanData.class);
        Object[] args = joinPoint.getArgs();
        if(cleanData==null||args==null){
            return joinPoint.proceed();
        }
        Object object=null;
        for(Object arg:args){
            if(arg instanceof EvaluateDTO){
              object=arg;
              break;
            }
            if(arg instanceof EvaluateMoreDTO) {
                object=arg;
                break;
            }
        }
        if(object instanceof EvaluateDTO){
            String model=((EvaluateDTO) object).getModel();
            if(model==null||!model.matches("^[a-zA-Z0-9_]+$")){
                throw new DataErrorException();
            }
            String attack=((EvaluateDTO) object).getAttack();
            if(attack==null||!attack.matches("^[a-zA-Z0-9_]+$")){
                throw new DataErrorException();
            }
            String dataset=((EvaluateDTO) object).getDataset();
            if(dataset==null||!dataset.matches("^[a-zA-Z0-9_]+$")){
                throw new DataErrorException();
            }
            String eps=((EvaluateDTO) object).getEps();
            if(eps==null||!eps.matches("^[a-zA-Z0-9_.]+$")){
                throw new DataErrorException();
            }
        }
        if(object instanceof EvaluateMoreDTO){
            String model=((EvaluateMoreDTO) object).getModel();
            if(model==null||!model.matches("^[a-zA-Z0-9_]+$")){
                throw new DataErrorException();
            }
            String attack_group=((EvaluateMoreDTO) object).getAttack_group();
            if(attack_group==null||!attack_group.matches("^[a-zA-Z0-9_]+$")){
                throw new DataErrorException();
            }
            String dataset=((EvaluateMoreDTO) object).getDataset();
            if(dataset==null||!dataset.matches("^[a-zA-Z0-9_]+$")){
                throw new DataErrorException();
            }
            String eps=((EvaluateMoreDTO) object).getEps();
            if(eps==null||!eps.matches("^[a-zA-Z0-9_.]+$")){
                throw new DataErrorException();
            }
        }
        return joinPoint.proceed(args);
    }
}
