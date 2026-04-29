package shiqifu.plane.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CleanData {
    String cleanData()default "cleanData";
}
