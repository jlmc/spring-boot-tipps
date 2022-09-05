package io.github.jlmc.springbootkafkaexamplerx.application.outboundservices;

import io.github.jlmc.springbootkafkaexamplerx.domain.model.aggregates.OrderId;
import io.github.jlmc.springbootkafkaexamplerx.domain.model.commands.OrderBookingCommand;
import io.github.jlmc.springbootkafkaexamplerx.sharewddomain.OrderBookedEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

/**
 * @See https://www.anycodings.com/1questions/4697826/spring-webfluxmonoflux-with-aop-triggering-rest-call-at-interception-and-working-with-monoflux
 */
@Aspect
@Component
public class OrderReportedAspect {

    private final OrderEventPublisherService orderEventPublisherService;

    public OrderReportedAspect(OrderEventPublisherService orderEventPublisherService) {
        this.orderEventPublisherService = orderEventPublisherService;
    }

    @Around("@annotation(OrderReported)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OrderReported orderReported = method.getAnnotation(OrderReported.class);

        Object[] args = joinPoint.getArgs();
        Object proceed = joinPoint.proceed();

        if (args != null && args.length > 0 && args[0] instanceof OrderBookingCommand command && proceed instanceof Mono<?> orderIdMono) {

            return orderIdMono.doOnNext((Object it) -> {
                        OrderId orderId = (OrderId) it;
                        OrderBookedEvent event =
                                OrderBookedEvent.of(
                                        orderId.getId(),
                                        command.address(),
                                        command.item(),
                                        orderReported.system());
                        orderEventPublisherService.send(event);
                    }
            );

        } else {
            return proceed;
        }
    }
}
