package au.uni.melb.cloud.computing.analytics.config;

import au.uni.melb.cloud.computing.analytics.bolt.*;
import au.uni.melb.cloud.computing.analytics.spout.TweeterSpout;
import org.apache.storm.Config;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public enum TopologyConfig {
    INSTANCE;

    public TopologyBuilder topologyBuilder() {
        final TopologyBuilder builder = new TopologyBuilder();

        // topology for publishing sentiment count
        builder.setSpout("10", new TweeterSpout(), 1);
        builder.setBolt("20", new SentimentBolt(), 5)
                .shuffleGrouping("10");
        builder.setBolt("30", new SentimentCounterBolt(), 1)
                .fieldsGrouping("20", new Fields("sentiment"));
        builder.setBolt("40", new SentimentPublisherBolt("sentiment"), 5)
                .shuffleGrouping("30");

        //topology for publishing work-life tweets
        builder.setBolt("31", new WorkLifeBolt(), 5)
                .fieldsGrouping("20", new Fields("sentiment"));
        builder.setBolt("41", new WorkLifeSentimentCounterBolt(), 1)
                .fieldsGrouping("31", new Fields("sentiment", "feature-name"));
        builder.setBolt("51", new WorkLifeSentimentPublisherBolt("sentiment-work-life"), 5)
                .shuffleGrouping("41");

        return builder;
    }

    public Config stormConfig() {
        final Config conf = new Config();
        conf.setDebug(true);
        return conf;
    }
}
