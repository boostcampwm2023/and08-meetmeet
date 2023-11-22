package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.DialogNotificationChangeBinding
import com.teameetmeet.meetmeet.util.dialogResize

class NotificationChangeDialog(
    context: Context,
    private val onNotiChangeListener: OnNotiChangeListener,
    private val noti: String
): Dialog(context) {

    private lateinit var binding: DialogNotificationChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_notification_change,
            null,
            false
        )
        setContentView(binding.root)
        context.dialogResize(this, 0.8f, 0.4f)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.dialogNotificationChangeEtBody.setText(noti)

        setClickListener()
    }

    private fun setClickListener() {
        with(binding) {
            dialogNotificationChangeTvCancel.setOnClickListener {
                dismiss()
            }
            dialogNotificationChangeTvSave.setOnClickListener {
                onNotiChangeListener.onSaveButtonClick(binding.dialogNotificationChangeEtBody.text.toString())
                dismiss()
            }
        }
    }
}