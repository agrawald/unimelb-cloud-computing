package au.uni.melb.cloud.computing.analytics.bolt;

import au.uni.melb.cloud.computing.analytics.config.CouchDbConfig;
import au.uni.melb.cloud.computing.analytics.domain.Record;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class WorkLifeBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(WorkLifeBolt.class);
    private static final CouchDbClient COUCH_DB_CLIENT = CouchDbConfig.INSTANCE.client("db_work_life_balance");
    private static final Random RAND = new Random();
    private OutputCollector outputCollector;
    private List<Record> statistics;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
        statistics = COUCH_DB_CLIENT.view("_all_docs")
                .includeDocs(true)
                .startKey("start-key")
                .endKey("end-key")
                .query(Record.class);
    }

    @Override
    public void execute(Tuple tuple) {
        // tuple will have data, sentiment
        LOG.info("Tuple: {}", tuple.toString());
        final String data = tuple.getStringByField("data");
        final String sentiment = tuple.getStringByField("sentiment");

        // FIXME
        final int lat = this.random(-90, 90);
        final int lon = this.random(-180, 180);
        final Optional<Record> oRecord = statistics.parallelStream()
                .filter(record -> record.inside(lat, lon))
                .findFirst();
        oRecord.ifPresent(record -> outputCollector.emit(new Values(data, sentiment, record.getFeatureName())));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("data", "sentiment", "feature-name"));
    }

    private int random(int from, int to) {
        return RAND.ints(1, from, to)
                .findAny()
                .getAsInt();
    }
}

