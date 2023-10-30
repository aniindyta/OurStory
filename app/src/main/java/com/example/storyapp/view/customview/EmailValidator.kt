package com.example.storyapp.view.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import java.util.regex.Pattern

class EmailValidator : AppCompatEditText {
    private lateinit var errorDetection: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        errorDetection = ContextCompat.getDrawable(context, R.drawable.baseline_error_24) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    showErrorDetection()
                    setError("Email must not be empty.", errorDetection)
                } else if (!isValidEmail(s.toString())) {
                    showErrorDetection()
                    setError("Invalid email address.", errorDetection)
                } else {
                    hideErrorDetection()
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun showErrorDetection() {
        setErrorDetection(endOfTheText = errorDetection)
    }

    private fun hideErrorDetection() {
        setErrorDetection()
    }

    private fun setErrorDetection(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return Pattern.compile(emailPattern).matcher(email).matches()
    }
}
