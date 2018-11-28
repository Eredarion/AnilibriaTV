package kt.anko.ranguel.ankokotlin.database.history

import android.arch.persistence.room.*
import io.reactivex.Flowable


@Dao
interface HistoryDao {

    @get:Query("SELECT * FROM HistoryData ORDER BY date DESC")
    val getAll: Flowable<List<HistoryData>>

    @Query("SELECT * FROM HistoryData WHERE id = :id")
    fun getById(id: Long): HistoryData

    @Insert
    fun insert(release: HistoryData)

    @Update
    fun update(release: HistoryData)

    @Delete
    fun delete(release: HistoryData)

    @Delete
    fun deleteAll(releases: List<HistoryData>)

}