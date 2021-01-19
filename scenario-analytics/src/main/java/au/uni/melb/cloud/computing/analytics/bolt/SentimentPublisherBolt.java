package au.uni.melb.cloud.computing.analytics.bolt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SentimentPublisherBolt extends RabbitPublisherBolt {
    private static final Logger LOG = LoggerFactory.getLogger(SentimentPublisherBolt.class);

    public SentimentPublisherBolt(final String qName) {
        super(qName);
    }
}
