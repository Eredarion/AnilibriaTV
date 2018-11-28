package kt.anko.ranguel.ankokotlin.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ReleaseDetail {

    @SerializedName("sessId")
    @Expose
    var sessId: String? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("code")
    @Expose
    var code: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("release_status")
    @Expose
    var releaseStatus: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("link")
    @Expose
    var link: String? = null
    @SerializedName("isBlocked")
    @Expose
    var isBlocked: Boolean? = null
    @SerializedName("fav")
    @Expose
    var fav: Fav? = null
    @SerializedName("season")
    @Expose
    var season: List<String>? = null
    @SerializedName("genres")
    @Expose
    var genres: List<String>? = null
    @SerializedName("voices")
    @Expose
    var voices: List<String>? = null
    @SerializedName("types")
    @Expose
    var types: List<String>? = null
    @SerializedName("torrent_link")
    @Expose
    var torrentLink: String? = null
    @SerializedName("torrentUpdate")
    @Expose
    var torrentUpdate: Int? = null
    @SerializedName("torrentList")
    @Expose
    var torrentList: List<TorrentList>? = null
    @SerializedName("mp4")
    @Expose
    var mp4: List<Any>? = null
    @SerializedName("Uppod")
    @Expose
    var uppod: List<Uppod>? = null
    @SerializedName("Moonwalk")
    @Expose
    var moonwalk: String? = null
    @SerializedName("fulll")
    @Expose
    var fulll: Fulll? = null


    inner class Fav {

        @SerializedName("sessId")
        @Expose
        var sessId: String? = null
        @SerializedName("skey")
        @Expose
        var skey: String? = null
        @SerializedName("isFaved")
        @Expose
        var isFaved: Boolean? = null
        @SerializedName("id")
        @Expose
        var id: Int? = null
        @SerializedName("count")
        @Expose
        var count: Int? = null
        @SerializedName("isGuest")
        @Expose
        var isGuest: Boolean? = null

    }

    inner class Fulll {

        @SerializedName("ID")
        @Expose
        var id: Int? = null
        @SerializedName("CODE")
        @Expose
        var code: String? = null

    }

    inner class TorrentList {

        @SerializedName("episode")
        @Expose
        var episode: String? = null
        @SerializedName("quality")
        @Expose
        var quality: String? = null
        @SerializedName("size")
        @Expose
        var size: String? = null
        @SerializedName("url")
        @Expose
        var url: String? = null

    }

    inner class Uppod(isViewed: Boolean) {

        var isViewed = false

        @SerializedName("comment")
        @Expose
        var comment: String? = null
        @SerializedName("file")
        @Expose
        var file: String? = null
        @SerializedName("filehd")
        @Expose
        var filehd: String? = null

        init {
            this.isViewed = isViewed
        }

    }
}