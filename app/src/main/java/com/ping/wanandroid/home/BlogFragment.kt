package com.ping.wanandroid.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ping.wanandroid.BaseFragment
import com.ping.wanandroid.data.Blog
import com.ping.wanandroid.databinding.FragmentBlogBinding
import com.ping.wanandroid.databinding.ItemBlogBinding
import com.ping.wanandroid.extension.fragmentBind
import com.ping.wanandroid.extension.toGone
import com.ping.wanandroid.extension.toVisible

class BlogFragment : BaseFragment() {
    private val mBinding by fragmentBind(FragmentBlogBinding::inflate)
    private val blogVM: BlogViewModel by viewModels()

    private val blogAdapter by lazy { BlogAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            rv.adapter = blogAdapter
            srl.setOnRefreshListener {
                blogVM.blog(true)
            }
            rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (blogAdapter.itemCount - linearLayoutManager.findLastVisibleItemPosition() < 6) {
                        blogVM.blog()
                    }
                }
            })
        }

        blogVM.blog()

        lifecycleScope.launchWhenCreated {
            blogVM.blogFlow.collect {
                blogAdapter.setData(it)
                if (mBinding.srl.isRefreshing) {
                    mBinding.srl.isRefreshing = false
                }
            }
        }
    }
}

class BlogAdapter : RecyclerView.Adapter<BlogViewHolder>() {

    private var data: List<Blog> = listOf()

    fun setData(data: List<Blog>) {
//        val oldData = this.data
//        val diffUtil = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
//            override fun getOldListSize() = oldData.size
//
//            override fun getNewListSize() = data.size
//
//            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                return oldData.getOrNull(oldItemPosition)?.id == data.getOrNull(newItemPosition)?.id
//            }
//
//            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                return oldData.getOrNull(oldItemPosition)?.id == data.getOrNull(newItemPosition)?.id
//            }
//        })
//        diffUtil.dispatchUpdatesTo(this)
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {

        return BlogViewHolder(
            ItemBlogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bindData(data?.getOrNull(position))
    }

    override fun getItemCount() = data?.size ?: 0
}

class BlogViewHolder(private val viewBing: ItemBlogBinding) :
    RecyclerView.ViewHolder(viewBing.root) {

    fun bindData(blog: Blog?) {
        blog?.let {
            viewBing.apply {
                tvTop.visibility = if (it.isTop) View.VISIBLE else View.GONE
                tvClassification.text = "${it.superChapterName} / ${it.chapterName}"
                tvAuthor.text = it.author.ifEmpty { it.shareUser }
                tvTime.text = it.niceDate
                tvBlog.text = it.title
            }
        }

    }
}