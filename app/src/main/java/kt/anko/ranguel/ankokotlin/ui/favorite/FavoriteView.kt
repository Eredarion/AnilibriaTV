package kt.anko.ranguel.ankokotlin.ui.favorite

import com.arellomobile.mvp.MvpView
import kt.anko.ranguel.ankokotlin.database.favorite.FavoriteData

interface FavoriteView : MvpView {

    fun showReleases(releases: List<FavoriteData>)

}