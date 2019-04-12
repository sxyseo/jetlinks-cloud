package org.jetlinks.cloud.device.gateway;

import lombok.Getter;
import lombok.Setter;
import org.jetlinks.cloud.DeviceConfigKey;
import org.jetlinks.protocol.device.DeviceInfo;
import org.jetlinks.protocol.device.DeviceProductInfo;
import org.jetlinks.protocol.device.DeviceProductOperation;
import org.jetlinks.registry.api.DeviceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

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
    @ConfigurationProperties(prefix = "test")
    public static class RegistryDevice implements CommandLineRunner {

        @Autowired
        DeviceRegistry registry;

        @Getter
        @Setter
        private long initDeviceNumber = 1000;

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

            productOperation.put("secureId", "test");
            productOperation.put("secureKey", "test");

            productOperation.put(DeviceConfigKey.functionReplyTopic.getValue(), "[\"device.function.reply\"]");

            //自动注册模拟设备
            for (int i = 0; i < initDeviceNumber; i++) {
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

}
