package kt.anko.ranguel.ankokotlin.ui.search

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kt.anko.ranguel.ankokotlin.network.ReleaseRepository
import kt.anko.ranguel.ankokotlin.ui.list.presenter.ReleasesView


@InjectViewState
class SearchPresenter : MvpPresenter<ReleasesView>() {
    private val releaseRepository = ReleaseRepository()
    var query = ""
    private val startPage = 1
    private var currentPage = startPage

    private val isFirstPage: Boolean
        get() = currentPage == startPage

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG, "onFirstViewAttach: create")
    }

    private fun searchReleases(pageNumber: Int) {
        currentPage = pageNumber
        if (isFirstPage) {
            viewState.setRefreshing(true)
        }
        releaseRepository.getSearchReleases(query, pageNumber)
            .doAfterTerminate { viewState.setRefreshing(false) }
            .subscribe({ releases ->
                                if (currentPage <= releases.navigation!!.totalPages!!) {
                                    if (isFirstPage) viewState.showReleases(releases.items!!)
                                    else viewState.insertMore(releases.items!!)
                                    Log.i("SUKA", "searchPresenter vse good")
                                } else {
                                    viewState.noMore()
                                    Log.i("SUKA", "searchPresenter vse ploho")
                                }},
                       { throwable ->
                                viewState.showError()
                                throwable.printStackTrace()
                                Log.i("SUKA", "error " + throwable.localizedMessage)
                       }).addToDisposable()
    }

    fun refreshReleases(query: String) {
        this.query = query
        searchReleases(startPage)
    }

    fun reload() {
        searchReleases(currentPage)
    }

    fun loadMore() {
        searchReleases(currentPage + 1)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private var compositeDisposable = CompositeDisposable()

    fun Disposable.addToDisposable() {
        compositeDisposable.add(this)
    }

    companion object {
        private val TAG = "ReleasesPresenter"
    }

}