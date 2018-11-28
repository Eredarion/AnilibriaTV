package kt.anko.ranguel.ankokotlin.database

import kt.anko.ranguel.ankokotlin.database.episode.EpisodeDao
import kt.anko.ranguel.ankokotlin.database.favorite.FavoriteDao
import kt.anko.ranguel.ankokotlin.database.history.HistoryDao
import android.arch.persistence.room.RoomDatabase
import kt.anko.ranguel.ankokotlin.database.episode.EpisodeData
import kt.anko.ranguel.ankokotlin.database.favorite.FavoriteData
import kt.anko.ranguel.ankokotlin.database.history.HistoryData
import android.arch.persistence.room.Database


@Database(entities = [HistoryData::class, FavoriteData::class, EpisodeData::class], version = 1)
abstract class ReleaseDatabase : RoomDatabase() {
    abstract fun getFavoriteDao(): FavoriteDao
    abstract fun getEpisodeDao(): EpisodeDao
    abstract fun getHistoryDao(): HistoryDao
}
