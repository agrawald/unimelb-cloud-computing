package au.uni.melb.cloud.computing.analytics.spout;

import au.uni.melb.cloud.computing.analytics.config.CouchDbConfig;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.lightcouch.Changes;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TweeterSpout extends BaseRichSpout {
    private final static Logger LOG = LoggerFactory.getLogger(TweeterSpout.class);
    private final static CouchDbClient CLIENT = CouchDbConfig.INSTANCE.client("db_tweeter");

    SpoutOutputCollector outputCollector;
    Changes changes;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.outputCollector = spoutOutputCollector;
        this.changes = CLIENT
                .changes()
                .includeDocs(true)
                .since(CLIENT
                        .context()
                        .info()
                        .getUpdateSeq())
                .heartBeat(3000)
                .continuousChanges();
    }

    @Override
    public void nextTuple() {
        if (this.changes.hasNext()) {
            final ChangesResult.Row row = changes.next();
            LOG.info("Tuple: {}", row.getDoc());
            this.outputCollector.emit(new Values(row.getDoc().toString()));
        }
    }


    @Override
    public void close() {
        super.close();
        this.changes.stop();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("data"));
    }
}
