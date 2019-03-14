package org.jetlinks.cloud.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.ModuleUtils;
import org.hswebframework.web.ThreadLocalUtils;
import org.hswebframework.web.id.IDGenerator;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;
import java.util.StringJoiner;

/**
 * @author zhouhao
 * @since 1.0.0
 */
public class LoggingAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static ApplicationEventPublisher publisher;

    static String appId = "default";

    static String appName = "default";

    @Setter
    @Getter
    private String commitId = "unknown";

    @Setter
    @Getter
    private String mavenModule = "unknown";

    static void setPublisher(ApplicationEventPublisher publisher) {
        LoggingAppender.publisher = publisher;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (publisher == null) {
            return;
        }
        StackTraceElement element = event.getCallerData()[0];
        IThrowableProxy proxies = event.getThrowableProxy();
        String message = event.getFormattedMessage();
        String stack = null;
        if (null != proxies) {
            int commonFrames = proxies.getCommonFrames();
            StackTraceElementProxy[] stepArray = proxies.getStackTraceElementProxyArray();
            StringJoiner joiner = new StringJoiner("\n", message + "\n[", "]");
            StringBuilder stringBuilder = new StringBuilder();
            ThrowableProxyUtil.subjoinFirstLine(stringBuilder, proxies);
            joiner.add(stringBuilder);
            for (int i = 0; i < stepArray.length - commonFrames; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(CoreConstants.TAB);
                ThrowableProxyUtil.subjoinSTEP(sb, stepArray[i]);
                joiner.add(sb);
            }
            stack = joiner.toString();
        }
        try {
            ModuleUtils.ModuleInfo moduleInfo = ModuleUtils.getModuleByClass(Class.forName(element.getClassName()));

        } catch (Exception e) {

        }
        Map<String, String> context = MDC.getCopyOfContextMap();
        String requestId = ThreadLocalUtils.get("request-id");

    }
}
