package org.jetlinks.cloud.device.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.jetlinks.cloud.device.gateway.vertx.DeviceMessageEvent;
import org.jetlinks.protocol.message.DeviceMessage;
import org.jetlinks.protocol.message.DeviceMessageReply;
import org.jetlinks.registry.api.DeviceMessageHandler;
import org.jetlinks.registry.api.DeviceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author bsetfeng
 * @version 1.0
 * @Date 2019/3/14 9:38 PM
 **/
@Component
@Slf4j
public class DeviceMessageReplyHandler {

    @Autowired
    private DeviceRegistry deviceRegistry;

    @Autowired
    private DeviceMessageHandler deviceMessageHandler;

    @EventListener
    public void handleReplyMessage(DeviceMessageEvent event) {
        DeviceMessage deviceMessage = event.getMessage();
        if (StringUtils.isEmpty(deviceMessage.getMessageId())) {
            log.warn("消息无messageId:{}", deviceMessage.toJson());
            return;
        }
        if (deviceMessage instanceof DeviceMessageReply) {
            deviceMessageHandler.reply(deviceMessage);
        }
    }
}
