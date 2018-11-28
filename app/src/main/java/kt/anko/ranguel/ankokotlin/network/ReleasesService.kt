package kt.anko.ranguel.ankokotlin.network

import io.reactivex.Flowable
import io.reactivex.Observable
import kt.anko.ranguel.ankokotlin.model.ReleaseDetail
import retrofit2.http.GET
import kt.anko.ranguel.ankokotlin.model.Release
import retrofit2.http.Query


interface ReleasesService {

    @GET("api/api.php?")
    fun getReleases(@Query("PAGEN_1") pageNumber: Int): Observable<Release>

    @GET("api/api.php?action=search&genre=")
    fun getReleasesSearch(@Query("name") name: String, @Query("PAGEN_1") pageNumber: Int): Flowable<Release>

    @GET("api/api_v2.php?action=release&")
    fun getReleaseDetail(@Query("ELEMENT_ID") id: Int): Observable<ReleaseDetail>

}