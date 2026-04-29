package shiqifu.plane.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageUtil {
    public static String imageToBase64(String imagePath) throws IOException {
        File file = new File(imagePath);
        byte[] bytes = Files.readAllBytes(file.toPath());
        return Base64.encodeBase64String(bytes);
    }
}
