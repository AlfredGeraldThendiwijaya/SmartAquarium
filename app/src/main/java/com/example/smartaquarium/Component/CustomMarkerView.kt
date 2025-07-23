package com.example.smartaquarium.Component

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.example.smartaquarium.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("ViewConstructor")
class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private val timeFormat: SimpleDateFormat,
    private val label: String
) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val time = timeFormat.format(Date(e.x.toLong()))
            val value = String.format("%.2f", e.y)
            tvContent.text = "$time\n$label: $value"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}
