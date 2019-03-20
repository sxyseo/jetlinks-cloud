package org.jetlinks.cloud.device.gateway.web;

import lombok.SneakyThrows;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.protocol.message.DeviceMessageReply;
import org.jetlinks.protocol.message.function.FunctionInvokeMessage;
import org.jetlinks.protocol.message.function.FunctionInvokeMessageReply;
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

        ReadPropertyMessage message = new ReadPropertyMessage();
        message.setMessageId(IDGenerator.MD5.generate());
        message.setPropertyIds(Arrays.asList(name));
        message.setDeviceId(deviceId);
        ReadPropertyMessageReply reply = registry.getDevice(deviceId).messageSender()
                .send(message, ReadPropertyMessageReply::new)
                .toCompletableFuture()
                .get(10, TimeUnit.SECONDS);
        return ResponseMessage.ok(reply);
    }

    @PostMapping("/{deviceId}/invoke/{id}")
    @SneakyThrows
    public ResponseMessage<FunctionInvokeMessageReply> invokeFunction(@PathVariable String deviceId,
                                                                      @PathVariable String id,
                                                                      @RequestBody List<Object> input) {
        FunctionInvokeMessage message = new FunctionInvokeMessage();
        message.setMessageId(IDGenerator.MD5.generate());
        message.setFunctionId(id);
        message.setInputs(input);
        message.setDeviceId(deviceId);
        FunctionInvokeMessageReply reply = registry.getDevice(deviceId).messageSender()
                .send(message, FunctionInvokeMessageReply::new)
                .toCompletableFuture()
                .get(10, TimeUnit.SECONDS);
        return ResponseMessage.ok(reply);
    }
}
