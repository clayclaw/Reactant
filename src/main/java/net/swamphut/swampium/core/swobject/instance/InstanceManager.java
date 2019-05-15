package net.swamphut.swampium.core.swobject.instance;

public interface InstanceManager {
    <T> T getInstance(Class<T> clazz);

    void removeInstance(Class clazz);
}
