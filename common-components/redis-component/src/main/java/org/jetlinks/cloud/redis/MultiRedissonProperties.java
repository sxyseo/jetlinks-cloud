package org.jetlinks.cloud.redis;

import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import lombok.Getter;
import lombok.Setter;
import org.redisson.config.TransportMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "jetlinks.redis")
@Getter
@Setter
public class MultiRedissonProperties {

    private Map<String, RedissonProperties> clients = new HashMap<>();

    private TransportMode transportMode =
            Epoll.isAvailable() ? TransportMode.EPOLL :
                    KQueue.isAvailable() ? TransportMode.KQUEUE :
                            TransportMode.NIO;

    private int threadSize = Runtime.getRuntime().availableProcessors() * 2;


}
