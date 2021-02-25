package com.tt.lvruheng.eyepetizer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dylanc.viewbinding.inflate
import com.gyf.barlibrary.ImmersionBar
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.adapter.RankAdapter
import com.tt.lvruheng.eyepetizer.databinding.ActivityFindDetailBinding
import com.tt.lvruheng.eyepetizer.mvp.contract.FindDetailContract
import com.tt.lvruheng.eyepetizer.mvp.model.bean.HotBean
import com.tt.lvruheng.eyepetizer.mvp.presenter.FindDetailPresenter
import java.util.regex.Pattern

/**
 * Created by lvruheng on 2017/7/8.
 */
class FindDetailActivity : AppCompatActivity(), FindDetailContract.View, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {
    private val binding: ActivityFindDetailBinding by inflate()

    lateinit var mPresenter: FindDetailPresenter
    lateinit var mAdapter: RankAdapter
    lateinit var data: String
    var mIsRefresh: Boolean = false
    var mList: ArrayList<HotBean.ItemListBean.DataBean> = ArrayList()
    var mstart: Int = 0
    override fun setData(bean: HotBean) {
        if (bean.count>0) {
            mstart = mstart.plus(bean.count)
        }
        if (bean.nextPageUrl!=null) {
            val regEx = "[^0-9]"
            val p = Pattern.compile(regEx)
            val m = p.matcher(bean.nextPageUrl as CharSequence?)
            data = m.replaceAll("").subSequence(1, m.replaceAll("").length - 1).toString()
            if (mIsRefresh) {
                mIsRefresh = false
                binding.refreshLayout.isRefreshing = false
                if (mList.size > 0) {
                    mList.clear()
                }

            }
        }

        bean.itemList?.forEach {
            if (it.type.equals("video")) {
                it.data?.let { it1 -> mList.add(it1) }
            }
        }
        mAdapter.notifyDataSetChanged()
    }

    lateinit var name: String
    private var catId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_detail)
        ImmersionBar.with(this).transparentBar().barAlpha(0.3f).fitsSystemWindows(true).init()
        setToolbar()

        catId = intent.getIntExtra("id",0)


        binding.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        mAdapter = RankAdapter(this, mList)
        binding.recyclerView.adapter = mAdapter
        binding.refreshLayout.setOnRefreshListener(this)
        mPresenter = FindDetailPresenter(this, this)
        mPresenter.requestData(catId, "date")
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                var layoutManager: androidx.recyclerview.widget.LinearLayoutManager = recyclerView?.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
                var lastPositon = layoutManager.findLastVisibleItemPosition()
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPositon == mList.size - 1) {
                    if (data != null) {
                        mPresenter?.requesMoreData(mstart, catId, "date")
                    }

                }
            }
        })
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        var bar = supportActionBar
        intent.getStringExtra("name")?.let {
            name = intent.getStringExtra("name")
            bar?.title = name
        }
        bar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRefresh() {
        if (!mIsRefresh) {
            mIsRefresh = true
            mPresenter.requestData(catId, "date")
        }
    }
}