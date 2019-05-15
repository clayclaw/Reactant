package net.swamphut.swampium.core.exception;

public class ServiceInstantiateException extends RuntimeException {
    private Class serviceClass;

    public ServiceInstantiateException(String reason, Class serviceClass) {
        super(String.format("Service %s instantiate failed, reason: %s", serviceClass.getName(), reason));
        this.serviceClass = serviceClass;
    }

    public ServiceInstantiateException(String reason, Throwable throwable, Class serviceClass) {
        super(String.format("Service %s instantiate failed, reason: %s", serviceClass.getName(), reason), throwable);
        this.serviceClass = serviceClass;
    }

    public Class getServiceClass() {
        return serviceClass;
    }
}
