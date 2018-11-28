package kt.anko.ranguel.ankokotlin.database.episode

import android.arch.persistence.room.*
import io.reactivex.Flowable


@Dao
interface EpisodeDao {

    @get:Query("SELECT * FROM EpisodeData")
    val getAll: Flowable<List<EpisodeData>>

    @Query("SELECT * FROM EpisodeData WHERE releaseId = :id")
    fun getById(id: Int): EpisodeData

    @Insert
    fun insert(episodeData: EpisodeData)

    @Update
    fun update(episodeData: EpisodeData)

    @Delete
    fun delete(episodeData: EpisodeData)

    @Delete
    fun deleteAll(episodeData: List<EpisodeData>)

}