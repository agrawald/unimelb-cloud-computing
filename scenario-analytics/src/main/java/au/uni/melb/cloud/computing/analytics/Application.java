package au.uni.melb.cloud.computing.analytics;

import au.uni.melb.cloud.computing.analytics.config.TopologyConfig;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

public class Application {
    public static void main(String[] args) throws Exception {
        final TopologyBuilder builder = TopologyConfig.INSTANCE.topologyBuilder();
        final Config config = TopologyConfig.INSTANCE.stormConfig();
        if (args != null && args.length > 0) {
            config.setNumWorkers(3);
            StormSubmitter.submitTopologyWithProgressBar(args[0], config, builder.createTopology());
        } else {
            startLocalCluster(config, builder);
        }
    }

    private static void startLocalCluster(final Config config, final TopologyBuilder builder) {
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("estimation", config, builder.createTopology());
        Utils.sleep(10000);
        cluster.killTopology("estimation");
        cluster.shutdown();
    }
}
