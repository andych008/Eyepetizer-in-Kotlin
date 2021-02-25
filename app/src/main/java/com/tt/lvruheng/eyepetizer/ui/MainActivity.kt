package com.tt.lvruheng.eyepetizer.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.viewbinding.inflate
import com.gyf.barlibrary.ImmersionBar
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.databinding.ActivityMainBinding
import com.tt.lvruheng.eyepetizer.search.SEARCH_TAG
import com.tt.lvruheng.eyepetizer.search.SearchFragment
import com.tt.lvruheng.eyepetizer.ui.fragment.FindFragment
import com.tt.lvruheng.eyepetizer.ui.fragment.HomeFragment
import com.tt.lvruheng.eyepetizer.ui.fragment.HotFragment
import com.tt.lvruheng.eyepetizer.ui.fragment.MineFragment
import com.tt.lvruheng.eyepetizer.utils.showToast
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val binding: ActivityMainBinding by inflate()

    lateinit var homeFragment: HomeFragment
    lateinit var findFragment: FindFragment
    lateinit var hotFragemnt: HotFragment
    lateinit var mineFragment: MineFragment
    var mExitTime: Long = 0
    var toast: Toast? = null
    lateinit var searchFragment: SearchFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ImmersionBar.with(this).transparentBar().barAlpha(0.3f).fitsSystemWindows(true).init()
        val window = window
        val params = window.attributes
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.attributes = params
        setRadioButton()
        initToolbar()
        initFragment(savedInstanceState)
    }

    private fun initToolbar() {
        var today = getToday()
        binding.tvBarTitle.text = today
        binding.tvBarTitle.typeface = Typeface.createFromAsset(this.assets, "fonts/Lobster-1.4.otf")
        binding.ivSearch.setOnClickListener {
            if (binding.rbMine.isChecked) {
                //todo 点击设置
            } else {
                //todo 点击搜索
                searchFragment = SearchFragment()
                searchFragment.show(fragmentManager, SEARCH_TAG)
            }

        }
    }

    private fun getToday(): String {
        var list = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        var data: Date = Date()
        var calendar: Calendar = Calendar.getInstance()
        calendar.time = data
        var index: Int = calendar.get(Calendar.DAY_OF_WEEK) - 1
        if (index < 0) {
            index = 0
        }
        return list[index]
    }

    private fun initFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            //异常情况
            val mFragments: List<androidx.fragment.app.Fragment> = supportFragmentManager.fragments
            for (item in mFragments) {
                if (item is HomeFragment) {
                    homeFragment = item
                }
                if (item is FindFragment) {
                    findFragment = item
                }
                if (item is HotFragment) {
                    hotFragemnt = item
                }
                if (item is MineFragment) {
                    mineFragment = item
                }
            }
        } else {
            homeFragment = HomeFragment()
            findFragment = FindFragment()
            mineFragment = MineFragment()
            hotFragemnt = HotFragment()
            val fragmentTrans = supportFragmentManager.beginTransaction()
            fragmentTrans.add(R.id.fl_content, homeFragment)
            fragmentTrans.add(R.id.fl_content, findFragment)
            fragmentTrans.add(R.id.fl_content, mineFragment)
            fragmentTrans.add(R.id.fl_content, hotFragemnt)
            fragmentTrans.commit()
        }
        supportFragmentManager.beginTransaction().show(homeFragment)
                .hide(findFragment)
                .hide(mineFragment)
                .hide(hotFragemnt)
                .commit()
    }

    private fun setRadioButton() {
        binding.rbHome.isChecked = true
        binding.rbHome.setTextColor(resources.getColor(R.color.black))
        binding.rbHome.setOnClickListener(this)
        binding.rbFind.setOnClickListener(this)
        binding.rbHot.setOnClickListener(this)
        binding.rbMine.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        clearState()
        when (v?.id) {
            R.id.rb_find -> {
                binding.rbFind.isChecked = true
                binding.rbFind.setTextColor(resources.getColor(R.color.black))
                supportFragmentManager.beginTransaction().show(findFragment)
                        .hide(homeFragment)
                        .hide(mineFragment)
                        .hide(hotFragemnt)
                        .commit()
                binding.tvBarTitle.text = "Discover"
                binding.tvBarTitle.visibility = View.VISIBLE
                binding.ivSearch.setImageResource(R.drawable.icon_search)
            }
            R.id.rb_home -> {
                binding.rbHome.isChecked = true
                binding.rbHome.setTextColor(resources.getColor(R.color.black))
                supportFragmentManager.beginTransaction().show(homeFragment)
                        .hide(findFragment)
                        .hide(mineFragment)
                        .hide(hotFragemnt)
                        .commit()
                binding.tvBarTitle.text = getToday()
                binding.tvBarTitle.visibility = View.VISIBLE
                binding.ivSearch.setImageResource(R.drawable.icon_search)
            }
            R.id.rb_hot -> {
                binding.rbHot.isChecked = true
                binding.rbHot.setTextColor(resources.getColor(R.color.black))
                supportFragmentManager.beginTransaction().show(hotFragemnt)
                        .hide(findFragment)
                        .hide(mineFragment)
                        .hide(homeFragment)
                        .commit()
                binding.tvBarTitle.text = "Ranking"
                binding.tvBarTitle.visibility = View.VISIBLE
                binding.ivSearch.setImageResource(R.drawable.icon_search)
            }
            R.id.rb_mine -> {
                binding.rbMine.isChecked = true
                binding.rbMine.setTextColor(resources.getColor(R.color.black))
                supportFragmentManager.beginTransaction().show(mineFragment)
                        .hide(findFragment)
                        .hide(homeFragment)
                        .hide(hotFragemnt)
                        .commit()
                binding.tvBarTitle.visibility = View.INVISIBLE
                binding.ivSearch.setImageResource(R.drawable.icon_setting)
            }
        }

    }

    private fun clearState() {
        binding.rgRoot.clearCheck()
        binding.rbHome.setTextColor(resources.getColor(R.color.gray))
        binding.rbMine.setTextColor(resources.getColor(R.color.gray))
        binding.rbHot.setTextColor(resources.getColor(R.color.gray))
        binding.rbFind.setTextColor(resources.getColor(R.color.gray))
    }

    override fun onPause() {
        super.onPause()
        toast?.let { toast!!.cancel() }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis().minus(mExitTime) <= 3000) {
                finish()
                toast!!.cancel()
            } else {
                mExitTime = System.currentTimeMillis()
                toast = showToast("再按一次退出程序")
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
