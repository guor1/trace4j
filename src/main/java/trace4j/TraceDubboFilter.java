package trace4j;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Optional;

import static org.apache.dubbo.common.constants.CommonConstants.*;

/**
 * dubbo调用链过滤器，由接口消费者（consumer）将traceId数据向下传递给接口提供者（provider）
 *
 * @author guorui1
 */
@Activate(group = {PROVIDER, CONSUMER}, order = -100000)
public class TraceDubboFilter implements Filter {
    static final String TRACE_ID_KEY = "traceId";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String side = invoker.getUrl().getParameter(SIDE_KEY);

        if (side.equals(PROVIDER)) {
            String traceId = RpcContext.getServerAttachment().getAttachment(TRACE_ID_KEY);
            try {
                TraceContext.putTraceId(traceId);
                return invoker.invoke(invocation);
            } finally {
                TraceContext.clear();
            }
        } else {
            String traceId = TraceContext.getTraceId();
            Optional.ofNullable(traceId).ifPresent((s) -> RpcContext.getClientAttachment().setAttachment(TRACE_ID_KEY, s));
            return invoker.invoke(invocation);
        }
    }
}
