package org.jetlinks.cloud.redis;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.Setter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhouhao
 * @since 1.1.0
 */
public class DefaultRedissonClientRepository implements RedissonClientRepository {
    @Getter
    @Setter
    private Map<String, RedissonProperties> clients = new HashMap<>();

    private Map<String, RedissonClient> repository = new HashMap<>();

    private EventLoopGroup eventLoopGroup;

    @Getter
    @Setter
    private TransportMode transportMode = TransportMode.NIO;

    @Getter
    @Setter
    private int threadSize = Runtime.getRuntime().availableProcessors() * 2;

    @PreDestroy
    public void shutdown() {
        for (RedissonClient client : repository.values()) {
            client.shutdown();
        }
    }

    @PostConstruct
    public void init() {
        if (transportMode == TransportMode.EPOLL) {
            eventLoopGroup = new EpollEventLoopGroup(threadSize, new DefaultThreadFactory("redisson-epoll-netty"));
        } else if (transportMode == TransportMode.KQUEUE) {
            eventLoopGroup = new KQueueEventLoopGroup(threadSize, new DefaultThreadFactory("redisson-kqueue-netty"));
        } else if (transportMode == TransportMode.NIO) {
            eventLoopGroup = new NioEventLoopGroup(threadSize, new DefaultThreadFactory("redisson-nio-netty"));
        }
        for (Map.Entry<String, RedissonProperties> entry : clients.entrySet()) {
            Config config = entry.getValue().toConfig(clients.get("default"));
            config.setEventLoopGroup(eventLoopGroup);
            config.setTransportMode(transportMode);
            repository.put(entry.getKey(), Redisson.create(config));
        }
    }

    public Optional<RedissonClient> getClient(String name) {
        return Optional.ofNullable(repository.get(name));
    }

}
