package guru.qa.niffler.service;

import guru.qa.niffler.api.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class RestClient {
    protected static final Config CFG = Config.getInstance();
    private final OkHttpClient okHttpClient;
    protected final Retrofit retrofit;
    public RestClient(String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);
    }
    public RestClient(String baseUrl, boolean followRedirect) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);
    }
    public RestClient(String baseUrl, Converter.Factory factory) {
        this(baseUrl, false, factory, HttpLoggingInterceptor.Level.BODY);
    }
    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirect);

        if (ArrayUtils.isNotEmpty(interceptors)){
            for (Interceptor interceptor : interceptors){
                builder.addNetworkInterceptor(interceptor);
            }
        }

        builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level));
        builder.cookieJar(
                new JavaNetCookieJar(
                        new CookieManager(
                                ThreadSafeCookieStore.INSTANCE,
                                CookiePolicy.ACCEPT_ALL
                        )
                )
        );
        this.okHttpClient = builder.build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(factory)
                .build();
    }
    public Retrofit retrofit() {
         return this.retrofit;
    }
    public <T> T create(final Class<T> service) {
        return this.retrofit.create(service);
    }
    public static final class EmtyRestClient extends RestClient {

        public EmtyRestClient(String baseUrl) {
            super(baseUrl);
        }

        public EmtyRestClient(String baseUrl, boolean followRedirect) {
            super(baseUrl, followRedirect);
        }

        public EmtyRestClient(String baseUrl, Converter.Factory factory) {
            super(baseUrl, factory);
        }

        public EmtyRestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
            super(baseUrl, followRedirect, factory, level, interceptors);
        }
    }
    public <T> T execute(Call<T> executeMethod, int expectedCode) {
        final Response<T> response;
        try {
            response = executeMethod.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        assertEquals(expectedCode, response.code());
        return response.body();
    }

    public <T> T execute(Call<T> executeMethod) {
        final Response<T> response;
        try {
            response = executeMethod.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        return response.body();
    }
}
