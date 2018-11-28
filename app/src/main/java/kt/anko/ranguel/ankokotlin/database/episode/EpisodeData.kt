package kt.anko.ranguel.ankokotlin.database.episode

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity
class EpisodeData(@field:PrimaryKey var releaseId: Int?,
                  var data: String?)
