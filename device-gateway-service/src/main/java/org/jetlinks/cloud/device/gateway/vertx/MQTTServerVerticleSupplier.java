package org.jetlinks.cloud.device.gateway.vertx;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttServerOptions;
import org.hswebframework.web.service.GenericsPayloadApplicationEvent;
import org.jetlinks.gateway.session.DeviceSessionManager;
import org.jetlinks.gateway.vertx.mqtt.MqttServer;
import org.jetlinks.protocol.ProtocolSupports;
import org.jetlinks.registry.api.DeviceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@Component
public class MQTTServerVerticleSupplier implements VerticleSupplier {

    @Autowired
    private MqttServerOptions mqttServerOptions;

    @Autowired
    private DeviceRegistry deviceRegistry;

    @Autowired
    private ProtocolSupports protocolSupports;

    @Autowired
    private DeviceSessionManager deviceSessionManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Verticle get() {
        MqttServer mqttServer = new MqttServer();
        mqttServer.setMqttServerOptions(mqttServerOptions);
        mqttServer.setRegistry(deviceRegistry);
        mqttServer.setProtocolSupports(protocolSupports);
        mqttServer.setMessageConsumer(((deviceClient, message) -> {
            //转发消息到spring event
            eventPublisher.publishEvent(new GenericsPayloadApplicationEvent<>(
                    MQTTServerVerticleSupplier.this,
                    new DeviceMessageEvent<>(deviceClient, message),
                    message.getClass()));
        }));
        mqttServer.setDeviceSessionManager(deviceSessionManager);
        return mqttServer;
    }
}
