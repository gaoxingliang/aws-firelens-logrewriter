package gaoxingliang.logrewriter.shaded;

import gaoxingliang.logrewriter.*;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.appender.*;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.*;
import org.apache.logging.log4j.status.*;
import pl.tkowalcz.tjahzi.*;
import pl.tkowalcz.tjahzi.github.*;
import pl.tkowalcz.tjahzi.http.*;
import pl.tkowalcz.tjahzi.log4j2.*;
import pl.tkowalcz.tjahzi.stats.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static java.util.Arrays.*;

/**
 * Builds LokiAppender instances.
 *
 * @param <B> The type to build
 */
public class LokiAppenderBuilder<B extends LokiAppenderBuilder<B>> extends AbstractAppender.Builder<B>
        implements org.apache.logging.log4j.core.util.Builder<LokiAppender> {

    private static final Logger LOGGER = StatusLogger.getLogger();

    private static final int BYTES_IN_MEGABYTE = 1024 * 1024;

    @PluginBuilderAttribute
    @Required(message = "No Loki address provided for LokiAppender")
    private String host;

    @PluginBuilderAttribute
    @Required(message = "No Loki port provided for LokiAppender")
    private int port;

    @PluginBuilderAttribute
    private int connectTimeoutMillis = 5000;

    @PluginBuilderAttribute
    private int readTimeoutMillis = 60_000;

    @PluginBuilderAttribute
    private int maxRetries = 3;

    @PluginBuilderAttribute
    private int bufferSizeMegabytes = 32;

    @PluginBuilderAttribute
    private boolean useOffHeapBuffer = true;

    @PluginBuilderAttribute
    private String logLevelLabel;

    @PluginBuilderAttribute
    private final long batchSize = 10_2400;

    @PluginBuilderAttribute
    private final long batchWait = 5;

    @PluginBuilderAttribute
    private int maxRequestsInFlight = 100;

    @PluginElement("Headers")
    private Header[] headers;

    @PluginElement("Labels")
    private Label[] labels;

    @Override
    public LokiAppender build() {
        ClientConfiguration configurationBuilder = ClientConfiguration.builder()
                .withHost(host)
                .withConnectionTimeoutMillis(connectTimeoutMillis)
                .withPort(port)
                .withMaxRetries(maxRetries)
                .withRequestTimeoutMillis(readTimeoutMillis)
                .withMaxRequestsInFlight(maxRequestsInFlight)
                .build();

        String[] additionalHeaders = stream(headers)
                .flatMap(header -> Stream.of(header.getName(), header.getValue()))
                .toArray(String[]::new);

        MutableMonitoringModuleWrapper monitoringModuleWrapper = new MutableMonitoringModuleWrapper();
        /**
         * The class {@link pl.tkowalcz.tjahzi.stats.StandardMonitoringModule} cause an error:
         * java.lang.IllegalStateException: AtomicBuffer is not correctly aligned: addressOffset=12 is not divisible by 8
         * 	at org.agrona.concurrent.UnsafeBuffer.verifyAlignment(UnsafeBuffer.java:331)
         * 	at org.agrona.concurrent.errors.DistinctErrorLog.<init>(DistinctErrorLog.java:124)
         * 	at org.agrona.concurrent.errors.DistinctErrorLog.<init>(DistinctErrorLog.java:112)
         * 	at pl.tkowalcz.tjahzi.stats.StandardMonitoringModule.<init>(StandardMonitoringModule.java:36)
         * 	at pl.tkowalcz.tjahzi.log4j2.LokiAppenderBuilder.build(LokiAppenderBuilder.java:93
         */
        monitoringModuleWrapper.setMonitoringModule(JsonSender.sharedStats);

        NettyHttpClient httpClient = HttpClientFactory
                .defaultFactory()
                .getHttpClient(
                        configurationBuilder,
                        monitoringModuleWrapper,
                        additionalHeaders
                );

        int bufferSizeBytes = getBufferSizeMegabytes() * BYTES_IN_MEGABYTE;
        if (!TjahziInitializer.isCorrectSize(bufferSizeBytes)) {
            LOGGER.warn("Invalid log buffer size {} - using nearest power of two greater than provided value, no less than 1MB. {}",
                    bufferSizeBytes,
                    GitHubDocs.LOG_BUFFER_SIZING.getLogMessage()
            );
        }

        LabelFactory labelFactory = new LabelFactory(logLevelLabel, labels);
        HashMap<String, String> lokiLabels = labelFactory.convertLabelsDroppingInvalid();
        logLevelLabel = labelFactory.validateLogLevelLabel(lokiLabels);

        LoggingSystem loggingSystem = new TjahziInitializer().createLoggingSystem(
                httpClient,
                monitoringModuleWrapper,
                lokiLabels,
                batchSize,
                TimeUnit.SECONDS.toMillis(batchWait),
                bufferSizeBytes,
                isUseOffHeapBuffer()
        );

        return new LokiAppender(
                getName(),
                getLayout(),
                getFilter(),
                isIgnoreExceptions(),
                getPropertyArray(),
                logLevelLabel,
                loggingSystem,
                monitoringModuleWrapper
        );
    }

    public String getHost() {
        return host;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public B setHost(String host) {
        this.host = host;
        return asBuilder();
    }

    public B setConnectTimeoutMillis(final int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
        return asBuilder();
    }

    public B setReadTimeoutMillis(final int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
        return asBuilder();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getBufferSizeMegabytes() {
        return bufferSizeMegabytes;
    }

    public void setBufferSizeMegabytes(int bufferSizeMegabytes) {
        this.bufferSizeMegabytes = bufferSizeMegabytes;
    }

    public boolean isUseOffHeapBuffer() {
        return useOffHeapBuffer;
    }

    public void setUseOffHeapBuffer(boolean useOffHeapBuffer) {
        this.useOffHeapBuffer = useOffHeapBuffer;
    }

    public String getLogLevelLabel() {
        return logLevelLabel;
    }

    public void setLogLevelLabel(String logLevelLabel) {
        this.logLevelLabel = logLevelLabel;
    }

    public long getBatchSize() {
        return batchSize;
    }

    public long getBatchWait() {
        return batchWait;
    }

    public void setMaxRequestsInFlight(int maxRequestsInFlight) {
        this.maxRequestsInFlight = maxRequestsInFlight;
    }

    public int getMaxRequestsInFlight() {
        return maxRequestsInFlight;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public Label[] getLabels() {
        return labels;
    }

    public void setLabels(Label[] labels) {
        this.labels = labels;
    }
}
