package ro.mpp2024;

import ro.mpp2024.Entity;

public interface IRepository<ID, E extends Entity<ID>> {
    E findOne(ID id);
    Iterable<E> findAll();
    E save(E entity);
    E delete(ID id);
    E update(E entity);
}
