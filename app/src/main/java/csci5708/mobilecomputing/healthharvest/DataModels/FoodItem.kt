package csci5708.mobilecomputing.healthharvest.DataModels

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FoodItem(
    val id: Long,
    val name: String,
    val DateTaken: Long,
    val calories: Int,
    val quantity: Int
) {
}