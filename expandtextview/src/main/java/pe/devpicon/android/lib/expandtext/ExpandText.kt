package pe.devpicon.android.lib.expandtext

import android.animation.LayoutTransition
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ExpandText private constructor(builder: Builder) {
    private val numberOfLines: Int
    private val moreLabelText: String
    private val lessLabelText: String
    private val moreLabelColor: Int
    private val lessLabelColor: Int

    init {
        this.numberOfLines = builder.numberOfLines
        this.moreLabelText = builder.moreLabelText
        this.lessLabelText = builder.lessLabelText
        this.moreLabelColor = builder.moreLabelColor
        this.lessLabelColor = builder.lessLabelColor
    }

    fun applyExpandTextTo(textView: TextView, text: String) {
        textView.setLines(numberOfLines)
        textView.text = text

        textView.post(Runnable {
            var currentNumberOfLines = numberOfLines

            if (textView.layout.lineCount <= numberOfLines) {
                textView.text = text
                return@Runnable
            }

            val marginLayoutParams: ViewGroup.MarginLayoutParams = textView.layoutParams as ViewGroup.MarginLayoutParams
            val reducedString = text.substring(textView.layout.getLineStart(0), textView.layout.getLineEnd(numberOfLines - 1))
            currentNumberOfLines = reducedString.length - (moreLabelText.length + 4 + marginLayoutParams.rightMargin / 6)

            val spannableString = SpannableString("${text.substring(0, currentNumberOfLines)}... $moreLabelText")
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    applyContractTextTo(textView, text)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = moreLabelColor
                    ds.isUnderlineText = true
                    ds.isFakeBoldText = true
                }
            }

            spannableString.setSpan(clickableSpan, spannableString.length - moreLabelText.length, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val layoutTransition = LayoutTransition()
                layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                (textView.parent as ViewGroup).layoutTransition = layoutTransition
            }

            textView.text = spannableString
            textView.movementMethod = LinkMovementMethod.getInstance()
        })
    }

    private fun applyContractTextTo(textView: TextView, text: String) {
        textView.maxLines = Integer.MAX_VALUE
        val spannableString = SpannableString("$text $lessLabelText")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                Handler().post { applyExpandTextTo(textView, text) }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = lessLabelColor
            }
        }
        spannableString.setSpan(clickableSpan, spannableString.length - lessLabelText.length, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    class Builder {
        internal var numberOfLines: Int = 3
        internal var moreLabelText: String = ""
        internal var lessLabelText: String = ""
        // Colors from https://www.color-hex.com/
        internal var moreLabelColor = Color.parseColor("#007BFF")
        internal var lessLabelColor = Color.parseColor("#CC554C")

        fun setLines(lines: Int): Builder {
            this.numberOfLines = lines
            return this@Builder
        }

        fun setMoreLabelText(text: String): Builder {
            this.moreLabelText = text
            return this@Builder
        }

        fun setLessLabelText(text: String): Builder {
            this.lessLabelText = text
            return this@Builder
        }

        fun setMoreLabelColor(color: Int): Builder {
            this.moreLabelColor = color
            return this@Builder
        }

        /**
         * Use Color.parseColor function
         * Get hex codes from https://www.color-hex.com/
         */
        fun setLessLabelColor(color: Int): Builder {
            this.lessLabelColor = color
            return this@Builder
        }

        fun build(): ExpandText {
            return ExpandText(this)
        }
    }
}