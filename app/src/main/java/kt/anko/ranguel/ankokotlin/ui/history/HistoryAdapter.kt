package kt.anko.ranguel.ankokotlin.ui.history

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kt.anko.ranguel.ankokotlin.R
import kt.anko.ranguel.ankokotlin.database.history.HistoryData
import kt.anko.ranguel.ankokotlin.utils.getRussianTitle
import kt.anko.ranguel.ankokotlin.utils.loadReleaseImage


class HistoryAdapter internal constructor() : BaseQuickAdapter<HistoryData, BaseViewHolder>(R.layout.item_release) {

    override fun convert(helper: BaseViewHolder, item: HistoryData) {
        loadReleaseImage(helper.getView(R.id.item_image), item.image!!)
        helper.setText(R.id.item_title, getRussianTitle(item.title!!))
        helper.setVisible(R.id.episode_root, false)
    }

}