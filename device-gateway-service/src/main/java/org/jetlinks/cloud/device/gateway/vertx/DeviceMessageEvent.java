package org.jetlinks.cloud.device.gateway.vertx;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetlinks.gateway.session.DeviceSession;
import org.jetlinks.protocol.message.DeviceMessage;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public class DeviceMessageEvent<M extends DeviceMessage> {
    private DeviceSession session;

    private M message;
}
