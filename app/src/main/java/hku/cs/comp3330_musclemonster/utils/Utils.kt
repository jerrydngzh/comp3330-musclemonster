package hku.cs.comp3330_musclemonster.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    fun formatMillisToDateTimeString(millis: Long): String {
        val date = Date(millis)
        val formatter = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return formatter.format(date)
    }
}