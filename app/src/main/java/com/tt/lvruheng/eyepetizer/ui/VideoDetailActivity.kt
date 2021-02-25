package com.tt.lvruheng.eyepetizer.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dylanc.viewbinding.inflate
import com.shuyu.gsyvideoplayer.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.databinding.ActivityVideoDetailBinding
import com.tt.lvruheng.eyepetizer.mvp.model.bean.VideoBean
import com.tt.lvruheng.eyepetizer.utils.*
import zlc.season.rxdownload2.RxDownload
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.concurrent.ExecutionException


/**
 * Created by lvruheng on 2017/7/7.
 */
class VideoDetailActivity : AppCompatActivity() {
    private val binding: ActivityVideoDetailBinding by inflate()

    companion object {
        var MSG_IMAGE_LOADED = 101
    }

    var mContext: Context = this
    lateinit var imageView: ImageView
    lateinit var bean: VideoBean
    var isPlay: Boolean = false
    var isPause: Boolean = false
    lateinit var orientationUtils: OrientationUtils
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                MSG_IMAGE_LOADED -> {
                    Log.e("video", "setImage")
                    binding.gsyPlayer.setThumbImageView(imageView)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_detail)
        bean = intent.getParcelableExtra<VideoBean>("data")
        initView()
        prepareVideo()
    }

    private fun initView() {
        var bgUrl = bean.blurred
        bgUrl?.let { ImageLoadUtils.displayHigh(this, binding.ivBottomBg, bgUrl) }
        binding.tvVideoDesc.text = bean.description
        binding.tvVideoDesc.typeface = Typeface.createFromAsset(this.assets, "fonts/FZLanTingHeiS-DB1-GB-Regular.TTF")
        binding.tvVideoTitle.text = bean.title
        binding.tvVideoTitle.typeface = Typeface.createFromAsset(this.assets, "fonts/FZLanTingHeiS-L-GB-Regular.TTF")
        var category = bean.category
        var duration = bean.duration
        var minute = duration?.div(60)
        var second = duration?.minus((minute?.times(60)) as Long)
        var realMinute: String
        var realSecond: String
        if (minute!! < 10) {
            realMinute = "0" + minute
        } else {
            realMinute = minute.toString()
        }
        if (second!! < 10) {
            realSecond = "0" + second
        } else {
            realSecond = second.toString()
        }
        binding.tvVideoTitle.text = "$category / $realMinute'$realSecond''"
        binding.tvVideoFavor.text = bean.collect.toString()
        binding.tvVideoShare.text = bean.share.toString()
        binding.tvVideoReply.text = bean.share.toString()
        binding.tvVideoDownload.setOnClickListener {
            //点击下载
            var url = bean.playUrl?.let { it1 -> SPUtils.getInstance(this, "downloads").getString(it1) }
            if (url.equals("")) {
                var count = SPUtils.getInstance(this, "downloads").getInt("count")
                if (count != -1) {
                    count = count.inc()
                } else {
                    count = 1
                }
                SPUtils.getInstance(this, "downloads").put("count", count)
                ObjectSaveUtils.saveObject(this, "download$count", bean)
                addMission(bean.playUrl,count)
            } else {
                showToast("该视频已经缓存过了")
            }
        }
    }

    private fun addMission(playUrl: String?, count: Int) {
        RxDownload.getInstance(this).serviceDownload(playUrl,"download$count").subscribe({
            showToast("开始下载")
            SPUtils.getInstance(this, "downloads").put(bean.playUrl.toString(),bean.playUrl.toString())
            SPUtils.getInstance(this, "download_state").put(playUrl.toString(), true)
        }, {
            showToast("添加任务失败")
        })
    }

    private fun prepareVideo() {
        var uri = intent.getStringExtra("loaclFile")
        if(uri!=null){
            Log.e("uri",uri)
            binding.gsyPlayer.setUp(uri, false, null, null)
        }else{
            binding.gsyPlayer.setUp(bean.playUrl, false, null, null)
        }
        //增加封面
        imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        ImageViewAsyncTask(mHandler, this, imageView).execute(bean.feed)
        binding.gsyPlayer.titleTextView.visibility = View.GONE
        binding.gsyPlayer.backButton.visibility = View.VISIBLE
        orientationUtils = OrientationUtils(this, binding.gsyPlayer)
        binding.gsyPlayer.setIsTouchWiget(true);
        //关闭自动旋转
        binding.gsyPlayer.isRotateViewAuto = false;
        binding.gsyPlayer.isLockLand = false;
        binding.gsyPlayer.isShowFullAnimation = false;
        binding.gsyPlayer.isNeedLockFull = true;
        binding.gsyPlayer.fullscreenButton.setOnClickListener {
            //直接横屏
            orientationUtils.resolveByClick();
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            binding.gsyPlayer.startWindowFullscreen(mContext, true, true);
        }
        binding.gsyPlayer.setStandardVideoAllCallBack(object : VideoListener() {
            override fun onPrepared(url: String?, vararg objects: Any?) {
                super.onPrepared(url, *objects)
                //开始播放了才能旋转和全屏
                orientationUtils.isEnable = true
                isPlay = true;
            }

            override fun onAutoComplete(url: String?, vararg objects: Any?) {
                super.onAutoComplete(url, *objects)

            }

            override fun onClickStartError(url: String?, vararg objects: Any?) {
                super.onClickStartError(url, *objects)
            }

            override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                super.onQuitFullscreen(url, *objects)
                orientationUtils?.let { orientationUtils.backToProtVideo() }
            }
        })
        binding.gsyPlayer.setLockClickListener { view, lock ->
            //配合下方的onConfigurationChanged
            orientationUtils.isEnable = !lock
        }
        binding.gsyPlayer.backButton.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

    }

    private class ImageViewAsyncTask(handler: Handler, activity: VideoDetailActivity, private val mImageView: ImageView) : AsyncTask<String, Void, String>() {
        private var handler = handler
        private var mPath: String? = null
        private var mIs: FileInputStream? = null
        private var mActivity: VideoDetailActivity = activity
        override fun doInBackground(vararg params: String): String? {
            val future = Glide.with(mActivity)
                    .load(params[0])
                    .downloadOnly(100, 100)
            try {
                val cacheFile = future.get()
                mPath = cacheFile.absolutePath
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            return mPath
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            try {
                mIs = FileInputStream(s)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            val bitmap = BitmapFactory.decodeStream(mIs)
            mImageView.setImageBitmap(bitmap)
            var message = handler.obtainMessage()
            message.what = MSG_IMAGE_LOADED
            handler.sendMessage(message)
        }
    }

    override fun onBackPressed() {
        orientationUtils?.let {
            orientationUtils.backToProtVideo()
        }
        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    override fun onResume() {
        super.onResume()
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoPlayer.releaseAllVideos()
        orientationUtils?.let {
            orientationUtils.releaseListener()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isPlay && !isPause) {
            if (newConfig?.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                if (!binding.gsyPlayer.isIfCurrentIsFullscreen) {
                    binding.gsyPlayer.startWindowFullscreen(mContext, true, true)
                }
            } else {
                //新版本isIfCurrentIsFullscreen的标志位内部提前设置了，所以不会和手动点击冲突
                if (binding.gsyPlayer.isIfCurrentIsFullscreen) {
                    StandardGSYVideoPlayer.backFromWindowFull(this);
                }
                orientationUtils?.let { orientationUtils.isEnable = true }
            }
        }
    }
}
