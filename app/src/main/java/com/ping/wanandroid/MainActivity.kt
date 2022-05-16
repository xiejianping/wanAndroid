package com.ping.wanandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import com.ping.wanandroid.databinding.ActivityMainBinding
import com.ping.wanandroid.extension.activityBind

class MainActivity : AppCompatActivity() {

    private val mBinding by activityBind(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        BarUtils.setStatusBarLightMode(this, true)
    }
}