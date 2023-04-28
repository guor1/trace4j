package trace4j;

import org.apache.dubbo.common.utils.StringUtils;
import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;

/**
 * 上下文
 *
 * @author guorui1
 */
public class TraceContext {
    static final String TRACE_ID_KEY = "traceId";
    static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    public static void clear() {
        TRACE_ID.remove();
    }

    /**
     * 获取上下文里面的traceId
     */
    public static String getTraceId() {
        return TRACE_ID.get();
    }

    /**
     * traceId放入ThreadLocal中，并写入MDC，如果traceId为空，则默认创建一个
     */
    public static void putTraceId(String traceId) {
        putTraceId(traceId, true);
    }

    public static void putTraceId(String traceId, boolean createIfNull) {
        Optional.of(traceId).map(item -> StringUtils.isEmpty(item) && createIfNull ? generateId() : item).ifPresent((s) -> {
            TRACE_ID.set(s);
            MDC.put(TRACE_ID_KEY, s);
        });
    }

    public static String generateId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }
}
