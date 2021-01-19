package au.uni.melb.cloud.computing.analytics.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public enum RabbitMqConfig {
    INSTANCE;
    public final Properties properties = new Properties();

    RabbitMqConfig() {
        try {
            final InputStream is = RabbitMqConfig.class.getClassLoader().getResourceAsStream("rabbitmq.properties");
            properties.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ConnectionFactory connectionFactory() {
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(properties.getProperty("rabbitmq.host"));
        factory.setPort(Integer.parseInt(properties.getProperty("rabbitmq.port")));
        return factory;
    }

    private Connection connection() throws IOException, TimeoutException {
        return this.connectionFactory().newConnection();
    }

    public Channel channel() throws IOException, TimeoutException {
        return connection().createChannel();
    }
}
