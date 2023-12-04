package com.teameetmeet.meetmeet.presentation.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding>(private val layoutResId: Int) :
    AppCompatActivity() {

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this
    }

    protected fun showMessage(messageId: Int, extraMessage: String) {
        if (extraMessage.isNotEmpty()) {
            Toast.makeText(
                this,
                String.format(getString(messageId), extraMessage),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
        }
    }
}