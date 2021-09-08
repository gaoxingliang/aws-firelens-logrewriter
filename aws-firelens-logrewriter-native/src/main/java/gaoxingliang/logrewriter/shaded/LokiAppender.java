package gaoxingliang.logrewriter.shaded;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.*;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.plugins.*;
import pl.tkowalcz.tjahzi.*;
import pl.tkowalcz.tjahzi.log4j2.*;
import pl.tkowalcz.tjahzi.stats.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Loki Appender.
 */
@Plugin(name = "Loki", category = Node.CATEGORY, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class LokiAppender extends AbstractAppender {

    /**
     * @return a builder for a LokiAppender.
     */
    @PluginBuilderFactory
    public static <B extends LokiAppenderBuilder<B>> B newBuilder() {
        return new LokiAppenderBuilder<B>().asBuilder();
    }

    private final LoggingSystem loggingSystem;
    private final MutableMonitoringModuleWrapper monitoringModuleWrapper;

    private final AppenderLogic appenderLogic;

    LokiAppender(
            String name,
            Layout<? extends Serializable> layout,
            Filter filter,
            boolean ignoreExceptions,
            Property[] properties,
            String logLevelLabel,
            LoggingSystem loggingSystem,
            MutableMonitoringModuleWrapper monitoringModuleWrapper
    ) {
        super(
                name,
                filter,
                layout,
                ignoreExceptions,
                properties
        );
        this.monitoringModuleWrapper = monitoringModuleWrapper;
        Objects.requireNonNull(layout, "layout");

        this.loggingSystem = loggingSystem;
        this.appenderLogic = new AppenderLogic(
                loggingSystem,
                logLevelLabel
        );
    }

    public LoggingSystem getLoggingSystem() {
        return loggingSystem;
    }

    /**
     * This is an entry point to set monitoring (statistics) hooks for this appender. This
     * API is in beta and is subject to change (and probably will).
     */
    public void setMonitoringModule(MonitoringModule monitoringModule) {
        monitoringModuleWrapper.setMonitoringModule(monitoringModule);
    }

    @Override
    public void start() {
        loggingSystem.start();
        super.start();
    }

    @Override
    public void append(LogEvent event) {
        appenderLogic.append(getLayout(), event);
    }

    @Override
    public boolean stop(
            long timeout,
            TimeUnit timeUnit
    ) {
        setStopping();

        boolean stopped = super.stop(
                timeout,
                timeUnit,
                false
        );

        appenderLogic.close(
                timeout,
                timeUnit
        );

        setStopped();
        return stopped;
    }

    @Override
    public String toString() {
        return "LokiAppender{" +
                "name=" + getName() +
                ", state=" + getState() +
                '}';
    }
}
