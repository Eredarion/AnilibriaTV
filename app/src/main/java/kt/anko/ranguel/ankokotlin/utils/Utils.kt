package kt.anko.ranguel.ankokotlin.utils

import android.content.Context
import kt.anko.ranguel.ankokotlin.R
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import android.widget.Toast
import android.text.Html




private fun getDeviceWidth(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return Math.round(displayMetrics.widthPixels / displayMetrics.density)
}

fun calculateNumColumns(context: Context): Int {
    val deviceWidth = getDeviceWidth(context)
    val columnWidth = context.resources.getInteger(R.integer.grid_column_width).toFloat()
    return Math.round(deviceWidth / columnWidth)
}

fun Context.showDialog(title: String): MaterialDialog.Builder {
    return MaterialDialog.Builder(this)
        .title(title)
        .titleColorRes(R.color.background)
        .titleGravity(GravityEnum.CENTER)
        .backgroundColorRes(R.color.background_main)
        .contentColorRes(R.color.background)
        .contentGravity(GravityEnum.CENTER)
        .itemsColorRes(R.color.background)
        .itemsGravity(GravityEnum.CENTER)
}


fun getRussianTitle(title: String): String {
    val titles = title.split(" / ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    return fromHtml(titles[1])
}

fun getEnglishTitle(title: String): String {
    val titles = title.split(" / ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    return fromHtml(titles[0])
}

fun fromHtml(string: String): String {
    return Html.fromHtml(string).toString()
}

fun Context.showToast(text: String) {
    Toast.makeText(this, fromHtml(text), Toast.LENGTH_SHORT).show()
}