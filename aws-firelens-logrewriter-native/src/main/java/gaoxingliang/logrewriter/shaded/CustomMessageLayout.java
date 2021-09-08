package gaoxingliang.logrewriter.shaded;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.layout.*;

import java.nio.charset.*;

public class CustomMessageLayout extends MessageLayout {
    @Override
    public byte[] toByteArray(LogEvent event) {
        return event.getMessage().getFormattedMessage().getBytes(StandardCharsets.UTF_8);
    }
}
