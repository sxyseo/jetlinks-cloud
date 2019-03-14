package org.jetlinks.cloud.device.gateway.vertx;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetlinks.gateway.session.DeviceClient;
import org.jetlinks.protocol.message.DeviceMessage;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public class DeviceMessageEvent<M extends DeviceMessage> {
    private DeviceClient deviceClient;

    private M message;
}
