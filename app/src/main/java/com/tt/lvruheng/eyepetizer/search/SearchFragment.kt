package com.tt.lvruheng.eyepetizer.search

import android.app.DialogFragment
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.adapter.SearchAdapter
import com.tt.lvruheng.eyepetizer.databinding.SearchFragmentBinding
import com.tt.lvruheng.eyepetizer.ui.ResultActivity
import com.tt.lvruheng.eyepetizer.utils.KeyBoardUtils


/**
 * Created by lvruheng on 2017/7/9.
 */
const val SEARCH_TAG = "SearchFragment"

class SearchFragment : DialogFragment(), CircularRevealAnim.AnimListener,
        ViewTreeObserver.OnPreDrawListener, DialogInterface.OnKeyListener,
        View.OnClickListener {
    var data : MutableList<String> = arrayListOf("脱口秀","城会玩","666","笑cry","漫威",
            "清新","匠心","VR","心理学","舞蹈","品牌广告","粉丝自制","电影相关","萝莉","魔性"
            ,"第一视角","教程","毕业设计","奥斯卡","燃","冰与火之歌","温情","线下campaign","公益")
    lateinit var mCircularRevealAnim: CircularRevealAnim
    lateinit var mAdatper : SearchAdapter


    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogStyle);

    }

    override fun onStart() {
        super.onStart()
        initDialog()
    }

    private fun initDialog() {
        val window = dialog.window
        val metrics = resources.displayMetrics
        val width = (metrics.widthPixels * 0.98).toInt() //DialogSearch的宽
        window!!.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT)
        window.setGravity(Gravity.TOP)
        window.setWindowAnimations(R.style.DialogEmptyAnimation)//取消过渡动画 , 使DialogSearch的出现更加平滑
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding =SearchFragmentBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setData()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setData() {
        mAdatper = SearchAdapter(activity, data as ArrayList<String>)
        mAdatper.setOnDialogDismissListener(object :SearchAdapter.onDialogDismiss{
            override fun onDismiss() {
                hideAnim()
            }
        })
        val manager = FlexboxLayoutManager(activity)
        //设置主轴排列方式
        manager.flexDirection = FlexDirection.ROW
        //设置是否换行
        manager.flexWrap = FlexWrap.WRAP
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        binding.recyclerView.adapter = mAdatper
    }

    private fun init() {
        binding.tvHint.typeface = Typeface.createFromAsset(activity.assets, "fonts/FZLanTingHeiS-DB1-GB-Regular.TTF")
        mCircularRevealAnim = CircularRevealAnim()
        mCircularRevealAnim.setAnimListener(this)
        dialog.setOnKeyListener(this)
        binding.ivSearchSearch.viewTreeObserver.addOnPreDrawListener(this)
        binding.ivSearchSearch.setOnClickListener(this)
        binding.ivSearchBack.setOnClickListener(this)
    }

    override fun onHideAnimationEnd() {
        binding.etSearchKeyword.setText("");
        dismiss();
    }

    override fun onShowAnimationEnd() {
        if (isVisible) {
            KeyBoardUtils.openKeyboard(activity, binding.etSearchKeyword);
        }
    }

    override fun onPreDraw(): Boolean {
        binding.ivSearchSearch.viewTreeObserver.removeOnPreDrawListener(this);
        mCircularRevealAnim.show(binding.ivSearchSearch, binding.root);
        return true;
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {
            hideAnim()
        } else if (keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
            search()
        }
        return false
    }

    private fun search() {
        val searchKey = binding.etSearchKeyword.text.toString()
        if (TextUtils.isEmpty(searchKey.trim({ it <= ' ' }))) {
            Toast.makeText(activity, "请输入关键字", Toast.LENGTH_SHORT).show()
        } else {
            hideAnim()
            var keyWord = binding.etSearchKeyword.text.toString().trim()
            var intent : Intent = Intent(activity, ResultActivity::class.java)
            intent.putExtra("keyWord",keyWord)
            activity?.startActivity(intent)
        }
    }

    private fun hideAnim() {
        KeyBoardUtils.closeKeyboard(activity, binding.etSearchKeyword);
        mCircularRevealAnim.hide(binding.ivSearchSearch, binding.root)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_search_back -> {
                hideAnim()
            }
            R.id.iv_search_search ->{
                search()
            }
        }
    }

}