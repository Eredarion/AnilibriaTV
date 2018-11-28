package kt.anko.ranguel.ankokotlin.database

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


fun fromAction(action: () -> Unit) : Completable {
    return Completable.fromAction(action)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
}