package kt.anko.ranguel.ankokotlin

import android.app.Application
import android.arch.persistence.room.Room
import kt.anko.ranguel.ankokotlin.database.ReleaseDatabase

class App : Application() {

    companion object {
        lateinit var instance: App
        lateinit var database: ReleaseDatabase
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, ReleaseDatabase::class.java, "database").build()
    }

}