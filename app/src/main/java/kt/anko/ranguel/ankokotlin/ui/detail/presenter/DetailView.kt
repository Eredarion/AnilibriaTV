package kt.anko.ranguel.ankokotlin.ui.detail.presenter

import kt.anko.ranguel.ankokotlin.model.Release
import kt.anko.ranguel.ankokotlin.model.ReleaseDetail
import com.arellomobile.mvp.MvpView


interface DetailView : MvpView {

    fun showRelease(release: ReleaseDetail)

    fun showPreRelease(release: Release.Item)

    fun loadTorrent(url: String)

    fun shareRelease(text: String)

    fun copyLink(url: String)

    fun playEpisodes(release: Release)

    fun showErrorDialog()

    fun hideEpisodeDialog()

}
