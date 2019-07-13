package cn.okcoming.fsm.handler;

import cn.okcoming.fsm.entity.Events;
import cn.okcoming.fsm.entity.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.*;

import java.io.IOException;
import java.util.Map;

//@WithStateMachine
public class EventAction {
 
    private Logger log = LoggerFactory.getLogger(getClass());



    @OnTransition(target = "UNPAID")
    public void create(@EventHeaders Map<String, Object> headers,
                       ExtendedState extendedState,
                       StateMachine<States, Events> stateMachine,
                       Message  message ,StateContext<States, Events>  stateContext)  {
        log.info("订单创建，待支付22");

    }
 
    @OnTransition(source = "UNPAID", target = "WAITING_FOR_RECEIVE")
    public void pay(StateMachine<States, Events> stateMachine,
                    Message  message ,StateContext<States, Events>  stateContext) throws IOException {
        log.info("用户完成支付，待收货");
        Integer.parseInt("abc");
        throw new IOException(" connection reset by peer");
    }
 
    @OnTransition(source = "WAITING_FOR_RECEIVE", target = "DONE")
    public void receive() {
        log.info("用户已收货，订单完成");
    }


    @OnStateMachineError
    public void error(StateContext<States, Events>  stateContext,Exception e) {
        log.error("---error-",e);
    }
}