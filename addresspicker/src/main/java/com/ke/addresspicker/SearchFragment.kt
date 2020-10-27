package com.ke.addresspicker

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.concurrent.TimeUnit


class SearchFragment : Fragment(), Inputtips.InputtipsListener {


    private val foregroundColorSpan: ForegroundColorSpan = ForegroundColorSpan(Color.RED)

    private lateinit var locationClient: AMapLocationClient

    private var currentSearchText = ""

    private var currentCityName = ""

    private var currentPoint: LatLonPoint? = null

    private val compositeDisposable = CompositeDisposable()

    private val baseQuickAdapter =
        object : BaseQuickAdapter<Tip, BaseViewHolder>(R.layout.item_search_address_tip) {
            override fun convert(helper: BaseViewHolder, item: Tip) {


                val name = item.name ?: ""

                val address =
                    item.district ?: ""

                if (name.contains(currentSearchText)) {
                    val spannableStringBuilder = SpannableStringBuilder(name)

                    val index = name.indexOf(currentSearchText)

                    spannableStringBuilder.setSpan(
                        foregroundColorSpan,
                        index,
                        index + currentSearchText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    helper.setText(R.id.name, spannableStringBuilder)
                } else {
                    helper.setText(R.id.name, name)
                }

                helper.setText(R.id.address, address)
            }

        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        back.setOnClickListener {
            activity?.onBackPressed()
        }

        recycler_view.adapter = baseQuickAdapter

        address.textChanges().debounce(500, TimeUnit.MILLISECONDS)
            .skip(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onSearchTextChanged(it)
            }.addTo(compositeDisposable)

        initLocationClient()


        baseQuickAdapter.setOnItemClickListener { _, _, position ->
            val tip = baseQuickAdapter.getItem(position)!!
            hideKeyboard()

            (activity as? HostActivity)?.showAddressLocationView(tip)
        }
    }

    override fun onResume() {
        super.onResume()

        showSoftKeyboard(address)
    }


    fun hideKeyboard() {
        activity?.apply {
            val view = currentFocus
            if (view != null) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()
            }
        }
    }

    @MainThread
    private fun showSoftKeyboard(view: View) {
        view.requestFocus()
        view.postDelayed({
            val manager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            manager?.showSoftInput(view, 0)
        }, 100)
    }

    private fun initLocationClient() {
        locationClient = AMapLocationClient(activity?.application)

        locationClient.setLocationListener {

            if (it.errorCode != 0) {
                return@setLocationListener
            }
            currentCityName = it.city
            locationClient.stopLocation()
            currentPoint = LatLonPoint(it.latitude, it.longitude)


        }

        val locationClientOption = AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
            isNeedAddress = true
            isOnceLocation = false
            interval = 2000 * 10
        }

        locationClient.setLocationOption(locationClientOption)
        locationClient.startLocation()
    }

    private fun onSearchTextChanged(content: CharSequence?) {
        currentSearchText = content?.toString() ?: ""
        val inputtipsQuery = InputtipsQuery(currentSearchText, currentCityName)
        inputtipsQuery.cityLimit = false
        if (currentPoint != null) {
            inputtipsQuery.location = currentPoint
        }
        val inputTips = Inputtips(activity, inputtipsQuery)
        inputTips.setInputtipsListener(this)
        inputTips.requestInputtipsAsyn()
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }


    override fun onGetInputtips(list: MutableList<Tip>?, code: Int) {
        if (code == AMapException.CODE_AMAP_SUCCESS && list != null) {
            baseQuickAdapter.setNewData(list.filter { it.point != null })
        } else {
            baseQuickAdapter.setNewData(null)
        }
    }
}