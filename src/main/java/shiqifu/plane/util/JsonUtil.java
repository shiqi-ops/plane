package shiqifu.plane.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ietf.jgss.GSSContext;

public class JsonUtil {
    private static final Gson gson=new GsonBuilder()
            .setPrettyPrinting()
            .create();
    public String toJson(Object o){
        return gson.toJson(o);
    }
}
