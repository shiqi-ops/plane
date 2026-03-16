package shiqifu.plane.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedDownloadModel {
    String fileField()default "model_file";
}
