package org.jetlinks.cloud.device.gateway.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetlinks.gateway.session.DeviceSession;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@AllArgsConstructor
@Getter
public class ChildDeviceOfflineEvent {

    private String deviceId;

    private String childDeviceId;

    private long timestamp;
}
