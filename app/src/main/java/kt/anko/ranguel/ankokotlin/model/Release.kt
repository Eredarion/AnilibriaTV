package kt.anko.ranguel.ankokotlin.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable


class Release : Serializable {

    var items: List<Item>? = null
    var navigation: Navigation? = null


    class Item(
        var torrentUpdate: Int?,
        var id: Int = 0,
        var code: String?,
        var title: String?,
        var torrentLink: String?,
        var link: String?,
        var image: String?,
        var episode: String?,
        var description: String?,
        var season: List<String>?,
        var voices: List<String>?,
        var genres: List<String>?,
        var types: List<String>?
    ) : Serializable


    inner class Navigation {

        @SerializedName("total")
        @Expose
        var total: Int? = null
        @SerializedName("total_pages")
        @Expose
        var totalPages: Int? = null
        @SerializedName("page")
        @Expose
        var page: String? = null

    }

}