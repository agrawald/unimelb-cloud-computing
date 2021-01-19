package au.uni.melb.cloud.computing.analytics.bolt;

public class WorkLifeSentimentPublisherBolt extends RabbitPublisherBolt {
    public WorkLifeSentimentPublisherBolt(final String qName) {
        super(qName);
    }
}
