package kt.anko.ranguel.ankokotlin.ui.favorite

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kt.anko.ranguel.ankokotlin.R
import kt.anko.ranguel.ankokotlin.database.favorite.FavoriteData
import kt.anko.ranguel.ankokotlin.utils.getRussianTitle
import kt.anko.ranguel.ankokotlin.utils.loadReleaseImage

class FavoriteAdapter internal constructor() : BaseQuickAdapter<FavoriteData, BaseViewHolder>(R.layout.item_release) {

    override fun convert(helper: BaseViewHolder, item: FavoriteData) {
        loadReleaseImage(helper.getView(R.id.item_image), item.image!!)
        helper.setText(R.id.item_title, getRussianTitle(item.title!!))
        helper.setVisible(R.id.episode_root, false)
    }

}