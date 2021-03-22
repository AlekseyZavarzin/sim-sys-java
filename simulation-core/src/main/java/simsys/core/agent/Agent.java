package simsys.core.agent;

import java.util.Set;

public interface Agent {

  String currentState();

  double getActivationTime();

  Set<String> getAllStates();

  void sleep();

  void sleep(double delay);

  void moveToState(String state);

  void moveToStateAfterTimeout(String state, double delay);

}
