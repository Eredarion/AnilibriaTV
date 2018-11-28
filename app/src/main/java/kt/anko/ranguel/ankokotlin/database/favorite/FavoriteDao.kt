package kt.anko.ranguel.ankokotlin.database.favorite

import android.arch.persistence.room.*
import io.reactivex.Flowable


@Dao
interface FavoriteDao {

    @get:Query("SELECT * FROM FavoriteData ORDER BY date DESC")
    val getAll: Flowable<List<FavoriteData>>

    @Query("SELECT * FROM FavoriteData WHERE id = :id")
    fun getById(id: Long): FavoriteData

    @Insert
    fun insert(release: FavoriteData)

    @Update
    fun update(release: FavoriteData)

    @Delete
    fun delete(release: FavoriteData)

    @Delete
    fun deleteAll(releases: List<FavoriteData>)

}
