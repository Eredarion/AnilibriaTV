package kt.anko.ranguel.ankokotlin.ui.list

import kt.anko.ranguel.ankokotlin.model.Release
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.BaseQuickAdapter
import kt.anko.ranguel.ankokotlin.R
import kt.anko.ranguel.ankokotlin.utils.getRussianTitle
import kt.anko.ranguel.ankokotlin.utils.loadReleaseImage


class ReleasesAdapter : BaseQuickAdapter<Release.Item, BaseViewHolder>(R.layout.item_release) {

    override fun convert(helper: BaseViewHolder, item: Release.Item) {
        loadReleaseImage(helper.getView(R.id.item_image), item.image!!)
        helper.setText(R.id.item_title, getRussianTitle(item.title!!))
        helper.setText(R.id.item_episode_count, item.episode)
    }

}