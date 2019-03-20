package org.jetlinks.cloud;

import org.jetlinks.protocol.defaults.JetLinksProtocolSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@Configuration
public class ProtocolSupportsConfiguration {

    @Bean
    public JetLinksProtocolSupport jetLinksProtocolSupport() {
        return new JetLinksProtocolSupport();
    }
}
