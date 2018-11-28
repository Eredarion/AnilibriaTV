package kt.anko.ranguel.ankokotlin.network

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kt.anko.ranguel.ankokotlin.model.Release
import kt.anko.ranguel.ankokotlin.model.ReleaseDetail


class ReleaseRepository {

    fun getReleases(page: Int): Observable<Release> {
        return ApiFactory().getRetrofitInstance()
            .getReleases(page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getSearchReleases(query: String, page: Int): Flowable<Release> {
        return ApiFactory().getRetrofitInstance()
            .getReleasesSearch(query, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getDetailRelease(id: Int): Observable<ReleaseDetail> {
        return ApiFactory().getRetrofitInstance()
            .getReleaseDetail(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}