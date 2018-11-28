package kt.anko.ranguel.ankokotlin.ui.search

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.chad.library.adapter.base.BaseQuickAdapter
import kt.anko.ranguel.ankokotlin.R
import kt.anko.ranguel.ankokotlin.model.Release
import kt.anko.ranguel.ankokotlin.ui.detail.DetailActivity
import kt.anko.ranguel.ankokotlin.ui.list.ReleasesAdapter
import kt.anko.ranguel.ankokotlin.ui.list.presenter.ReleasesView
import kt.anko.ranguel.ankokotlin.utils.CustomLoadMoreView
import kt.anko.ranguel.ankokotlin.utils.calculateNumColumns
import kt.anko.ranguel.ankokotlin.utils.showToast
import java.util.*


class SearchFragment : MvpAppCompatFragment(),
                       BaseQuickAdapter.OnItemClickListener,
                       BaseQuickAdapter.RequestLoadMoreListener,
                       ReleasesView {

    @InjectPresenter
    lateinit var presenter: SearchPresenter

    private val mAdapter = ReleasesAdapter()
    private var isError = false
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRefreshView: SwipeRefreshLayout
    private lateinit var mSearchView: SearchView
    private lateinit var emptyView: View
    private var state: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_releases_list, container, false)

        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.apply {
            layoutManager = GridLayoutManager(context, calculateNumColumns(requireContext()))
            adapter = mAdapter
        }

        mRefreshView = view.findViewById(R.id.refreshLayout)
        mRefreshView.apply {
            setOnRefreshListener {
                if (presenter.query != "") {
                    presenter.refreshReleases(presenter.query)
                } else {
                    mRefreshView.isRefreshing = false
                }}
            setColorSchemeColors(resources.getColor(R.color.anilibria))
        }

        emptyView = inflater.inflate(R.layout.no_item_view, container, false)


        (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        initAdapter()
        state = savedInstanceState == null
        return view
    }

    private fun initAdapter() {
        mAdapter.apply {
            setLoadMoreView(CustomLoadMoreView())
            setOnLoadMoreListener(this@SearchFragment, mRecyclerView)
            onAttachedToRecyclerView(mRecyclerView)
            openLoadAnimation(BaseQuickAdapter.ALPHAIN)
            setPreLoadNumber(4)
            onItemClickListener = this@SearchFragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        mSearchView = searchItem.actionView as SearchView
        mSearchView.queryHint = "Search releases..."
        if (state) {
            mSearchView.isIconified = false
        }
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                mAdapter.setNewData(Collections.emptyList())
                presenter.refreshReleases(s)
                mSearchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(s: String): Boolean { return false }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().supportFragmentManager.popBackStack()
                mSearchView.clearFocus()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val item = mAdapter.getItem(position)
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA, item)
        requireActivity().startActivity(intent)
    }

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
        Log.i("SUKA", "showReleases: releases data size = ${mAdapter.data.size}")
        mAdapter.loadMoreComplete()
    }

    override fun insertMore(releases: List<Release.Item>) {
        isError = false
        mAdapter.addData(releases)
        Log.i("SUKA", "insertMore: releases data size = ${mAdapter.data.size}")
        mAdapter.loadMoreComplete()
    }

    override fun showError() {
        isError = true
        requireContext().showToast("Not internet connection")
        mAdapter.loadMoreFail()
    }

    override fun noMore() {
        mAdapter.loadMoreEnd()
    }

    override fun setRefreshing(state: Boolean) {
        mRefreshView.isRefreshing = state
    }

    override fun onDestroy() {
        (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        super.onDestroy()
    }

}