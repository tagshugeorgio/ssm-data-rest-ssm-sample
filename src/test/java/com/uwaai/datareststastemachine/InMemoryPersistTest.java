package com.uwaai.datareststastemachine;

import com.uwaai.datareststastemachine.order.OrderEvent;
import com.uwaai.datareststastemachine.order.OrderState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("ut")
public class InMemoryPersistTest {

    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

    @Autowired
    private StateMachinePersister<OrderState, OrderEvent, UUID> persister;

    @Test
    public void persistWithInMemory() throws Exception {
        StateMachine<OrderState, OrderEvent> firstStateMachine = stateMachineFactory.getStateMachine();
        firstStateMachine.start();
        firstStateMachine.sendEvent(OrderEvent.PAY);

        StateMachine<OrderState, OrderEvent> secondStateMachine = stateMachineFactory.getStateMachine();
        secondStateMachine.start();
        secondStateMachine.sendEvent(OrderEvent.PAY);
        secondStateMachine.sendEvent(OrderEvent.SHIP);

        assertThat(secondStateMachine.getState().getId()).isEqualTo(OrderState.SHIPPED);

        persister.persist(firstStateMachine, firstStateMachine.getUuid());
        persister.restore(secondStateMachine, firstStateMachine.getUuid());

        assertThat(secondStateMachine.getState().getId()).isEqualTo(OrderState.PAID);
    }
}
