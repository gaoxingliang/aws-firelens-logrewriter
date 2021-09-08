package gaoxingliang.logrewriter.shaded;

import io.netty.buffer.*;
import io.netty.handler.codec.http.*;
import pl.tkowalcz.tjahzi.stats.*;

import java.util.concurrent.atomic.*;

public class StandardMonitoringModule implements MonitoringModule {

    private static final int ERROR_LOG_CAPACITY = 1024;

    private final AtomicLong droppedPuts = new AtomicLong();
    private final AtomicLong httpConnectAttempts = new AtomicLong();

    private final AtomicLong sentHttpRequests = new AtomicLong();
    private final AtomicLong sentBytes = new AtomicLong();

    private final AtomicLong failedHttpRequests = new AtomicLong();
    private final AtomicLong retriedHttpRequests = new AtomicLong();
    private final AtomicLong httpResponses = new AtomicLong();
    private final AtomicLong channelInactive = new AtomicLong();
    private final AtomicLong agentErrors = new AtomicLong();


    public StandardMonitoringModule() {
        StatsDumpingThread thread = new StatsDumpingThread(this);
        if (thread.isEnabled()) {
            thread.start();
        }
    }

    @Override
    public void incrementDroppedPuts() {
        droppedPuts.incrementAndGet();
    }

    @Override
    public void incrementDroppedPuts(Throwable throwable) {
        incrementDroppedPuts();
    }

    @Override
    public void incrementSentHttpRequests(int sizeBytes) {
        sentHttpRequests.incrementAndGet();
        sentBytes.addAndGet(sizeBytes);
    }

    public long getSentHttpRequests() {
        return sentHttpRequests.get();
    }

    public long getSentBytes() {
        return sentBytes.get();
    }

    @Override
    public void incrementFailedHttpRequests() {
        failedHttpRequests.incrementAndGet();
    }

    public long getFailedHttpRequests() {
        return failedHttpRequests.get();
    }

    @Override
    public void incrementRetriedHttpRequests() {
        retriedHttpRequests.incrementAndGet();
    }

    public long getRetriedHttpRequests() {
        return retriedHttpRequests.get();
    }

    @Override
    public void addAgentError(Throwable throwable) {
        agentErrors.incrementAndGet();
    }

    @Override
    public void incrementHttpConnectAttempts() {
        httpConnectAttempts.incrementAndGet();
    }

    public long getHttpConnectAttempts() {
        return httpConnectAttempts.get();
    }

    @Override
    public void incrementChannelInactive() {
        channelInactive.incrementAndGet();
    }

    @Override
    public void incrementHttpResponses() {
        httpResponses.incrementAndGet();
    }

    public long getHttpResponses() {
        return httpResponses.get();
    }

    @Override
    public void addPipelineError(Throwable cause) {
    }

    @Override
    public void incrementHttpErrors(HttpResponseStatus status, ByteBuf content) {
        failedHttpRequests.incrementAndGet();
    }

    @Override
    public void recordResponseTime(long time) {
    }

    @Override
    public String toString() {
        return "StandardMonitoringModule{" +
                "droppedPuts=" + droppedPuts +
                ", httpConnectAttempts=" + httpConnectAttempts +
                ", sentHttpRequests=" + sentHttpRequests +
                ", sentKilobytes=" + (sentBytes.longValue() / 1024) +
                ", failedHttpRequests=" + failedHttpRequests +
                ", retriedHttpRequests=" + retriedHttpRequests +
                ", httpResponses=" + httpResponses +
                ", channelInactive=" + channelInactive +
                ", agentErrors=" + agentErrors +
                '}';
    }
}
