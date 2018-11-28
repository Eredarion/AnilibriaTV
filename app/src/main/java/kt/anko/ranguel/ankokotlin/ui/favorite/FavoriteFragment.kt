package kt.anko.ranguel.ankokotlin.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.chad.library.adapter.base.BaseQuickAdapter
import kt.anko.ranguel.ankokotlin.R
import kt.anko.ranguel.ankokotlin.database.favorite.FavoriteData
import kt.anko.ranguel.ankokotlin.ui.detail.DetailActivity
import kt.anko.ranguel.ankokotlin.utils.calculateNumColumns
import kt.anko.ranguel.ankokotlin.utils.showDialog

class FavoriteFragment : MvpAppCompatFragment(),
                         BaseQuickAdapter.OnItemClickListener,
                         BaseQuickAdapter.OnItemLongClickListener,
                         FavoriteView {

    private val mAdapter = FavoriteAdapter()
    private lateinit var mRecyclerView: RecyclerView

    @InjectPresenter
    lateinit var presenter: FavoritePresenter

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

    private fun initAdapter() {
        mAdapter.apply {
            onAttachedToRecyclerView(mRecyclerView)
            setPreLoadNumber(4)
            onItemClickListener = this@FavoriteFragment
            onItemLongClickListener = this@FavoriteFragment
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> requireActivity().supportFragmentManager.popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val item = mAdapter.getItem(position)
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA_FAVORITE, item)
        requireActivity().startActivity(intent)
    }

    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int): Boolean {
        val item = mAdapter.getItem(position)
        requireContext().showDialog("Удалить ?")
            .content("Удалить тайтл из избранного ?")
            .positiveText("Да")
            .positiveColor(resources.getColor(R.color.anilibria))
            .onPositive { _, _ -> presenter.deleteFavoriteRelease(item!!) }
            .negativeText("Нет")
            .onNegative { _, _ -> }
            .show()
        return true
    }


    override fun showReleases(releases: List<FavoriteData>) {
        mAdapter.setNewData(releases)
    }

    override fun onDestroy() {
        (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        super.onDestroy()
    }

}