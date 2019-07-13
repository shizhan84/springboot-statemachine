package cn.okcoming.fsm;

import cn.okcoming.fsm.entity.Events;
import cn.okcoming.fsm.entity.States;
import cn.okcoming.fsm.service.StatemachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application implements CommandLineRunner {
 
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
 
    //@Autowired
    private StateMachine<States, Events> stateMachine;

    @Autowired
    private StatemachineService statemachineService;

    @Override
    public void run(String... args) throws Exception {

        Map<String, Object> context = new HashMap<>();
        context.put("key1","value1");
        statemachineService.execute(1,Events.PAY,context);
        statemachineService.execute(1,Events.RECEIVE,null);

        //stateMachine.sendEvent(Events.PAY);
        //stateMachine.sendEvent(Events.RECEIVE);

    }
 
}