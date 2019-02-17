package pe.devpicon.android.demo.expandtext

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_demo1.*
import pe.devpicon.android.expandtextdemo.R
import pe.devpicon.android.lib.expandtext.ExpandText


class Demo1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo1)

        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        appbar.bringToFront()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        val expandText = ExpandText.Builder().setLessLabelText(getString(R.string.less)).setLines(5).setMoreLabelText(getString(R.string.more)).build()
        expandText.applyExpandTextTo(tv_description, getString(R.string.manga_description))

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

