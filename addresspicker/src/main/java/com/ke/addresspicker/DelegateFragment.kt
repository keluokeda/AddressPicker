package com.ke.addresspicker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.subjects.PublishSubject

class DelegateFragment : Fragment() {

    lateinit var resultSubject: PublishSubject<Address>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }

    @SuppressLint("CheckResult")
    fun start() {
        resultSubject = PublishSubject.create()

        RxPermissions(this)
            .request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_PHONE_STATE
            ).subscribe {
                if (it) {
                    startActivityForResult(Intent(requireContext(), HostActivity::class.java), 101)
                } else {
                    resultSubject.onComplete()
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            val latitude = data.getDoubleExtra(HostActivity.key_latitude, .0)
            val longitude = data.getDoubleExtra(HostActivity.key_longitude, .0)
            val name = data.getStringExtra(HostActivity.key_address) ?: ""
            resultSubject.onNext(Address(latitude, longitude, name))
            resultSubject.onComplete()
        }
    }
}