package com.ke.addresspicker

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.amap.api.services.help.Tip

class HostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        setStatusBarColor(resources.getColor(android.R.color.white))

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, MapViewFragment())
//                .addToBackStack(null)
                .commit()
        }
    }


    private fun setStatusBarColor(@ColorInt color: Int) {


        window?.apply {
            statusBarColor = color

            //状态栏文字颜色处理
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val flat =
                    if (isLightColor(color)) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else View.SYSTEM_UI_FLAG_VISIBLE

                decorView.systemUiVisibility = flat
            }
        }

//        activity?.window?.statusBarColor = color


    }

    fun showSearchView() {
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, SearchFragment())
            .addToBackStack(null).commit()
    }

    fun showAddressLocationView(tip: Tip) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, AddressLocationFragment.newInstance(tip))
            .addToBackStack(null).commit()
    }


    private fun isLightColor(@ColorInt color: Int): Boolean =
        ColorUtils.calculateLuminance(color) >= 0.5

    companion object {
        const val key_latitude = "key_latitude"
        const val key_longitude = "key_longitude"
        const val key_address = "key_address"
    }
}