package simsys.entity.queue;

import simsys.entity.Entity;
import simsys.entity.demand.Demand;

import java.util.Collection;

public interface Queue extends Entity {
    int size();

    void add(Demand demand);

    void addAll(Collection<Demand> demands);

    Demand peek();

    Demand poll();

    default boolean isEmpty() {
        return size() == 0;
    }
}
