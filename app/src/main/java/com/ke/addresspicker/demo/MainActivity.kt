package com.ke.addresspicker.demo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.amap.api.maps.MapsInitializer
import com.ke.addresspicker.KeAddressPicker
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        MapsInitializer.updatePrivacyShow(application,true,true)
        MapsInitializer.updatePrivacyAgree(application,true)


        findViewById<Button>(R.id.pick)
            .setOnClickListener {
                lifecycleScope.launch {
                    val result = KeAddressPicker(supportFragmentManager).pickAddress()

                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("获取地址结果")
                        .setMessage(result.toString())
                        .setPositiveButton("确定",null)
                        .show()
                }
            }


    }
}