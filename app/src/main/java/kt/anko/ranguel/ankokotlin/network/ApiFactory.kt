package kt.anko.ranguel.ankokotlin.network

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import java.util.concurrent.TimeUnit

class ApiFactory {

    private val interceptor: HttpLoggingInterceptor
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
            return interceptor
        }

    private val httpClient: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(3000, TimeUnit.MILLISECONDS)
            .connectTimeout(3000, TimeUnit.MILLISECONDS)
            .build()

    private val retrofitInstance: Retrofit
        get() = Retrofit.Builder()
            .baseUrl("https://www.anilibria.tv")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()

    fun getRetrofitInstance(): ReleasesService {
       return retrofitInstance.create(ReleasesService::class.java)
    }
}
