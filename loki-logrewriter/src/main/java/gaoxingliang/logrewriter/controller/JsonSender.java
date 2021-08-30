package gaoxingliang.logrewriter.controller;

import com.alibaba.fastjson.*;
import lombok.extern.log4j.*;
import org.apache.commons.lang3.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.impl.*;
import org.apache.logging.log4j.core.layout.*;
import org.apache.logging.log4j.message.*;
import pl.tkowalcz.tjahzi.log4j2.*;

import java.util.*;
import java.util.concurrent.*;

@Log4j2
public class JsonSender {
    private static final Map<Integer, LokiAppender> hash2Labels = new ConcurrentHashMap<>();

    public static final String LOKI_HOST = getEnv("LOKI_HOST", "127.0.0.1");
    public static final int LOKI_PORT = Integer.valueOf(getEnv("LOKI_PORT", "3100"));

    private static final String getEnv(String k, String d) {
        return Optional.ofNullable(System.getenv(k)).orElse(d);
    }

    public static void log(String body) {
        try {
            JSONObject obj = JSON.parseObject(body);
            JSONArray streams = obj.getJSONArray("streams");
            streams.forEach(stream -> {
                JSONObject eachObj = (JSONObject) stream;
                int hash = eachObj.getJSONObject("stream").hashCode();
                LokiAppender a = hash2Labels.computeIfAbsent(hash, k -> {
                    List<Label> labels = new ArrayList<>(1);
                    eachObj.getJSONObject("stream").getInnerMap()
                            .forEach((la, lv) -> labels.add(Label.createLabel(la, lv.toString())));
                    return init(labels.toArray(new Label[0]));
                });
                JSONArray vals = eachObj.getJSONArray("values");
                for (int i = 0; i < vals.size(); i++) {
                    JSONArray x  = vals.getJSONArray(i);
                    String lineAll = x.getString(1);
                    String realLog = JSONObject.parseObject(lineAll).getString("log");
                    LogEvent k = new Log4jLogEvent.Builder().setMessage(new SimpleMessage(realLog)).build();
                    a.append(k);
                }
            });
        } catch (Exception e) {
            LOG.error("Fail to process {} with error {}", StringUtils.abbreviate(body, 500), e.getMessage());
        }
    }

    private static LokiAppender init(Label labels[]) {
        LokiAppenderBuilder b = new LokiAppenderBuilder();
        b.setHost(LOKI_HOST);
        b.setPort(LOKI_PORT);
        b.setLabels(labels);
        b.setHeaders(new Header[0]);
        b.withName("root");
        b.setBufferSizeMegabytes(Integer.valueOf(getEnv("BUFFER_SIZE_MB", "16")));
        b.withLayout(PatternLayout.newBuilder().withPattern("%msg").build());
        LokiAppender lokiAppender = b.build();
        lokiAppender.start();
        return lokiAppender;
    }
}
