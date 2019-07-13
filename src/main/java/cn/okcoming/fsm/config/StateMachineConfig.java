package cn.okcoming.fsm.config;

import cn.okcoming.fsm.entity.Events;
import cn.okcoming.fsm.entity.States;
import cn.okcoming.fsm.handler.TestStateMachineMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.monitor.StateMachineMonitor;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.EnumSet;
import java.util.HashMap;

@Configuration
//@EnableStateMachine
@EnableStateMachineFactory
class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states.withStates()
                .initial(States.UNPAID,init())
                .states(EnumSet.allOf(States.class));

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration().autoStartup(false);
        config.withMonitoring().monitor(stateMachineMonitor());
    }


    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                .withExternal()
                .source(States.UNPAID).target(States.WAITING_FOR_RECEIVE)
                .event(Events.PAY).action(pay())
                .and()
                .withExternal()
                .source(States.WAITING_FOR_RECEIVE).target(States.DONE)
                .event(Events.RECEIVE).action(recieve());
    }

    @Bean
    public Action<States, Events> init() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                log.info("订单创建，待支付");
            }
        };
    }

    @Bean
    public Action<States, Events> pay() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                log.info("用户完成支付，待收货");
            }
        };
    }

    @Bean
    public Action<States, Events> recieve() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                log.info("用户已收货，订单完成");
            }
        };
    }

/*    @Bean(name = "stateMachineTarget")
    @Scope(scopeName="prototype")
    public StateMachine<States, Events> stateMachineTarget() throws Exception {
        StateMachineBuilder.Builder<States, Events> builder = StateMachineBuilder.builder();

        builder.configureConfiguration().withConfiguration().autoStartup(true);
        builder.configureConfiguration().withMonitoring().monitor(stateMachineMonitor());

        builder.configureStates()
                .withStates()
                .initial(States.UNPAID)
                .states(EnumSet.allOf(States.class));

        builder.configureTransitions()
                .withExternal()
                .source(States.UNPAID).target(States.WAITING_FOR_RECEIVE)
                .event(Events.PAY)
                .and()
                .withExternal()
                .source(States.WAITING_FOR_RECEIVE).target(States.DONE)
                .event(Events.RECEIVE);

        return builder.build();
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ProxyFactoryBean stateMachine() {
        ProxyFactoryBean pfb = new ProxyFactoryBean();
        pfb.setTargetSource(poolTargetSource());
        return pfb;
    }
    @Bean
    public CommonsPool2TargetSource poolTargetSource() {
        CommonsPool2TargetSource pool = new CommonsPool2TargetSource();
        pool.setMaxSize(10);
        pool.setTargetBeanName("stateMachineTarget");
        return pool;
    }*/

    static class InMemoryStateMachinePersist implements StateMachinePersist<States, Events, Integer> {

        private final HashMap<Integer, StateMachineContext<States, Events>> contexts = new HashMap<>();

        @Override
        public void write(StateMachineContext<States, Events> context, Integer contextObj) throws Exception {
            contexts.put(contextObj, context);
        }

        @Override
        public StateMachineContext<States, Events> read(Integer contextObj) throws Exception {
            return contexts.get(contextObj);
        }
    }


    @Bean
    public StateMachinePersister<States, Events, Integer> persister() {
        InMemoryStateMachinePersist stateMachinePersist = new InMemoryStateMachinePersist();
        StateMachinePersister<States, Events, Integer> persister = new DefaultStateMachinePersister<>(stateMachinePersist);
        return persister;
    }

    @Bean
    public StateMachineMonitor<States, Events> stateMachineMonitor() {
        return new TestStateMachineMonitor();
    }
}