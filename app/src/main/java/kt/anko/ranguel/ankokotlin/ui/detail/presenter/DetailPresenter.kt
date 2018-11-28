package kt.anko.ranguel.ankokotlin.ui.detail.presenter


import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.Disposable
import kt.anko.ranguel.ankokotlin.App
import kt.anko.ranguel.ankokotlin.database.episode.EpisodeData
import kt.anko.ranguel.ankokotlin.database.favorite.FavoriteData
import kt.anko.ranguel.ankokotlin.database.fromAction
import kt.anko.ranguel.ankokotlin.database.history.HistoryData
import kt.anko.ranguel.ankokotlin.model.Release
import kt.anko.ranguel.ankokotlin.model.ReleaseDetail
import kt.anko.ranguel.ankokotlin.network.ReleaseRepository
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


@InjectViewState
class DetailPresenter : MvpPresenter<DetailView>() {

    private val releaseRepository = ReleaseRepository()
    private var mSubscriptionReleases: Disposable? = null
    var release: Release.Item? = null
    var releaseDetail: ReleaseDetail? = null
    var id = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.i("SUKA", "onFirstViewAttach")
        when {
            release != null -> {
                id = release!!.id
                viewState.showPreRelease(release!!)
                loadReleaseDetail(release!!.id)
            }
            id != 0 -> loadReleaseDetail(id)
            else -> Log.d("SUKA", "Null pointer :(")
        }
    }

    private fun loadReleaseDetail(id: Int) {
        Log.i("SUKA", "loadReleaseDetail")
        mSubscriptionReleases = releaseRepository.getDetailRelease(id)
            .subscribe({ releaseDetail ->
                viewState.showRelease(releaseDetail)
                safeRelease(releaseDetail)
                this.releaseDetail = releaseDetail
                this.id = releaseDetail.id!!
            },
                { viewState.showErrorDialog() })
    }

    fun reloadReleaseDetail() {
        Log.i("SUKA", "RELOAD RELEASE + $id")
        loadReleaseDetail(id)
    }

    private fun safeRelease(release: ReleaseDetail) {
        fromAction {
            val data = prepareHistoryDataToSave(release)
            val dao = App.database.getHistoryDao()
            if (dao.getById(release.id!!.toLong()) == null) {
                dao.insert(data)
            } else {
                dao.update(data)
            }}.subscribe()
    }

    fun putEpisodes(data: MutableList<ReleaseDetail.Uppod>) {
        val jsonEpisodes = JSONArray()
        for (i in 0 until data.size) {
            val dataUppod = data[i]
            val one = JSONObject()
            try {
                one.put("episodeCount", i)
                one.put("isViewed", dataUppod.isViewed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            jsonEpisodes.put(one)
        }
        Log.i("SUKA", jsonEpisodes.toString())
        fromAction {
            val dao = App.database.getEpisodeDao()
            val episodeData = EpisodeData(id, jsonEpisodes.toString())
            if (dao.getById(id) == null) {
                dao.insert(episodeData)
            } else {
                dao.update(episodeData)
            }
        }.subscribe()
    }

    fun loadWatchedEpisodes(episodes: List<ReleaseDetail.Uppod>) {
        fromAction {
            val data = App.database.getEpisodeDao().getById(id)
            if (data != null) {
                val jsonArray = JSONArray(data.data)
                (0 until jsonArray.length()).forEach { position ->
                    jsonArray.getJSONObject(position).let {
                        episodes[position].isViewed = it.getBoolean("isViewed")
                        Log.i("SUKA WTF???", "position = $position view? = ${it.getBoolean("isViewed")}")
                    }
                }
            }
        }.subscribe()
    }

    fun convertHistoryDataToRelease(historyData: HistoryData) {
        id = historyData.id
        val item = Release.Item(
            historyData.torrentUpdate,
            historyData.id!!.toInt(),
            historyData.code,
            historyData.title,
            historyData.torrentLink,
            historyData.link,
            historyData.image,
            historyData.episode,
            historyData.description,
            historyData.season!!.split(", "),
            historyData.voices!!.split(", "),
            historyData.genres!!.split(", "),
            historyData.types!!.split(", ")
        )
        viewState.showPreRelease(item)
    }

    fun convertFavoriteDataToRelease(favoriteData: FavoriteData) {
        id = favoriteData.id
        val item = Release.Item(
            favoriteData.torrentUpdate,
            favoriteData.id!!.toInt(),
            favoriteData.code,
            favoriteData.title,
            favoriteData.torrentLink,
            favoriteData.link,
            favoriteData.image,
            favoriteData.episode,
            favoriteData.description,
            favoriteData.season!!.split(", "),
            favoriteData.voices!!.split(", "),
            favoriteData.genres!!.split(", "),
            favoriteData.types!!.split(", ")
        )
        viewState.showPreRelease(item)
    }

    private fun prepareHistoryDataToSave(release: ReleaseDetail): HistoryData {
        return HistoryData(
            release.torrentUpdate,
            release.id!!,
            release.code,
            release.title,
            release.torrentLink,
            release.link,
            release.image,
            release.torrentList!![0].episode,
            release.description,
            release.season!!.joinToString(", "),
            release.voices!!.joinToString(", "),
            release.genres!!.joinToString(", "),
            release.types!!.joinToString(", "),
            Calendar.getInstance().time.time
        )
    }

    fun prepareFavoriteRelease(release: ReleaseDetail): FavoriteData {
        return FavoriteData(
            release.torrentUpdate,
            release.id!!,
            release.code,
            release.title,
            release.torrentLink,
            release.link,
            release.image,
            release.torrentList!![0].episode,
            release.description,
            release.season!!.joinToString(", "),
            release.voices!!.joinToString(", "),
            release.genres!!.joinToString(", "),
            release.types!!.joinToString(", "),
            Calendar.getInstance().time.time
        )
    }

    override fun onDestroy() {
        if (mSubscriptionReleases != null) {
            mSubscriptionReleases!!.dispose()
        }
        super.onDestroy()
    }
}