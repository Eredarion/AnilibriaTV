package kt.anko.ranguel.ankokotlin.ui.history

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.chad.library.adapter.base.BaseQuickAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kt.anko.ranguel.ankokotlin.App
import kt.anko.ranguel.ankokotlin.R
import kt.anko.ranguel.ankokotlin.database.fromAction
import kt.anko.ranguel.ankokotlin.database.history.HistoryDao
import kt.anko.ranguel.ankokotlin.database.history.HistoryData
import kt.anko.ranguel.ankokotlin.ui.detail.DetailActivity
import kt.anko.ranguel.ankokotlin.utils.calculateNumColumns
import kt.anko.ranguel.ankokotlin.utils.showDialog


class HistoryFragment : MvpAppCompatFragment(),
                        BaseQuickAdapter.OnItemClickListener,
                        BaseQuickAdapter.OnItemLongClickListener,
                        HistoryView {

    private val mAdapter = HistoryAdapter()
    private lateinit var mRecyclerView: RecyclerView

    @InjectPresenter
    lateinit var presenter: HistoryPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_releases_list, container, false)

        mRecyclerView = view.findViewById(R.id.recyclerView)
        val column = calculateNumColumns(requireContext())
        mRecyclerView.layoutManager = GridLayoutManager(activity, column)
        mRecyclerView.adapter = mAdapter

        val mRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.refreshLayout)
        mRefreshLayout.isEnabled = false

        (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initAdapter()
        return view
    }

    override fun showReleases(releases: List<HistoryData>) {
        mAdapter.setNewData(releases)
    }


    private fun initAdapter() {
        mAdapter.apply {
            onAttachedToRecyclerView(mRecyclerView)
            setPreLoadNumber(4)
            onItemClickListener = this@HistoryFragment
            onItemLongClickListener = this@HistoryFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val data = mAdapter.getItem(position)
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA_HISTORY, data)
        requireActivity().startActivity(intent)
    }

    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int): Boolean {
        val item = mAdapter.getItem(position)
        requireContext().showDialog(resources.getString(R.string.delete))
            .content(resources.getString(R.string.remove_release_from_history))
            .positiveText(resources.getString(R.string.yes))
            .positiveColor(resources.getColor(R.color.anilibria))
            .onPositive { _, _ -> presenter.deleteHistoryRelease(item!!) }
            .negativeText(resources.getString(R.string.no))
            .onNegative { _, _ -> }
            .show()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.history_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> requireActivity().supportFragmentManager.popBackStack()
            R.id.delete_all_history -> requireContext().showDialog(resources.getString(R.string.carefully))
                .content(resources.getString(R.string.this_will_delete_the_whole_story))
                .positiveText(resources.getString(R.string.confirm_deletion))
                .onPositive { _, _ -> presenter.deleteAll()}
                .negativeText(resources.getString(R.string.cancel))
                .show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        super.onDestroy()
    }
}