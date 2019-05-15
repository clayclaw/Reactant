package net.swamphut.swampium.service.spec.net.http.server;

public interface HttpResponse {
    byte[] getBody();

    void setBody(byte[] body);

    int getStatusCode();

    void setStatusCode(int statusCode);
}
