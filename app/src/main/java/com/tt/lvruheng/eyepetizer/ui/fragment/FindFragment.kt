package com.tt.lvruheng.eyepetizer.ui.fragment

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.adapter.FindAdapter
import com.tt.lvruheng.eyepetizer.databinding.FindFragmentBinding
import com.tt.lvruheng.eyepetizer.databinding.SearchFragmentBinding
import com.tt.lvruheng.eyepetizer.mvp.contract.FindContract
import com.tt.lvruheng.eyepetizer.mvp.model.bean.FindBean
import com.tt.lvruheng.eyepetizer.mvp.presenter.FindPresenter
import com.tt.lvruheng.eyepetizer.ui.FindDetailActivity


/**
 * Created by lvruheng on 2017/7/4.
 */
class FindFragment : BaseFragment<FindFragmentBinding>(),FindContract.View {
    var mPresenter: FindPresenter? = null
    var mAdapter : FindAdapter? = null
    var mList : MutableList<FindBean>? = null


    override fun setData(beans: MutableList<FindBean>) {
        mAdapter?.mList = beans
        mList = beans
        mAdapter?.notifyDataSetChanged()
    }

    override fun getLayoutResources(): Int {
        return R.layout.find_fragment
    }

    override fun initView() {
        mPresenter = FindPresenter(context,this)
        mPresenter?.start()
        mAdapter = FindAdapter(context,mList)
        binding.gvFind.adapter = mAdapter
        binding.gvFind.setOnItemClickListener { parent, view, position, id ->
            val bean = mList?.get(position)
            val name = bean?.name
            val intent = Intent(context,FindDetailActivity::class.java)
            intent.putExtra("name",name)
            intent.putExtra("id",bean?.id)
            startActivity(intent)

        }
    }

}