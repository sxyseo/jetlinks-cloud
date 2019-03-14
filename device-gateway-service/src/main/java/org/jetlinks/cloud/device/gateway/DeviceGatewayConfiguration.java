package org.jetlinks.cloud.device.gateway;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.mqtt.MqttServerOptions;
import lombok.extern.slf4j.Slf4j;
import org.jetlinks.cloud.device.gateway.vertx.VerticleSupplier;
import org.jetlinks.gateway.session.DefaultDeviceSessionManager;
import org.jetlinks.protocol.ProtocolSupports;
import org.jetlinks.registry.api.DeviceMonitor;
import org.jetlinks.registry.api.DeviceRegistry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@Configuration
public class DeviceGatewayConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "vertx")
    public VertxOptions vertxOptions() {
        return new VertxOptions();
    }

    @Bean
    @ConfigurationProperties(prefix = "vertx.mqtt")
    public MqttServerOptions mqttServerOptions() {
        return new MqttServerOptions();
    }

    @Bean
    public Vertx vertx(VertxOptions vertxOptions) {
        return Vertx.vertx(vertxOptions);
    }

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public DefaultDeviceSessionManager deviceSessionManager(Environment environment,
                                                            ProtocolSupports protocolSupports,
                                                            DeviceRegistry registry,
                                                            DeviceMonitor deviceMonitor,
                                                            ScheduledExecutorService executorService) {
        DefaultDeviceSessionManager sessionManager = new DefaultDeviceSessionManager();
        sessionManager.setServerId(environment.getProperty("gateway.server-id"));
        sessionManager.setProtocolSupports(protocolSupports);
        sessionManager.setDeviceRegistry(registry);
        sessionManager.setExecutorService(executorService);
        sessionManager.setDeviceMonitor(deviceMonitor);
        return sessionManager;
    }

    @Bean
    public MQTTServerInitializer commandLineRunner() {
        return new MQTTServerInitializer();
    }

    @Slf4j
    public static class MQTTServerInitializer implements CommandLineRunner, DisposableBean {

        @Autowired
        private VerticleFactory verticleFactory;

        @Autowired
        private List<VerticleSupplier> verticles;

        @Autowired
        private Vertx vertx;

        @Override
        public void run(String... args) {
            vertx.registerVerticleFactory(verticleFactory);
            for (VerticleSupplier supplier : verticles) {
                DeploymentOptions options = new DeploymentOptions();
                options.setHa(true);
                options.setInstances(supplier.getInstances());
                vertx.deployVerticle(supplier, options, e -> {
                    if (!e.succeeded()) {
                        log.error("deploy verticle :{} error", supplier, e.succeeded(), e.cause());
                    } else {
                        log.debug("deploy verticle :{} success", supplier);
                    }
                });
            }
        }

        @Override
        public void destroy() throws Exception {
            log.debug("close vertx");
            CountDownLatch latch = new CountDownLatch(1);
            vertx.close(result -> {
                log.debug("close vertx done");
                latch.countDown();
            });
            latch.await(10, TimeUnit.SECONDS);
        }
    }
}
