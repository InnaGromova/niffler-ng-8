package guru.qa.niffler.test.web.gql;

import com.apollographql.adapter.core.DateAdapter;
import guru.qa.niffler.jupiter.annotation.GqlTest;
import guru.qa.type.Date;
import com.apollographql.java.client.ApolloClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.extension.RegisterExtension;

@GqlTest
public class BaseGraphQlTest {
    protected static final Config CFG = Config.getInstance();

    @RegisterExtension
    protected static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.restApiLoginExtension();
    protected static final ApolloClient apolloClient = new ApolloClient.Builder()
            .serverUrl(CFG.gatewayUrl() + "graphql")
            .addCustomScalarAdapter(Date.type, DateAdapter.INSTANCE)
            .okHttpClient(
                    new OkHttpClient.Builder()
                            .addNetworkInterceptor(new AllureOkHttp3())
                            .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                            .build()
            ).build();
}
