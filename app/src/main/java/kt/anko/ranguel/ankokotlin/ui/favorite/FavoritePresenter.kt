package kt.anko.ranguel.ankokotlin.ui.favorite

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kt.anko.ranguel.ankokotlin.App
import kt.anko.ranguel.ankokotlin.database.favorite.FavoriteData
import kt.anko.ranguel.ankokotlin.database.fromAction

@InjectViewState
class FavoritePresenter : MvpPresenter<FavoriteView>() {

    private var subscribe: Disposable? = null
    private val favoriteDao = App.database.getFavoriteDao()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadFavoriteReleases()
    }

    fun loadFavoriteReleases() {
        subscribe = favoriteDao.getAll
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data -> viewState.showReleases(data) }
    }

    fun deleteFavoriteRelease(item: FavoriteData) {
        fromAction {
            favoriteDao.delete(item)
        }.subscribe { loadFavoriteReleases() }
         .dispose()
    }

    override fun onDestroy() {
        subscribe!!.dispose()
        super.onDestroy()
    }

}