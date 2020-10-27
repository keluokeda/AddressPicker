package com.ke.addresspicker.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ke.addresspicker.RxAddressPicker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        pick.setOnClickListener {
            RxAddressPicker(this)
                .pick().subscribe { address ->
//                    Log.d("TAG", "address = $address")
                    AlertDialog.Builder(this)
                        .setMessage(address.toString())
                        .show()
                }
        }


    }
}