package au.uni.melb.cloud.computing.analytics.bolt;

import au.uni.melb.cloud.computing.analytics.config.RabbitMqConfig;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public abstract class RabbitPublisherBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitPublisherBolt.class);
    private final static Gson gson = new Gson();
    private static Channel RABBIT_CHANNEL;
    private final String qName;
    private OutputCollector outputCollector;

    public RabbitPublisherBolt(final String qName) {
        this.qName = qName;
    }

    public void declareQueue() {
        try {
            RABBIT_CHANNEL = RabbitMqConfig.INSTANCE.channel();
            RABBIT_CHANNEL.queueDeclare(qName, false, false, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
        this.declareQueue();
    }

    @Override
    public void execute(Tuple tuple) {
        LOG.info("Tuple: {}", tuple.toString());
        String json = gson.toJson(tuple.getValue(0));
        try {
            RABBIT_CHANNEL.basicPublish("", qName, null, json.getBytes());
            outputCollector.ack(tuple);
        } catch (IOException e) {
            e.printStackTrace();
            outputCollector.fail(tuple);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        try {
            RABBIT_CHANNEL.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
