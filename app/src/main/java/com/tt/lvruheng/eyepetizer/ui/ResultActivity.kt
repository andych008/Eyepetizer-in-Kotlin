package com.tt.lvruheng.eyepetizer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dylanc.viewbinding.inflate
import com.gyf.barlibrary.ImmersionBar
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.adapter.FeedAdapter
import com.tt.lvruheng.eyepetizer.databinding.ActivityResultBinding
import com.tt.lvruheng.eyepetizer.mvp.contract.ResultContract
import com.tt.lvruheng.eyepetizer.mvp.model.bean.HotBean
import com.tt.lvruheng.eyepetizer.mvp.presenter.ResultPresenter

/**
 * Created by lvruheng on 2017/7/11.
 */
class ResultActivity : AppCompatActivity(), ResultContract.View, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {
    private val binding: ActivityResultBinding by inflate()

    lateinit var keyWord: String
    lateinit var mPresenter: ResultPresenter
    lateinit var mAdapter: FeedAdapter
    var mIsRefresh: Boolean = false
    var mList = ArrayList<HotBean.ItemListBean.DataBean>()
    var start: Int = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).transparentBar().barAlpha(0.3f).fitsSystemWindows(true).init()
        setContentView(R.layout.activity_result)
        keyWord = intent.getStringExtra("keyWord")
        mPresenter = ResultPresenter(this, this)
        mPresenter.requestData(keyWord, start)
        setToolbar()
        binding.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        mAdapter = FeedAdapter(this, mList)
        binding.recyclerView.adapter = mAdapter
        binding.refreshLayout.setOnRefreshListener(this)
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                var layoutManager: androidx.recyclerview.widget.LinearLayoutManager = recyclerView?.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
                var lastPositon = layoutManager.findLastVisibleItemPosition()
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPositon == mList.size - 1) {
                    start = start.plus(10)
                    mPresenter.requestData(keyWord, start)
                }
            }
        })
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        var bar = supportActionBar
        bar?.title = "'$keyWord' 相关"
        bar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun setData(bean: HotBean) {
        if (mIsRefresh) {
            mIsRefresh = false
            binding.refreshLayout.isRefreshing = false
            if (mList.size > 0) {
                mList.clear()
            }

        }
        bean.itemList?.forEach {
            it.data?.let { it1 -> mList.add(it1) }
        }
        mAdapter.notifyDataSetChanged()
    }

    override fun onRefresh() {
        if (!mIsRefresh) {
            mIsRefresh = true
            start = 10
            mPresenter.requestData(keyWord, start)
        }
    }
}