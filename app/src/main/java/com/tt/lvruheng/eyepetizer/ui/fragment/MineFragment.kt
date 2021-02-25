package com.tt.lvruheng.eyepetizer.ui.fragment

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.databinding.MineFragmentBinding
import com.tt.lvruheng.eyepetizer.ui.AdviseActivity
import com.tt.lvruheng.eyepetizer.ui.CacheActivity
import com.tt.lvruheng.eyepetizer.ui.WatchActivity

/**
 * Created by lvruheng on 2017/7/4.
 */
class MineFragment : BaseFragment<MineFragmentBinding>(),View.OnClickListener{
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_watch ->{
                var intent = Intent(activity,WatchActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_advise ->{
                var intent = Intent(activity,AdviseActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_save ->{
                var intent = Intent(activity,CacheActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun getLayoutResources(): Int {
        return R.layout.mine_fragment
    }

    override fun initView() {
        binding.tvAdvise.setOnClickListener(this)
        binding.tvWatch.setOnClickListener(this)
        binding.tvSave.setOnClickListener(this)
        binding.tvAdvise.typeface = Typeface.createFromAsset(context?.assets, "fonts/FZLanTingHeiS-DB1-GB-Regular.TTF")
        binding.tvWatch.typeface = Typeface.createFromAsset(context?.assets, "fonts/FZLanTingHeiS-DB1-GB-Regular.TTF")
        binding.tvSave.typeface = Typeface.createFromAsset(context?.assets, "fonts/FZLanTingHeiS-DB1-GB-Regular.TTF")
    }

}