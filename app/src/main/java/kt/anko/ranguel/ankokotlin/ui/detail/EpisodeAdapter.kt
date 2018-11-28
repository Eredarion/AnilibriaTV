package kt.anko.ranguel.ankokotlin.ui.detail

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kt.anko.ranguel.ankokotlin.R
import kt.anko.ranguel.ankokotlin.model.ReleaseDetail


class EpisodeAdapter internal constructor() : BaseQuickAdapter<ReleaseDetail.Uppod,
                                                               BaseViewHolder>(R.layout.item_episode) {

    override fun convert(helper: BaseViewHolder, item: ReleaseDetail.Uppod) {
        helper.setText(R.id.episode_number, item.comment!!.toUpperCase())
        helper.setVisible(R.id.isViewed, item.isViewed)
    }

}