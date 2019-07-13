package cn.okcoming.fsm.service;

import cn.okcoming.fsm.entity.Events;
import cn.okcoming.fsm.entity.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class StatemachineService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private StateMachinePersister<States, Events, Integer> stateMachinePersist;
    @Autowired
    private StateMachineFactory<States, Events> stateMachineFactory;

    public void execute(Integer businessId, Events event, Map<String, Object> context) {
        // 利用随记ID创建状态机，创建时没有与具体定义状态机绑定
        StateMachine<States, Events> stateMachine = stateMachineFactory.getStateMachine(UUID.randomUUID());
        //stateMachine.start();
       /* if(stateMachine.getState() == null){
            stateMachine.getState().
                .initial(States.UNPAID,init())
        }*/
        try {

            // 在BizStateMachinePersist的restore过程中，绑定turnstileStateMachine状态机相关事件监听
            stateMachinePersist.restore(stateMachine, businessId);
            // 本处写法较为繁琐，实际为注入Map<String, Object> context内容到message中
            MessageBuilder<Events> messageBuilder = MessageBuilder
                    .withPayload(event)
                    .setHeader("BusinessId", businessId);
            if (context != null) {
                context.entrySet().forEach(p -> messageBuilder.setHeader(p.getKey(), p.getValue()));
            }
            Message<Events> message = messageBuilder.build();

            // 发送事件，返回是否执行成功
            boolean success = stateMachine.sendEvent(message);
            if (success) {
                stateMachinePersist.persist(stateMachine, businessId);
            } else {
                log.info("状态机处理未执行成功，请处理 " + message + "，当前context：" + stateMachine.getState() );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //stateMachine.stop();
        }
    }
}