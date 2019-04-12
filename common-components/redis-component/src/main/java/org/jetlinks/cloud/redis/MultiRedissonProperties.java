package org.jetlinks.cloud.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "jetlinks.redis")
public class MultiRedissonProperties {
    @Getter
    @Setter
    private Map<String, RedissonProperties> clients = new HashMap<>();

}
