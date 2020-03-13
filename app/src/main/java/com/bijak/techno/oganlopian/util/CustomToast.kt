package com.bijak.techno.oganlopian.util

import android.content.Context
import android.location.Location
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bijak.techno.oganlopian.R
import java.text.SimpleDateFormat
import java.util.*

class CustomToast(val context: Context) {
    val text: TextView
    val icon: ImageView
    val layout: View

    init {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        layout = inflater.inflate(R.layout.custom_meaasge, null)
        text = layout.findViewById(R.id.text)
        icon = layout.findViewById(R.id.icon)
    }

    private fun showActualToast() {
        val toast = Toast(context)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()

        //More subtle way
//        with(Toast(context)) {
//            setGravity(Gravity.CENTER_VERTICAL, 0, 0)
//            duration = Toast.LENGTH_LONG
//            view = layout
//            show()
//        }
    }

    fun showError(msg: String) {
        text.text = msg
        icon.setImageResource(R.drawable.round_error_24)
        icon.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.MULTIPLY)

        showActualToast()
    }

    fun showInfo(msg: String) {
        text.text = msg
        icon.setImageResource(R.drawable.round_info_24)
        icon.setColorFilter(ContextCompat.getColor(context, R.color.info), android.graphics.PorterDuff.Mode.MULTIPLY)

        showActualToast()
    }

    fun showSuccess(msg: String) {
        text.text = msg
        icon.setImageResource(R.drawable.round_success_24)
        icon.setColorFilter(ContextCompat.getColor(context, R.color.success), android.graphics.PorterDuff.Mode.MULTIPLY)

        showActualToast()
    }

    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        // distance in meter
        return results[0]
    }
    fun distanceText(distance: Float): String {
        val distanceString: String

        if (distance < 1000)
            if (distance < 1)
                distanceString = String.format(Locale.US, "%dm", 1)
            else
                distanceString = String.format(Locale.US, "%dm", Math.round(distance))
        else if (distance > 10000)
            if (distance < 1000000)
                distanceString = String.format(Locale.US, "%dkm", Math.round(distance / 1000))
            else
                distanceString = "FAR"
        else
            distanceString = String.format(Locale.US, "%.2fkm", distance / 1000)

        return distanceString
    }
    fun getDisplayDateTime(dateTimePhp: String): String {
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
            val date = simpleDateFormat.parse(dateTimePhp)
            val convetDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return convetDateFormat.format(date)
        } catch (e: Exception) {
            return ""
        }
    }
}
