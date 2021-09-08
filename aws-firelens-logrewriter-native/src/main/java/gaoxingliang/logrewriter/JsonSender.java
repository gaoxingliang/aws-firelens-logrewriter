package gaoxingliang.logrewriter;

import gaoxingliang.logrewriter.shaded.LokiAppenderBuilder;
import gaoxingliang.logrewriter.shaded.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.impl.*;
import org.apache.logging.log4j.message.*;
import org.json.*;
import pl.tkowalcz.tjahzi.log4j2.*;
import pl.tkowalcz.tjahzi.stats.StandardMonitoringModule;
import pl.tkowalcz.tjahzi.stats.*;

import javax.enterprise.context.*;
import java.util.*;
import java.util.concurrent.*;

@ApplicationScoped
public class JsonSender {

    public static final MonitoringModule sharedStats = new StandardMonitoringModule();


    private static final Map<Integer, gaoxingliang.logrewriter.shaded.LokiAppender> hash2Labels = new ConcurrentHashMap<>();



    public static void log(final String lokiHost, final int lokiPort, String body) {
        try {
            JSONObject obj = new JSONObject(body);
            JSONArray streams = obj.getJSONArray("streams");
            streams.forEach(stream -> {
                JSONObject eachObj = (JSONObject) stream;
                int hash = eachObj.getJSONObject("stream").toString().hashCode();
                gaoxingliang.logrewriter.shaded.LokiAppender a = hash2Labels.computeIfAbsent(hash, k -> {
                    List<Label> labels = new ArrayList<>(1);
                    JSONObject oneloglabels = eachObj.getJSONObject("stream");
                    oneloglabels.keySet().forEach(loglabel -> {
                        labels.add(Label.createLabel(loglabel, oneloglabels.get(loglabel).toString()));
                    });
                    System.out.println("New label:" + eachObj.getJSONObject("stream").toString() + " hash" + hash);
                    return init(lokiHost, lokiPort, labels.toArray(new Label[0]));
                });
                JSONArray vals = eachObj.getJSONArray("values");
                for (int i = 0; i < vals.length(); i++) {
                    JSONArray x = vals.getJSONArray(i);
                    String lineAll = x.getString(1);
                    String realLog = new JSONObject(lineAll).getString("log");
                    LogEvent k = new Log4jLogEvent.Builder().setMessage(new SimpleMessage(realLog)).build();
                    a.append(k);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static gaoxingliang.logrewriter.shaded.LokiAppender init(String lokiHost, int lokiPort, Label labels[]) {
        gaoxingliang.logrewriter.shaded.LokiAppenderBuilder b = new LokiAppenderBuilder();
        b.setHost(lokiHost);
        b.setPort(lokiPort);
        b.setLabels(labels);
        b.setHeaders(new Header[0]);
        b.withName("root");
        b.setBufferSizeMegabytes(16);
        // https://quarkus.io/guides/logging
        // the native can't translate the format correctly
        //b.withLayout(PatternLayout.newBuilder().withPattern("%m").build());
        b.withLayout(new CustomMessageLayout());
        gaoxingliang.logrewriter.shaded.LokiAppender lokiAppender = b.build();
        lokiAppender.start();
        System.out.println("LOKI " + lokiHost + " " + lokiPort);
        return lokiAppender;
    }

    public static void main(String[] args) {

        System.out.println(System.currentTimeMillis());

//        // test
        log("a", 123, "{\"streams\":[{\"stream\": {\"app\":\"test\"}, \"values\":[[\"1629791107687\",\"{\\\"container_id\\\": " +
                "\\\"82b1fd\\\",\\\"log\\\": \\\"yesitis\\\"}\"]]}]}");
    }
}
