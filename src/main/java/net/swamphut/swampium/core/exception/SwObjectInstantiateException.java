package net.swamphut.swampium.core.exception;

public class SwObjectInstantiateException extends RuntimeException {
    private Class serviceClass;

    public SwObjectInstantiateException(String reason, Class serviceClass) {
        super(String.format("SwObject %s instantiate failed, reason: %s", serviceClass.getName(), reason));
        this.serviceClass = serviceClass;
    }

    public SwObjectInstantiateException(String reason, Throwable throwable, Class serviceClass) {
        super(String.format("SwObject %s instantiate failed, reason: %s", serviceClass.getName(), reason), throwable);
        this.serviceClass = serviceClass;
    }

    public Class getServiceClass() {
        return serviceClass;
    }
}
