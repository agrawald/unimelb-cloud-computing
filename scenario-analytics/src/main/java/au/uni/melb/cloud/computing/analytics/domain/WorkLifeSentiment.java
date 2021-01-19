package au.uni.melb.cloud.computing.analytics.domain;

public class WorkLifeSentiment extends Sentiment {
    private String name;
    private String featureName;

    public WorkLifeSentiment(String sentiment, String featureName) {
        super(sentiment);
        this.name = "work-life";
        this.featureName = featureName;
    }
}
