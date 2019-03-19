package org.jetlinks.cloud.device.gateway;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import org.jetlinks.protocol.ProtocolSupport;
import org.jetlinks.protocol.message.CommonDeviceMessageReply;
import org.jetlinks.protocol.message.DeviceMessage;
import org.jetlinks.protocol.message.codec.*;
import org.jetlinks.protocol.message.property.ReadPropertyMessageReply;
import org.jetlinks.protocol.metadata.DeviceMetadataCodec;
import org.jetlinks.registry.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@SpringCloudApplication
@ComponentScan("org.jetlinks.cloud")
@EnableCaching
public class DeviceGatewayServiceApplication {
    public static void main(String[] args) {

        SpringApplication.run(DeviceGatewayServiceApplication.class, args);
    }

    @Component
    public static class RegistryDevice implements CommandLineRunner {

        @Autowired
        DeviceRegistry registry;

        @Value("${init-size:10000}")
        private long size = 10000;

        @Override
        public void run(String... strings) {
            //自动注册模拟设备
            for (int i = 0; i < size; i++) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setId("test" + i);
                deviceInfo.setProtocol("mock");
                deviceInfo.setName("test");
                registry.registry(deviceInfo);
            }
        }
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return (request, deviceOperation) -> AuthenticationResponse.success();
    }

    @Bean
    public ProtocolSupport protocolSupport() {
        return new ProtocolSupport() {
            @Override
            public String getId() {
                return "mock";
            }

            @Override
            public String getName() {
                return "模拟协议";
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public DeviceMessageCodec getMessageCodec() {
                return new DeviceMessageCodec() {
                    @Override
                    public EncodedMessage encode(Transport transport, MessageEncodeContext context) {
                        DeviceMessage message = context.getMessage();
                        if (transport == Transport.MQTT) {
                            return EncodedMessage.mqtt(message.getDeviceId(),
                                    "execute",
                                    Unpooled.copiedBuffer(message.toJson().toString().getBytes()));
                        }
                        throw new UnsupportedOperationException("不支持的传输协议:" + transport);
                    }

                    @Override
                    public DeviceMessage decode(Transport transport, MessageDecodeContext context) {
                        EncodedMessage encodedMessage = context.getMessage();
                        String json = encodedMessage.getByteBuf().toString(StandardCharsets.UTF_8);
                        JSONObject jsonObject = JSONObject.parseObject(json);
                        if (encodedMessage instanceof MqttMessage) {
                            MqttMessage mqttMessage = ((MqttMessage) encodedMessage);
                            if ("reply".equals(mqttMessage.getTopic())) {
                                if ("readProperty".equals(jsonObject.getString("operation"))) {
                                    return jsonObject.toJavaObject(ReadPropertyMessageReply.class);
                                }
                            }
                        }
                        return jsonObject.toJavaObject(CommonDeviceMessageReply.class);
                    }
                };
            }

            @Override
            public DeviceMetadataCodec getMetadataCodec() {
                throw new UnsupportedOperationException("不支持元数据转码");
            }
        };
    }
}
