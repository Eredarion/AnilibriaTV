package kt.anko.ranguel.ankokotlin.ui.list.presenter

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import kt.anko.ranguel.ankokotlin.model.Release

@StateStrategyType(SkipStrategy::class)
interface ReleasesView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun showReleases(releases: List<Release.Item>)

    @StateStrategyType(SkipStrategy::class)
    fun insertMore(releases: List<Release.Item>)

    @StateStrategyType(SkipStrategy::class)
    fun showError()

    @StateStrategyType(SkipStrategy::class)
    fun noMore()

    @StateStrategyType(SkipStrategy::class)
    fun setRefreshing(state: Boolean)

}