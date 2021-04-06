package de.peacepunkt.tda2plugin.persistence;

import java.util.List;
import java.util.Optional;

public interface EntityDao<T> {
    //public T get(Object o);
    public List<T> getAll();
    public void update(T t);
    public  void delete(T t);
    public void add(T t);
}
