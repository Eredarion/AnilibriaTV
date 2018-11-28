package kt.anko.ranguel.ankokotlin.ui.list

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.chad.library.adapter.base.BaseQuickAdapter
import kt.anko.ranguel.ankokotlin.App
import kt.anko.ranguel.ankokotlin.R
import kt.anko.ranguel.ankokotlin.database.fromAction
import kt.anko.ranguel.ankokotlin.model.Release
import kt.anko.ranguel.ankokotlin.ui.detail.DetailActivity
import kt.anko.ranguel.ankokotlin.ui.favorite.FavoriteFragment
import kt.anko.ranguel.ankokotlin.ui.history.HistoryFragment
import kt.anko.ranguel.ankokotlin.ui.list.presenter.ReleasesPresenter
import kt.anko.ranguel.ankokotlin.ui.list.presenter.ReleasesView
import kt.anko.ranguel.ankokotlin.ui.search.SearchFragment
import kt.anko.ranguel.ankokotlin.utils.*
import java.util.*


class ReleasesFragment : MvpAppCompatFragment(),
                         BaseQuickAdapter.OnItemClickListener,
                         BaseQuickAdapter.RequestLoadMoreListener,
                         BaseQuickAdapter.OnItemLongClickListener,
                         ReleasesView {

    @InjectPresenter
    lateinit var presenter: ReleasesPresenter

    private val mAdapter = ReleasesAdapter()
    private var isError = false
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRefreshView: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_releases_list,container, false)

        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.apply {
            layoutManager = GridLayoutManager(context, calculateNumColumns(requireContext()))
            adapter = mAdapter
        }

        mRefreshView = view.findViewById(R.id.refreshLayout)
        mRefreshView.apply {
            setOnRefreshListener { presenter.refreshReleases() }
            setColorSchemeColors(resources.getColor(R.color.anilibria))
        }

        initAdapter()
        return view
    }

    private fun initAdapter() {
        mAdapter.apply {
            setLoadMoreView(CustomLoadMoreView())
            setOnLoadMoreListener(this@ReleasesFragment, mRecyclerView)
            onAttachedToRecyclerView(mRecyclerView)
            openLoadAnimation(BaseQuickAdapter.ALPHAIN)
            setPreLoadNumber(4)
            onItemClickListener = this@ReleasesFragment
            onItemLongClickListener = this@ReleasesFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.releases_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.search -> pushFragment(SearchFragment())
            R.id.history -> pushFragment(HistoryFragment())
            R.id.favorite -> pushFragment(FavoriteFragment())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pushFragment(fragment: MvpAppCompatFragment) {
        requireFragmentManager()
            .beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val item = mAdapter.getItem(position)!!
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA, item)
        requireActivity().startActivity(intent)
    }

    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int): Boolean {
        requireContext().showDialog("Действие:")
            .items(Arrays.asList("Скачать", "Добавить в избранное"))
            .itemsCallback { _, _, position1, _ ->
                when (position1) {
                    0 -> requireContext().showToast("скачал проверяй")
                    1 -> requireContext().showToast("шо ?")
                    else -> requireContext().showToast("???")
                }
            }
            .show()
        return true
    }

    private fun listToString(list: List<String>) : String = list.joinToString(", ")

    override fun onLoadMoreRequested() {
        if (isError) {
            presenter.reload()
        } else {
            presenter.loadMore()
        }
    }

    override fun showReleases(releases: List<Release.Item>) {
        isError = false
        mAdapter.setNewData(releases)
        mAdapter.loadMoreComplete()
    }

    override fun insertMore(releases: List<Release.Item>) {
        isError = false
        mAdapter.addData(releases)
        mAdapter.loadMoreComplete()
    }

    override fun showError() {
        isError = true
        requireContext().showToast("Not internet connection")
        mAdapter.loadMoreFail()
    }

    override fun noMore() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setRefreshing(state: Boolean) {
        mRefreshView.isRefreshing = state
    }

}