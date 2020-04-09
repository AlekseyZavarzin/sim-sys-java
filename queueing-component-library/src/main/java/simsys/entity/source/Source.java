package simsys.entity.source;

import simsys.entity.demand.Demand;


// источник это компонент имитационной модели  = события + ресурсы
// для источника должны быть указаны выходы (очереди или еще что-то)
public interface Source<T extends Demand> {
    T generateDemand();
}
