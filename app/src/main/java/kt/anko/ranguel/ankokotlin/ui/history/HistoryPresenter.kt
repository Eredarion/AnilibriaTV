package kt.anko.ranguel.ankokotlin.ui.history

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kt.anko.ranguel.ankokotlin.App
import kt.anko.ranguel.ankokotlin.database.fromAction
import kt.anko.ranguel.ankokotlin.database.history.HistoryData

@InjectViewState
class HistoryPresenter : MvpPresenter<HistoryView>() {

    private var subscribe: Disposable? = null
    private val historyDao = App.database.getHistoryDao()
    private lateinit var list: List<HistoryData>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        refreshData()
    }

    private fun refreshData() {
        subscribe = historyDao.getAll
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data -> viewState.showReleases(data); list = data}
    }

    fun deleteHistoryRelease(item: HistoryData) {
        fromAction {
            historyDao.delete(item)
        }.subscribe { refreshData() }
         .dispose()
    }

    fun deleteAll() {
        fromAction {
            historyDao.deleteAll(list)
        }.subscribe {refreshData()}
         .dispose()
}

    override fun onDestroy() {
        subscribe!!.dispose()
        super.onDestroy()
    }

}