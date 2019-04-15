package org.jetlinks.cloud.device.gateway.web;

import lombok.SneakyThrows;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.protocol.message.DeviceMessageReply;
import org.jetlinks.protocol.message.function.FunctionInvokeMessageReply;
import org.jetlinks.protocol.message.function.FunctionParameter;
import org.jetlinks.protocol.message.property.ReadPropertyMessage;
import org.jetlinks.protocol.message.property.ReadPropertyMessageReply;
import org.jetlinks.registry.api.DeviceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@RestController
@RequestMapping("/device")
public class DeviceOperationController {

    @Autowired
    private DeviceRegistry registry;

    @GetMapping("/{deviceId}/metadata")
    public ResponseMessage<String> getDeviceMetadata(@PathVariable String deviceId) {
        return ResponseMessage.ok(registry.getDevice(deviceId).getMetadata().toJson().toJSONString());
    }

    @GetMapping("/{deviceId}/property/{name}")
    @SneakyThrows
    public ResponseMessage<DeviceMessageReply> sendReadProperty(@PathVariable String deviceId,
                                                                @PathVariable String name) {

        ReadPropertyMessageReply reply = registry.getDevice(deviceId)
                .messageSender()
                .readProperty(deviceId, name)
                .sendAsync()
                .toCompletableFuture()
                .get(10, TimeUnit.SECONDS);
        return ResponseMessage.ok(reply);
    }

    @PostMapping("/{deviceId}/invoke/{id}")
    @SneakyThrows
    public ResponseMessage<FunctionInvokeMessageReply> invokeFunction(@PathVariable String deviceId,
                                                                      @PathVariable String id,
                                                                      @RequestBody List<FunctionParameter> input) {
        FunctionInvokeMessageReply reply = registry
                .getDevice(deviceId)
                .messageSender()
                .invokeFunction(deviceId, id)
                .setParameter(input)
                .messageId(IDGenerator.MD5.generate())
                .sendAsync()
                .toCompletableFuture()
                .get(10, TimeUnit.SECONDS);
        return ResponseMessage.ok(reply);
    }
}
