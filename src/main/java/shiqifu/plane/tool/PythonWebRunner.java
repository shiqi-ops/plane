package shiqifu.plane.tool;

import io.jsonwebtoken.io.IOException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class PythonWebRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception{

        String path = System.getProperty("user.dir") + "\\plane\\src\\main\\resources\\script\\attack_engine\\api_server.py";
        String pythonExe = System.getProperty("user.dir") + "\\plane\\src\\main\\resources\\script\\python_env\\scripts\\python.exe";
        String workDirectory = System.getProperty("user.dir") + "\\plane\\src\\main\\resources\\script\\attack_engine";

        ProcessBuilder pb = new ProcessBuilder(
                pythonExe,
                "-m",
                "uvicorn",
                "api_server:app",
                "--reload"
        );

        pb.directory(new File(workDirectory));

        pb.redirectErrorStream(true);

        Process process = pb.start();

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Python] " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
    }
}
