package net.swamphut.swampium.core.swobject.instance;

import net.swamphut.swampium.core.exception.ServiceInstantiateException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class SwampiumInstanceManager implements InstanceManager {
    private HashMap<Class<?>, Object> classInstanceMap = new HashMap<>();

    @Override
    public <T> T getInstance(Class<T> clazz) {
        if (!classInstanceMap.containsKey(clazz)) {
            classInstanceMap.put(clazz, instantiateService(clazz));
        }
        return (T) classInstanceMap.get(clazz);
    }

    @Override
    public void removeInstance(Class clazz) {
        if (!classInstanceMap.containsKey(clazz)) throw new IllegalStateException();
        classInstanceMap.remove(clazz);
    }

    private <T> T instantiateService(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new ServiceInstantiateException(
                    "SwObject classes are required to have a public default constructor", e, clazz);
        } catch (InvocationTargetException | InstantiationException e) {
            throw new ServiceInstantiateException("Exception throwed while instantiate", e, clazz);
        }
    }
}
