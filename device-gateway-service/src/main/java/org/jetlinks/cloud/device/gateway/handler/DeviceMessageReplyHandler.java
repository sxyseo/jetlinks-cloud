package org.jetlinks.cloud.device.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.jetlinks.cloud.device.gateway.vertx.DeviceMessageEvent;
import org.jetlinks.protocol.message.DeviceMessage;
import org.jetlinks.protocol.message.DeviceMessageReply;
import org.jetlinks.registry.api.DeviceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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

    @EventListener
    public void handleReplyMessage(DeviceMessageEvent event){
        DeviceMessage deviceMessage= event.getMessage();
        if(deviceMessage instanceof DeviceMessageReply){
            deviceRegistry.getMessageHandler().reply(deviceMessage);
        }
    }
}
