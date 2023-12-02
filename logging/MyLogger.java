package account.logging;

import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Configuration
public class MyLogger {
    public static Logger setupLogger() throws IOException {
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;

        fh = new FileHandler("../mylog.txt", true);
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

        return logger;
    }
}
