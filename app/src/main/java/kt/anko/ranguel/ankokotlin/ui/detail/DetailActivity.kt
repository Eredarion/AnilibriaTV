package kt.anko.ranguel.ankokotlin.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.chad.library.adapter.base.BaseQuickAdapter
import kt.anko.ranguel.ankokotlin.App
import kt.anko.ranguel.ankokotlin.R
import kt.anko.ranguel.ankokotlin.database.favorite.FavoriteData
import kt.anko.ranguel.ankokotlin.database.fromAction
import kt.anko.ranguel.ankokotlin.database.history.HistoryData
import kt.anko.ranguel.ankokotlin.model.Release
import kt.anko.ranguel.ankokotlin.model.ReleaseDetail
import kt.anko.ranguel.ankokotlin.ui.PosterViewActivity
import kt.anko.ranguel.ankokotlin.ui.detail.presenter.DetailPresenter
import kt.anko.ranguel.ankokotlin.ui.detail.presenter.DetailView
import kt.anko.ranguel.ankokotlin.utils.*
import java.lang.StringBuilder
import java.util.*


class DetailActivity : MvpAppCompatActivity(), DetailView, BaseQuickAdapter.OnItemClickListener {

    @InjectPresenter
    lateinit var presenter: DetailPresenter
    private val mAdapter = EpisodeAdapter()
    private val mFavoriteDao = App.database.getFavoriteDao()

    // View
    private lateinit var titleRus: TextView
    private lateinit var titleEng: TextView
    private lateinit var genres: TextView
    private lateinit var voices: TextView
    private lateinit var season: TextView
    private lateinit var type: TextView
    private lateinit var episodeCount: TextView
    private lateinit var description: TextView
    private lateinit var poster: ImageView
    private lateinit var backgroundPoster: ImageView
    private lateinit var favoriteButton: ImageButton
    private lateinit var loadingView: View
    private lateinit var loadFinishView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
        mAdapter.onItemClickListener = this

        titleRus = findViewById(R.id.title_rus)
        titleEng = findViewById(R.id.title_eng)
        genres = findViewById(R.id.genres)
        voices = findViewById(R.id.voices)
        season = findViewById(R.id.season)
        type = findViewById(R.id.type)
        episodeCount = findViewById(R.id.episode_count)
        description = findViewById(R.id.description)
        // TODO
        poster = findViewById(R.id.poster)

        backgroundPoster = findViewById(R.id.background_poster)
        val playButton: Button = findViewById(R.id.play)
        playButton.setOnClickListener { showEpisodesDialog() }

        favoriteButton = findViewById(R.id.addToFavorite)
        favoriteButton.setOnClickListener { safeRelease() }
        loadFinishView = findViewById(R.id.load_finish)
        loadingView = findViewById(R.id.loading_release_detail)

