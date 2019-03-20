package org.jetlinks.cloud;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hswebframework.web.dict.EnumDict;
import org.jetlinks.protocol.metadata.ValueWrapper;
import org.jetlinks.registry.api.DeviceOperation;

/**
 * 设备配置常量
 *
 * @author zhouhao
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DeviceConfigKey implements EnumDict<String> {
    deviceConnectTopic("device:topic:connect", "设备连接"),
    deviceDisconnectTopic("device:topic:disconnect", "设备断开连接"),
    eventTopic("device:topic:event", "设备事件上报"),
    functionReplyTopic("device:topic:async:function:reply", "功能调用回复"),
    ;

    private String value;
    private String text;

    public ValueWrapper getConfigValue(DeviceOperation operation, String suffix) {
        return operation.get(getValue().concat(suffix == null ? "" : suffix))
                .notPresent(() -> getConfigValue(operation));
    }

    public ValueWrapper getConfigValue(DeviceOperation operation) {
        return operation.get(getValue());
    }
}
