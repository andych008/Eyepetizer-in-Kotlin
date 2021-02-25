package com.tt.lvruheng.eyepetizer.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.util.Log.println
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.adapter.HomeAdatper
import com.tt.lvruheng.eyepetizer.databinding.HomeFragmentBinding
import com.tt.lvruheng.eyepetizer.mvp.contract.HomeContract
import com.tt.lvruheng.eyepetizer.mvp.model.bean.HomeBean
import com.tt.lvruheng.eyepetizer.mvp.model.bean.HomeBean.IssueListBean.ItemListBean
import com.tt.lvruheng.eyepetizer.mvp.presenter.HomePresenter

import java.util.*
import java.util.regex.Pattern

/**
 * Created by lvruheng on 2017/7/4.
 */
class HomeFragment : BaseFragment<HomeFragmentBinding>(), HomeContract.View, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {
    var mIsRefresh: Boolean = false
    var mPresenter: HomePresenter? = null
    var mList = ArrayList<ItemListBean>()
    var mAdapter: HomeAdatper? = null
    var data: String? = null
    override fun setData(bean: HomeBean) {
        val regEx = "[^0-9]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(bean?.nextPageUrl)
        data = m.replaceAll("").subSequence(1, m.replaceAll("").length - 1).toString()
        if (mIsRefresh) {
            mIsRefresh = false
            binding.refreshLayout.isRefreshing = false
            if (mList.size > 0) {
                mList.clear()
            }

        }
        bean.issueList!!
                .flatMap { it.itemList!! }
                .filter { it.type.equals("video") }
                .forEach { mList.add(it) }
        mAdapter?.notifyDataSetChanged()
    }


    override fun getLayoutResources(): Int {
        return R.layout.home_fragment
    }

    override fun initView() {
        mPresenter = HomePresenter(context, this)
        mPresenter?.start()
        binding.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mAdapter = HomeAdatper(context, mList)
        binding.recyclerView.adapter = mAdapter
        binding.refreshLayout.setOnRefreshListener(this)
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                var layoutManager: androidx.recyclerview.widget.LinearLayoutManager = binding.recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
                var lastPositon = layoutManager.findLastVisibleItemPosition()
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPositon == mList.size - 1) {
                    if (data != null) {
                        mPresenter?.moreData(data)
                    }

                }
            }
        })

    }

    override fun onRefresh() {
        if (!mIsRefresh) {
            mIsRefresh = true
            mPresenter?.start()
        }
    }
}