        if (intent != null) {
            when {
                intent.getSerializableExtra(EXTRA_DATA_HISTORY) != null -> {
                    val data = intent.getSerializableExtra(EXTRA_DATA_HISTORY) as HistoryData
                    presenter.convertHistoryDataToRelease(data)
                }
                intent.getSerializableExtra(EXTRA_DATA_FAVORITE) != null -> {
                    val favoriteData = intent.getSerializableExtra(EXTRA_DATA_FAVORITE) as FavoriteData
                    presenter.convertFavoriteDataToRelease(favoriteData)
                }
                else -> presenter.release = intent.getSerializableExtra(EXTRA_DATA) as Release.Item
            }
        }
    }

    private fun safeRelease() {
       fromAction {
           if (mFavoriteDao.getById(presenter.id.toLong()) == null) {
               mFavoriteDao.insert(presenter.prepareFavoriteRelease(presenter.releaseDetail!!))
               runOnUiThread {
                   favoriteButton.setImageResource(R.drawable.ic_favorite)
                   showToast(resources.getString(R.string.add_to_favorite))
               }
           } else {
               mFavoriteDao.delete(presenter.prepareFavoriteRelease(presenter.releaseDetail!!))
               runOnUiThread {
                   favoriteButton.setImageResource(R.drawable.ic_favorite_disable)
                   showToast(resources.getString(R.string.removed_from_favorites))
               }
           }
       }.subscribe()
    }

    private fun showEpisodesDialog() {
        showDialog(resources.getString(R.string.choose_a_series))
            .adapter(mAdapter, LinearLayoutManager(this))
            .show()
    }

    override fun showRelease(release: ReleaseDetail) {
        mAdapter.setNewData(release.uppod)
        loadingView.visibility = View.INVISIBLE
        loadFinishView.visibility = View.VISIBLE
        loadReleasePoster(poster, release.image!!)
        loadReleasePoster(backgroundPoster, release.image!!)
        titleRus.text = getRussianTitle(release.title!!)
        titleEng.text = getEnglishTitle(release.title!!)
        description.text = fromHtml(release.description!!)
        episodeCount.text = StringBuilder(resources.getString(R.string.voiced) + release.torrentList!![0].episode)
        genres.text = printDetails(R.string.genres, release.genres!!)
        voices.text = printDetails(R.string.voices, release.voices!!)
        season.text = printDetails(R.string.season, release.season!!)
        type.text = printDetails(R.string.type, release.types!!)
        presenter.loadWatchedEpisodes(release.uppod!!)

        poster.setOnClickListener {
            val intent = Intent(this, PosterViewActivity::class.java)
            intent.putExtra(PosterViewActivity.IMAGE_PATH, release.image!!)
            startActivity(intent)
        }
    }

    private fun printDetails(resourceString: Int, details: List<String>) =
        StringBuilder(resources.getString(resourceString) + fromHtml(TextUtils.join(", ", details)))

    override fun showPreRelease(release: Release.Item) {
        loadReleasePoster(poster, release.image!!)
        loadReleasePoster(backgroundPoster, release.image!!)
        fromAction {
            if (mFavoriteDao.getById(release.id.toLong()) != null)
                runOnUiThread { favoriteButton.setImageResource(R.drawable.ic_favorite) }
        }.subscribe()
        titleRus.text = getRussianTitle(release.title!!)
        titleEng.text = getEnglishTitle(release.title!!)
        description.text = fromHtml(release.description!!)
        episodeCount.text = StringBuilder(resources.getString(R.string.voiced) + release.episode)
        genres.text = printDetails(R.string.genres, release.genres!!)
        voices.text = printDetails(R.string.voices, release.voices!!)
        season.text = printDetails(R.string.season, release.season!!)
        type.text = printDetails(R.string.type, release.types!!)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val data = mAdapter.getItem(position)!!
        data.isViewed = true
        mAdapter.getItem(position)!!.isViewed = true
        mAdapter.notifyDataSetChanged()
        presenter.putEpisodes(mAdapter.data)
        showDialog(resources.getString(R.string.quality))
            .items(Arrays.asList("SD", "HD"))
            .itemsCallback { _, _, position1, _ ->
                when (position1) {
                    0 -> playEpisode(data.file)
                    1 -> playEpisode(data.filehd)
                    else -> showToast(resources.getString(R.string.no_links))
                }
            }
            .show()
    }

    private fun playEpisode(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.setDataAndType(Uri.parse(url), "video/*")
        startActivity(intent)
    }

    override fun loadTorrent(url: String) {

    }

    override fun shareRelease(text: String) {

    }

    override fun copyLink(url: String) {

    }

    override fun playEpisodes(release: Release) {

    }

    override fun showErrorDialog() {
        showDialog(resources.getString(R.string.error))
            .content(resources.getString(R.string.an_error_has_occurred))
            .positiveText(resources.getString(R.string.reload))
            .positiveColorRes(R.color.anilibria)
            .onPositive { _, _ -> presenter.reloadReleaseDetail() }
            .negativeText(resources.getString(R.string.exit))
            .negativeColorRes(R.color.anilibria)
            .onNegative { _, _ -> finish() }
            .cancelListener { _ -> finish() }
            .show()
    }

    override fun hideEpisodeDialog() {

    }

    companion object {
        const val EXTRA_DATA = "extraData"
        const val EXTRA_DATA_HISTORY = "extraDataHistory"
        const val EXTRA_DATA_FAVORITE = "extraDataFavorite"
    }

}