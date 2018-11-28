package kt.anko.ranguel.ankokotlin.ui.history

import com.arellomobile.mvp.MvpView
import kt.anko.ranguel.ankokotlin.database.history.HistoryData

interface HistoryView : MvpView {

    fun showReleases(releases: List<HistoryData>)

}