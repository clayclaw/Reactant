package net.swamphut.swampium.service.spec.net.http.client;

import io.reactivex.Single;

import java.util.Map;

public interface HttpClientService {
    Single<String> get(String url);

    Single<String> get(String url, Map<String, String> headers);


    Single<String> post(String url, HttpRequestBody body, Map<String, String> headers);

    Single<String> post(String url, HttpRequestBody body);

    Single<String> post(String url);


    Single<String> delete(String url, HttpRequestBody body, Map<String, String> headers);

    Single<String> delete(String url, HttpRequestBody body);

    Single<String> delete(String url);


    Single<String> put(String url, HttpRequestBody body, Map<String, String> headers);

    Single<String> put(String url, HttpRequestBody body);

    Single<String> put(String url);

    HttpRequestBody makeBody(String type, byte[] body);

}
