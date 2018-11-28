package kt.anko.ranguel.ankokotlin.utils

import com.bumptech.glide.Glide
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition


fun loadReleasePoster(imageView: ImageView, posterPath: String) {
    val url = "https://www.anilibria.tv$posterPath"
    Glide.with(imageView.context)
        .load(url)
        .into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                imageView.setImageDrawable(resource)
            }

        })
}

fun loadReleaseImage(imageView: ImageView, posterPath: String) {
    val url = "https://www.anilibria.tv$posterPath"
    Glide.with(imageView.context).load(url).into(imageView)
}