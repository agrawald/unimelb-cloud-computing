package au.uni.melb.cloud.computing.analytics.bolt;

import au.uni.melb.cloud.computing.analytics.domain.WorkLifeSentiment;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WorkLifeSentimentCounterBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(WorkLifeSentimentCounterBolt.class);
    private static final Map<String, WorkLifeSentiment> WORK_LIFE_SENTIMENT_MAP = new HashMap<>();
    private OutputCollector outputCollector;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        LOG.info("Tuple: {}", tuple.toString());
        if (tuple.contains("sentiment") && tuple.contains("feature-name")) {
            final String sentiment = tuple.getStringByField("sentiment");
            final String featureName = tuple.getStringByField("feature-name");
            final String key = sentiment + ":" + featureName;
            WorkLifeSentiment workLifeSentiment;
            if (WORK_LIFE_SENTIMENT_MAP.containsKey(key)) {
                workLifeSentiment = WORK_LIFE_SENTIMENT_MAP.get(key);
            } else {
                workLifeSentiment = new WorkLifeSentiment(sentiment, featureName);
            }
            workLifeSentiment.increment();
            outputCollector.emit(new Values(workLifeSentiment));
        } else {
            outputCollector.ack(tuple);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sentiment-work-life-count"));
    }
}
