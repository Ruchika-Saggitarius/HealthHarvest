package csci5708.mobilecomputing.healthharvest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import kotlin.random.Random
import android.widget.TextView
import csci5708.mobilecomputing.healthharvest.DataModels.FoodItem


class AddFoodItemActivity : AppCompatActivity() {
    private lateinit var foodNameEditText: AutoCompleteTextView
    private lateinit var caloriesEditText: TextView
    private lateinit var quantityEditText: TextView
    private lateinit var addFoodButton: Button
    private lateinit var cancelFoodButton: Button

    private lateinit var foodDatabaseHelper: FoodDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food_item)

        foodNameEditText = findViewById(R.id.foodNameEditText)
        caloriesEditText = findViewById(R.id.caloriesEditText)
        quantityEditText = findViewById(R.id.quantityEditText)


        addFoodButton = findViewById(R.id.addFoodButton)
        cancelFoodButton = findViewById(R.id.cancelAddFoodButton)

        foodDatabaseHelper = FoodDatabaseHelper(this)

        // Extract food names from the food items list
        val foodNames = foodDatabaseHelper.getAllFoodNames()

        Log.e("FoodNames", foodNames.toString())

        // Create an ArrayAdapter to provide suggestions to the AutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, foodNames)

        // Set the adapter to the AutoCompleteTextView
        foodNameEditText.setAdapter(adapter)

        addFoodButton.setOnClickListener {
            saveFoodItem()
        }

        cancelFoodButton.setOnClickListener {
            val intent = Intent(this, FoodTrackerActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveFoodItem() {
        val name = foodNameEditText.text.toString()
        val calories = caloriesEditText.text.toString().toIntOrNull()
        val quantity = quantityEditText.text.toString().toIntOrNull()

        if (name.isBlank() || calories == null || quantity == null ) {
            showToast("Please fill all the fields.")
            return
        }

        val randomId = Random.nextLong()

        val foodItem = FoodItem(randomId, name, System.currentTimeMillis(), calories, quantity)

        val newRowId = foodDatabaseHelper.insertFoodItem(foodItem)

        if (newRowId != -1L) {
            showToast("Entry successfully added.")
            // Move to FoodTrackerActivity after the toast is complete
            val intent = Intent(this, FoodTrackerActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            showToast("Error adding food item.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
