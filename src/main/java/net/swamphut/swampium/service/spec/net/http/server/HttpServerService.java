package net.swamphut.swampium.service.spec.net.http.server;


import io.reactivex.Observable;

public interface HttpServerService {
    Observable<HttpResponse> get(String urlPattern);

    Observable<HttpResponse> put(String urlPattern);

    Observable<HttpResponse> post(String urlPattern);

    Observable<HttpResponse> delete(String urlPattern);
}
