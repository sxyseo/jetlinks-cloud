package org.jetlinks.cloud.device.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.NotFoundException;
import org.jetlinks.cloud.DeviceConfigKey;
import org.jetlinks.cloud.device.gateway.vertx.DeviceMessageEvent;
import org.jetlinks.gateway.session.DeviceSession;
import org.jetlinks.protocol.message.DeviceMessage;
import org.jetlinks.protocol.message.DeviceMessageReply;
import org.jetlinks.protocol.message.event.EventMessage;
import org.jetlinks.protocol.message.function.FunctionInvokeMessageReply;
import org.jetlinks.protocol.metadata.FunctionMetadata;
import org.jetlinks.registry.api.DeviceMessageHandler;
import org.jetlinks.registry.api.DeviceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.jetlinks.cloud.DeviceConfigKey.*;

/**
 * @author bsetfeng
 * @version 1.0
 **/
@Component
@Slf4j
public class DeviceMessageReplyHandler {

    @Autowired
    private DeviceMessageHandler deviceMessageHandler;

    @EventListener
    public void handleReplyMessage(DeviceMessageEvent event) {
        DeviceMessage deviceMessage = event.getMessage();
        if (StringUtils.isEmpty(deviceMessage.getMessageId())) {
            log.warn("消息无messageId:{}", deviceMessage.toJson());
            return;
        }

        DeviceSession session = event.getSession();
        //设备回复的消息
        if (deviceMessage instanceof DeviceMessageReply) {
            if (deviceMessage instanceof FunctionInvokeMessageReply) {
                FunctionInvokeMessageReply invokeMessage = ((FunctionInvokeMessageReply) deviceMessage);

                // 设备配置了转发到指定的topic
                functionReplyTopic
                        .getConfigValue(session.getOperation(), invokeMessage.getFunctionId())
                        .asList(String.class)
                        .ifPresent(topics -> sendMessageToTopic(topics, deviceMessage));

                //判断是否为异步操作，如果不异步的，则需要回复
                boolean async = session.getOperation()
                        .getMetadata()
                        .getFunction(invokeMessage.getFunctionId())
                        .map(FunctionMetadata::isAsync)
                        .orElse(false);
                //同步操作则直接返回
                if (!async) {
                    deviceMessageHandler.reply(deviceMessage);
                }
            } else if (deviceMessage instanceof EventMessage) {
                EventMessage eventMessage = ((EventMessage) deviceMessage);
                eventTopic.getConfigValue(session.getOperation(), eventMessage.getEvent())
                        .asList(String.class)
                        .ifPresent(topics -> sendMessageToTopic(topics, deviceMessage));
            } else {
                //其他消息 直接回复
                // FIXME: 19-3-19 还有更好的操作？
                deviceMessageHandler.reply(deviceMessage);
            }
        }
    }

    private void sendMessageToTopic(List<String> topics, DeviceMessage message) {
        log.info("发送消息到topic:{}\n{}", message.toJson());

    }
}
