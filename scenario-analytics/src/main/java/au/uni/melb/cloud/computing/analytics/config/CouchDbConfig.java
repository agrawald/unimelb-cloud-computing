package au.uni.melb.cloud.computing.analytics.config;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public enum CouchDbConfig {
    INSTANCE;
    private final static Map<String, CouchDbClient> COUCH_DB_CLIENT_MAP = new HashMap<>();
    private Properties properties = new Properties();

    CouchDbConfig() {
        try {
            final InputStream is = CouchDbConfig.class.getClassLoader().getResourceAsStream("couchdb.properties");
            properties.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CouchDbProperties couchDbProperties(final String dbName) {
        return new CouchDbProperties()
                .setProtocol(properties.getProperty("couchdb.protocol"))
                .setHost(properties.getProperty("couchdb.host"))
                .setPort(Integer.parseInt(properties.getProperty("couchdb.port")))
                .setDbName(dbName);
    }

    public CouchDbClient client(final String dbName) {
        final CouchDbClient client;
        if (COUCH_DB_CLIENT_MAP.containsKey(dbName)) {
            client = COUCH_DB_CLIENT_MAP.get(dbName);
        } else {
            client = new CouchDbClient(couchDbProperties(dbName));
            COUCH_DB_CLIENT_MAP.put(dbName, client);
        }
        return client;
    }
}
