package kt.anko.ranguel.ankokotlin.database.favorite

import android.arch.persistence.room.*
import java.io.Serializable


@Entity
class FavoriteData(var torrentUpdate: Int?,
                   @PrimaryKey var id: Int = 0,
                   var code: String?,
                   var title: String?,
                   var torrentLink: String?,
                   var link: String?,
                   var image: String?,
                   var episode: String?,
                   var description: String?,
                   var season: String?,
                   var voices: String?,
                   var genres: String?,
                   var types: String?,
                   var date: Long?) : Serializable