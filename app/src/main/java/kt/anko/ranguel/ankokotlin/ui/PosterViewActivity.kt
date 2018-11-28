package kt.anko.ranguel.ankokotlin.ui

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_poster_view.*
import kt.anko.ranguel.ankokotlin.R
import kt.anko.ranguel.ankokotlin.utils.showToast
import kt.anko.ranguel.ankokotlin.view.PosterView

class PosterViewActivity : AppCompatActivity() {

    private lateinit var customView: PosterView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_poster_view)

        supportActionBar!!.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        customView = findViewById(R.id.custom_poster)
        if (intent.getStringExtra(IMAGE_PATH) != null) {
            Log.i("SUKA", "SRC = ${intent.getStringExtra(IMAGE_PATH)}")
            val url = "https://www.anilibria.tv${intent.getStringExtra(IMAGE_PATH)}"
            Glide.with(this).load(url).addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.i("SUKA", "FAILED PIZDA ")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress_load_poster.visibility = View.GONE
                    customView.visibility = View.VISIBLE
                    customView.setImageDrawable(resource!!)
                    return true
                }

            }).submit()

        } else {
            showToast("Нет картикни :(")
        }

    }

    companion object {
        const val IMAGE_PATH = "IMAGE_PATH"
    }
}