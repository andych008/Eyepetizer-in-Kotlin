package com.tt.lvruheng.eyepetizer.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.dylanc.viewbinding.inflateBindingWithGeneric


/**
 * Created by lvruheng on 2017/7/4.
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    var isFirst : Boolean = false
    var rootView :View? = null
    var isFragmentVisiable :Boolean = false


    private var _binding: VB? = null
    val binding:VB get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = inflateBindingWithGeneric(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        if(rootView==null){
//            rootView = inflater.inflate(getLayoutResources(),container,false)
//        }
//        return  rootView
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            isFragmentVisiable = true;
        }
        if (rootView == null) {
            return;
        }
        //可见，并且没有加载过
        if (!isFirst&&isFragmentVisiable) {
            onFragmentVisiableChange(true);
            return;
        }
        //由可见——>不可见 已经加载过
        if (isFragmentVisiable) {
            onFragmentVisiableChange(false);
            isFragmentVisiable = false;
        }
    }
    open protected fun onFragmentVisiableChange(b: Boolean) {

    }


    abstract fun getLayoutResources(): Int

    abstract fun initView()
}