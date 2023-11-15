package com.teameetmeet.meetmeet.presentation.searchevent

import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import com.google.android.material.search.SearchView

@BindingAdapter("text_max_length")
fun SearchView.setTextMaxLength(maxLength: Int) {
    editText.addTextChangedListener {
        val text = it.toString()
        if (text.length > maxLength) {
            editText.setText(text.substring(0, maxLength))
            editText.setSelection(maxLength)
        }
    }
}

@BindingAdapter("on_submit_text")
fun SearchView.setOnSubmitText(onSubmitText: (String) -> Unit) {
    editText.setOnEditorActionListener { textView, _, _ ->
        onSubmitText(textView.text.toString())
        hide()
        false
    }
}
