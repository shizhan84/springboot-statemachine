package cn.okcoming.fsm.handler;

import cn.okcoming.fsm.entity.Events;
import cn.okcoming.fsm.entity.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.monitor.AbstractStateMachineMonitor;
import org.springframework.statemachine.transition.Transition;

public class TestStateMachineMonitor extends AbstractStateMachineMonitor<States, Events> {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void transition(StateMachine<States, Events> stateMachine, Transition<States, Events> transition, long duration) {
        log.info("{} trans {} 耗时 {}",transition.getSource() ,transition.getTarget(), duration);
    }

    @Override
    public void action(StateMachine<States, Events> stateMachine, Action<States, Events> action, long duration) {
        log.info("{} action {} 耗时 {}" ,stateMachine.getState(),action, duration);
    }
}