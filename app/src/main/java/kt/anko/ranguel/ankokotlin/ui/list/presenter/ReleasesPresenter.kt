package kt.anko.ranguel.ankokotlin.ui.list.presenter

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.Disposable
import kt.anko.ranguel.ankokotlin.network.ReleaseRepository


@InjectViewState
class ReleasesPresenter : MvpPresenter<ReleasesView>() {

    private val releaseRepository = ReleaseRepository()
    private var mSubscriptionReleases: Disposable? = null
    private val startPage = 1
    private var currentPage = startPage

    private val isFirstPage: Boolean
        get() = currentPage == startPage

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG, "onFirstViewAttach: create")
        refreshReleases()
    }

    private fun loadReleases(pageNumber: Int) {
        Log.d(TAG, "loadReleases $pageNumber")
        currentPage = pageNumber
        if (isFirstPage) viewState.setRefreshing(true)
        mSubscriptionReleases = releaseRepository.getReleases(pageNumber)
            .doAfterTerminate { viewState.setRefreshing(false) }
            .subscribe({releases ->
                             if (isFirstPage) viewState.showReleases(releases.items!!)
                             else viewState.insertMore(releases.items!!) },
                       {t -> viewState.showError()
                             t.printStackTrace()})
    }

    fun refreshReleases() {
        loadReleases(startPage)
    }

    fun reload() {
        loadReleases(currentPage)
    }

    fun loadMore() {
        loadReleases(currentPage + 1)
    }

    override fun onDestroy() {
        if (mSubscriptionReleases != null) {
            mSubscriptionReleases!!.dispose()
        }
        super.onDestroy()
    }

    companion object {
        private val TAG = "ReleasesPresenter"
    }

}