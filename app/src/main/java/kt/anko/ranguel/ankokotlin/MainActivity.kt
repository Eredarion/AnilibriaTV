package kt.anko.ranguel.ankokotlin

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.arellomobile.mvp.MvpAppCompatFragment
import kotlinx.android.synthetic.main.activity_main.*
import kt.anko.ranguel.ankokotlin.ui.list.ReleasesFragment

class MainActivity : AppCompatActivity() {

    private var exit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setFragment(ReleasesFragment())
        }
    }

    override fun onBackPressed() {
        if (!popFragment()) {
            if (!exit) {
                Snackbar.make(main_container, "Exit? Click again to confirm.",
                    Snackbar.LENGTH_LONG).show()
                exit = true
                Handler().postDelayed({exit = false }, 3000)
            } else {
                finish()
            }
        }
    }

    private fun setFragment(fragment: MvpAppCompatFragment) {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount >= 1) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        fm.beginTransaction()
          .replace(R.id.main_container, fragment)
          .commit()
    }

    fun pushFragment(fragment: MvpAppCompatFragment) {
        supportFragmentManager.beginTransaction()
                              .replace(R.id.main_container, fragment)
                              .addToBackStack(null)
                              .commit()
    }

    private fun popFragment() : Boolean {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
            return true
        }
        return false
    }
}
