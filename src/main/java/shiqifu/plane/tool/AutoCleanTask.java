package shiqifu.plane.tool;

import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@EnableScheduling
public class AutoCleanTask {
    @Scheduled(fixedRate = 600000)
    public void autoClean() throws IOException {
        String pathDirectory=System.getProperty("user.dir")+"\\plane\\src\\main\\resources\\script\\attack_engine";
        File dir = new File(pathDirectory);
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            for(File file:files){
                if(file.isFile()&&isImage(file)){
                    boolean delete = file.delete();
                    if(delete){
                        System.out.println("删除成功");
                    }else{
                        System.err.println("删除失败");
                    }
                }
            }
        }
    }
    private boolean isImage(File file){
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }
}
