package org.jetlinks.cloud.device.gateway;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import org.jetlinks.cloud.DeviceConfigKey;
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
            DeviceProductInfo productInfo = new DeviceProductInfo();
            productInfo.setProtocol("jet-links");
            productInfo.setName("测试型号");
            productInfo.setId("test");
            DeviceProductOperation productOperation = registry.getProduct(productInfo.getId());
            productOperation.update(productInfo);
            productOperation.updateMetadata("{\n" +
                    "  \"id\": \"test-device\",\n" +
                    "  \"name\": \"测试设备\",\n" +
                    "  \"properties\": [\n" +
                    "    {\n" +
                    "      \"id\": \"name\",\n" +
                    "      \"name\": \"名称\",\n" +
                    "      \"valueType\": {\n" +
                    "        \"type\": \"string\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\": \"model\",\n" +
                    "      \"name\": \"型号\",\n" +
                    "      \"valueType\": {\n" +
                    "        \"type\": \"string\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"functions\": [\n" +
                    "    {\n" +
                    "      \"id\": \"playVoice\",\n" +
                    "      \"name\": \"播放声音\",\n" +
                    "      \"inputs\": [\n" +
                    "        {\n" +
                    "          \"id\": \"content\",\n" +
                    "          \"name\": \"内容\",\n" +
                    "          \"valueType\": {\n" +
                    "            \"type\": \"string\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"id\": \"times\",\n" +
                    "          \"name\": \"播放次数\",\n" +
                    "          \"valueType\": {\n" +
                    "            \"type\": \"int\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\": \"setColor\",\n" +
                    "      \"name\": \"灯光颜色\",\n" +
                    "      \"inputs\": [\n" +
                    "        {\n" +
                    "          \"id\": \"colorRgb\",\n" +
                    "          \"name\": \"颜色RGB值\",\n" +
                    "          \"valueType\": {\n" +
                    "            \"type\": \"string\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"events\": [\n" +
                    "    {\n" +
                    "      \"id\": \"temperature\",\n" +
                    "      \"name\": \"温度\",\n" +
                    "      \"parameters\": [\n" +
                    "        {\n" +
                    "          \"id\": \"temperature\",\n" +
                    "          \"valueType\": {\n" +
                    "            \"type\": \"int\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
            productOperation.put(DeviceConfigKey.eventTopic.getValue(), "[\"device.events\"]");
            productOperation.put(DeviceConfigKey.deviceConnectTopic.getValue(), "[\"device.connect\"]");
            productOperation.put(DeviceConfigKey.deviceDisconnectTopic.getValue(), "[\"device.disconnect\"]");
            productOperation.put(DeviceConfigKey.childDeviceConnectTopic.getValue(), "[\"device.child.connect\"]");
            productOperation.put(DeviceConfigKey.childDeviceDisconnectTopic.getValue(), "[\"device.child.disconnect\"]");
            productOperation.put(DeviceConfigKey.functionReplyTopic.getValue(), "[\"device.function.reply\"]");

            //自动注册模拟设备
            for (int i = 0; i < size; i++) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setId("test" + i);
                deviceInfo.setProtocol("jet-links");
                deviceInfo.setName("test");
                deviceInfo.setProductId(productInfo.getId());
                registry.registry(deviceInfo);
            }
            //注册20个子设备绑定到test0
            for (int i = 0; i < 20; i++) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setId("child" + i);
                deviceInfo.setProtocol("jet-links");
                deviceInfo.setName("test-child");
                deviceInfo.setProductId(productInfo.getId());
                deviceInfo.setParentDeviceId("test0");
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
