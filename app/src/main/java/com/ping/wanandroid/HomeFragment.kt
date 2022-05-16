package com.ping.wanandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ping.wanandroid.databinding.FragmentHomeBinding
import com.ping.wanandroid.extension.fragmentBind
import com.ping.wanandroid.home.BlogFragment


class HomeFragment : BaseFragment() {

    private val mBinding by fragmentBind(FragmentHomeBinding::inflate)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.vp2.adapter = VPAdapter(this, listOf<Fragment>(BlogFragment()))
    }
}

class VPAdapter(fragment: Fragment, private val fragments: List<Fragment>) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}