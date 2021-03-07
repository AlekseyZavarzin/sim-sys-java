package simsys.core.agent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.util.ReflectionUtils;
import simsys.core.annotation.Action;
import simsys.core.annotation.State;
import simsys.core.annotation.Statistic;
import simsys.core.context.SimulationContext;
import simsys.core.event.HandledEvent;
import simsys.core.event.handler.StatisticHandler;


// Wrapper-adapter for agent as event
public class AgentEventImpl implements AgentEvent {

  protected Agent agent;
  protected SimulationContext context;

  List<HandledEvent> events;

  public AgentEventImpl(Agent agent, SimulationContext context) {
    this.agent = agent;
    this.context = context;
    setAgent(agent);
  }

  @Override
  public Agent getAgent() {
    return this.agent;
  }

  @Override
  public void setAgent(Agent agent) {
    // create handler for all states
    StatisticHandler statisticHandler = new StatisticHandler(agent, this.context);

    Set<String> states = new HashSet<>();

    String initialState = null;

    Field[] fields = agent.getClass().getDeclaredFields();
    for (Field field : fields) {
      Annotation[] annotations = field.getDeclaredAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation.annotationType().equals(State.class)) {
          field.setAccessible(true);
          String stateName = (String) ReflectionUtils.getField(field, agent);
          states.add(stateName);
          boolean isInitial = ((State) annotation).initial();
          if (isInitial) {
            initialState = stateName;
          }
        }
      }
    }

    // create map State->Method
    Map<String, Method> methodResolver = new HashMap<>();
    Map<String, HandledEvent> eventResolver = new HashMap<>();

    Method[] methods = agent.getClass().getDeclaredMethods();

    Field nextActivationTimeField = ReflectionUtils
        .findField(agent.getClass(), "nextActivationTime");
    nextActivationTimeField.setAccessible(true);

    for (Method method : methods) {
      Annotation[] annotations = method.getDeclaredAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation.annotationType().equals(Action.class)) {
          String[] statesFromAnnotation = ((Action) annotation).states();
          for (String state : statesFromAnnotation) {
            if (!states.contains(state)) {
              throw new IllegalStateException("There is no a state with name " + state);
            }
            methodResolver.put(state, method);
            eventResolver.put(state, new HandledEvent().addHandler(statisticHandler));
          }
        }
      }
    }

    events = new ArrayList<>();

    Field nextStateField = ReflectionUtils.findField(agent.getClass(), "nextState");
    nextStateField.setAccessible(true);
    Field currentStateFiled = ReflectionUtils.findField(agent.getClass(), "currentState");
    currentStateFiled.setAccessible(true);
    // we create event per method
    for (Map.Entry<String, Method> response : methodResolver.entrySet()) {
      HandledEvent event = eventResolver.get(response.getKey());
      Method method = response.getValue();
      event.addHandler(e -> {
        // first off all invoke  agent method
        method.setAccessible(true);
        ReflectionUtils.invokeMethod(method, agent);
        // after this agent was pre-updated
        Object nextState = ReflectionUtils.getField(nextStateField, agent);
        ReflectionUtils.setField(currentStateFiled, agent, nextState);

        // it means the next state is defined = makes sense
        // we need create the next event
        if (nextState != null) {
          HandledEvent nextEvent = eventResolver.get(nextState);
          double nextActivationTime = (double) ReflectionUtils
              .getField(nextActivationTimeField, agent);
          nextEvent.setActivateTime(nextActivationTime);
          context.getEventProvider().add(nextEvent);

        }
      });

    }

    // need schedule the first event
    if (initialState == null) {
      //dummy way
      throw new IllegalStateException("There is no initial state");
    }
    context.getEventProvider().add(eventResolver.get(initialState));
    // set initial value from annotation
    // also we can consider a case with init method or initialization within constructor
    ReflectionUtils.setField(currentStateFiled, agent, initialState);

    // Print all
//    System.out.println("Print states and corresponding actions");
//    for (Map.Entry<String, HandledEvent> pair : eventResolver.entrySet()) {
//      System.out.println(pair.getKey() + ": " + pair.getValue());
//    }
//    System.out.println("*******************************************");

  }

  @Override
  public double getActivateTime() {
    return 0;
  }

  @Override
  public void setActivateTime(double activateTime) {

  }

  @Override
  public void activate() {

  }

}
