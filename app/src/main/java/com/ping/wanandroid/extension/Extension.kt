package com.ping.wanandroid.extension

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.ping.wanandroid.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import java.io.InputStream
import java.io.OutputStream
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified V : ViewBinding> Activity.activityBind(crossinline provider: (LayoutInflater) -> V): Lazy<V> {
    val v = ViewBindingLazy {
        provider.invoke(layoutInflater)
    }
    return v
}

class ViewBindingLazy<V : ViewBinding>(private val viewBind: () -> V) : Lazy<V> {
    val cache: V? = null
    override val value: V
        get() {
            LogUtils.d("你有几次调用$this")
            return cache ?: viewBind.invoke()
        }

    override fun isInitialized() = cache != null
}


//inline fun <reified V : ViewBinding> activityBind(crossinline provider: (LayoutInflater) -> V): ActivityViewBind<Activity, V> {
//    val v = ActivityViewBind { activity: Activity ->
//        provider.invoke(activity.layoutInflater)
//    }
//    return v
//}
//
//
//class ActivityViewBind<A : Activity, V : ViewBinding>(private val viewBind: (A) -> V) :
//    ReadOnlyProperty<A, V> {
//    private var mBinding: V? = null
//    override fun getValue(thisRef: A, property: KProperty<*>): V {
//        mBinding?.let { return it }
//        mBinding = viewBind.invoke(thisRef)
//        return mBinding!!
//    }
//
//}

inline fun <reified V : ViewBinding> fragmentBind(crossinline provider: (LayoutInflater) -> V): FragmentViewBind<Fragment, V> {
    return FragmentViewBind { f ->
        provider.invoke(f.layoutInflater)
    }
}

class FragmentViewBind<F : Fragment, V : ViewBinding>(private val viewBind: (F) -> V) :
    ReadOnlyProperty<F, V> {
    private val TAG = "FragmentViewBind"
    private var mBinding: V? = null
    override fun getValue(thisRef: F, property: KProperty<*>): V {

        mBinding?.let { return it }

        if (thisRef.lifecycle.currentState == Lifecycle.State.DESTROYED) {
//            throw IllegalStateException("don't access viewBinding after destroyed ")
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "" +
                            "---------------------------------------------------------------------------------\n" +
                            "access viewBinding after destroyed ,the ${thisRef.javaClass.simpleName}may be leaked\n" +
                            "--------------------------------------------------------------------------------- "
                )
            }
            return viewBind.invoke(thisRef)
        }
        thisRef.lifecycle.addObserver(observer)
        return viewBind.invoke(thisRef).also {
            if (BuildConfig.DEBUG) {
                Log.d(
                    TAG,
                    "${thisRef.javaClass.simpleName} -> ${it.javaClass.simpleName}  initialization"
                )
            }
            mBinding = it
        }

    }

    private val observer = object : DefaultLifecycleObserver {

        @MainThread
        override fun onDestroy(owner: LifecycleOwner) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "${owner.javaClass.simpleName} is exe onDestroy")
            }
            owner.lifecycle.removeObserver(this)
            mBinding = null
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "${owner.javaClass.simpleName} mBinding is release")
            }
        }
    }
}


inline fun InputStream.listenerCopyTo(
    out: OutputStream,
    progressListener: (Long) -> Unit
) {
    val bufferSize: Int = 8 * 1024
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        progressListener(bytesCopied)
        bytes = read(buffer)
    }
}

fun View.toVisible() {
    visibility = View.VISIBLE
}

fun View.toGone() {
    visibility = View.GONE
}