package com.ke.addresspicker

import androidx.fragment.app.FragmentActivity
import io.reactivex.Observable

class RxAddressPicker(fragmentActivity: FragmentActivity) {

    private val tag = BuildConfig.LIBRARY_PACKAGE_NAME + RxAddressPicker::class.java.name
    private val delegateFragment: DelegateFragment

    init {
        val fragment = fragmentActivity.supportFragmentManager.findFragmentByTag(tag)

        if (fragment == null) {
            delegateFragment = DelegateFragment()
            fragmentActivity.supportFragmentManager.beginTransaction().add(delegateFragment, tag)
                .commitNow()
        } else {
            delegateFragment = fragment as DelegateFragment
        }
    }

    fun pick(): Observable<Address> {
        delegateFragment.start()
        return Observable.just(1).flatMap {
            delegateFragment.resultSubject
        }
    }
}