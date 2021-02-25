package com.tt.lvruheng.eyepetizer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.viewbinding.inflate
import com.gyf.barlibrary.ImmersionBar
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.databinding.ActivityAdviseBinding

/**
 * Created by lvruheng on 2017/7/11.
 */
class AdviseActivity:AppCompatActivity(){
    private val binding: ActivityAdviseBinding by inflate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advise)
        ImmersionBar.with(this).transparentBar().barAlpha(0.3f).fitsSystemWindows(true).init()
        settoolbar()
    }

     fun settoolbar(){
         setSupportActionBar(binding.toolbar)
         var bar = supportActionBar
         bar?.title = "意见反馈"
         bar?.setDisplayHomeAsUpEnabled(true)
         binding.toolbar.setNavigationOnClickListener {
             onBackPressed()
         }
     }
}