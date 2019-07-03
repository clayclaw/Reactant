package io.reactant.reactant.core.exception;

public class ComponentInstantiateException extends RuntimeException {
    private Class serviceClass;

    public ComponentInstantiateException(String reason, Class serviceClass) {
        super(String.format("Component %s instantiate failed, reason: %s", serviceClass.getName(), reason));
        this.serviceClass = serviceClass;
    }

    public ComponentInstantiateException(String reason, Throwable throwable, Class serviceClass) {
        super(String.format("Component %s instantiate failed, reason: %s", serviceClass.getName(), reason), throwable);
        this.serviceClass = serviceClass;
    }

    public Class getServiceClass() {
        return serviceClass;
    }
}
