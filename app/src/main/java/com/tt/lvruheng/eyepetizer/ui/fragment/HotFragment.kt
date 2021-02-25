package com.tt.lvruheng.eyepetizer.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.adapter.HotAdatpter
import com.tt.lvruheng.eyepetizer.databinding.HotFragmentBinding
import com.tt.lvruheng.eyepetizer.mvp.contract.HotContract
import com.tt.lvruheng.eyepetizer.mvp.model.bean.HotBean
import com.tt.lvruheng.eyepetizer.mvp.presenter.HotPresenter

/**
 * Created by lvruheng on 2017/7/4.
 */
class HotFragment : BaseFragment<HotFragmentBinding>() {
    var mTabs = listOf<String>("周排行", "月排行", "总排行").toMutableList()
    lateinit var mFragments: ArrayList<androidx.fragment.app.Fragment>
    val STRATEGY = arrayOf("weekly", "monthly", "historical")
    override fun getLayoutResources(): Int {
        return R.layout.hot_fragment
    }

    override fun initView() {
        var weekFragment: RankFragment = RankFragment()
        var weekBundle = Bundle()
        weekBundle.putString("strategy", STRATEGY[0])
        weekFragment.arguments = weekBundle
        var monthFragment: RankFragment = RankFragment()
        var monthBundle = Bundle()
        monthBundle.putString("strategy", STRATEGY[1])
        monthFragment.arguments = monthBundle
        var allFragment: RankFragment = RankFragment()
        var allBundle = Bundle()
        allBundle.putString("strategy", STRATEGY[2])
        allFragment.arguments = allBundle
        mFragments = ArrayList()
        mFragments.add(weekFragment as androidx.fragment.app.Fragment)
        mFragments.add(monthFragment as androidx.fragment.app.Fragment)
        mFragments.add(allFragment as androidx.fragment.app.Fragment)
        binding.vpContent.adapter = HotAdatpter(fragmentManager, mFragments, mTabs)
        binding.tabs.setupWithViewPager(binding.vpContent)
    }

}