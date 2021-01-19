package au.uni.melb.cloud.computing.analytics.bolt;

import com.jayway.jsonpath.JsonPath;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

public class SentimentBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(SentimentBolt.class);
    private static final Function<Integer, String> FIND_SENTIMENT = sentiment -> {
        if (sentiment <= -6) return "dissatisfied";
        if (sentiment <= -1) return "unhappy";
        if (sentiment == 0) return "neutral";
        if (sentiment <= 5) return "happy";
        return "satisfied";
    };
    private static final Function<String, Integer> GET_SENTIMENT = data -> {
        try {
            return JsonPath.parse(data).read("$.sentiment", Integer.class);
        } catch (Exception e) {
            LOG.error("Error while fetching json path", e);
            return 0;
        }
    };
    protected OutputCollector outputCollector;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        LOG.info("Tuple: {}", tuple.toString());
        final String data = tuple.getString(0);
        final String sentiment = GET_SENTIMENT.andThen(FIND_SENTIMENT).apply(data);
        if (sentiment != null) {
            this.outputCollector.emit(new Values(data, sentiment));
        } else {
            outputCollector.ack(tuple);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("data", "sentiment"));
    }
}

