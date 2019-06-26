package io.reactant.reactant.core.exception;

public class ReactantObjectInstantiateException extends RuntimeException {
    private Class serviceClass;

    public ReactantObjectInstantiateException(String reason, Class serviceClass) {
        super(String.format("ReactantObject %s instantiate failed, reason: %s", serviceClass.getName(), reason));
        this.serviceClass = serviceClass;
    }

    public ReactantObjectInstantiateException(String reason, Throwable throwable, Class serviceClass) {
        super(String.format("ReactantObject %s instantiate failed, reason: %s", serviceClass.getName(), reason), throwable);
        this.serviceClass = serviceClass;
    }

    public Class getServiceClass() {
        return serviceClass;
    }
}
