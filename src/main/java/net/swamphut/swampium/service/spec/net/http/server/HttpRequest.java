package net.swamphut.swampium.service.spec.net.http.server;

import java.util.Map;

public interface HttpRequest {
    byte[] getBody();

    void setBody(byte[] body);

    Map<String, String> getHeader();

    void setHeader(Map<String, String> header);
}
