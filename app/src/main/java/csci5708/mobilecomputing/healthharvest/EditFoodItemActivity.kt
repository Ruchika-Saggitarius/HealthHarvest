package csci5708.mobilecomputing.healthharvest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import csci5708.mobilecomputing.healthharvest.DataModels.FoodItem

class EditFoodItemActivity : AppCompatActivity() {
    private lateinit var foodDatabaseHelper: FoodDatabaseHelper
    private lateinit var editCaloriesEditText: TextView
    private lateinit var editFoodNameEditText: AutoCompleteTextView
    private lateinit var editQuantityEditText: TextView
    private lateinit var saveButton: Button
    private lateinit var discardButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_food_item)

        val foodItemId = intent.getLongExtra("foodItemId", -1)

        foodDatabaseHelper = FoodDatabaseHelper(this)
        val foodItem = foodDatabaseHelper.getFoodItem(foodItemId)

        editCaloriesEditText = findViewById(R.id.editCaloriesEditText)
        editFoodNameEditText = findViewById(R.id.editFoodNameEditText)
        editQuantityEditText = findViewById(R.id.editQuantityEditText)
        saveButton = findViewById(R.id.saveButton)
        discardButton = findViewById(R.id.discardButton)

        // Extract food names from the food items list
        val foodNames = foodDatabaseHelper.getAllFoodNames()

        // Create an ArrayAdapter to provide suggestions to the AutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, foodNames)

        // Set the adapter to the AutoCompleteTextView
        editFoodNameEditText.setAdapter(adapter)



        // Pre-fill the text fields with existing record data
        if (foodItem != null) {
            editFoodNameEditText.setText(foodItem.name)
            editCaloriesEditText.setText(foodItem.calories.toString())
        }

        saveButton.setOnClickListener {
            val newName = editFoodNameEditText.text.toString()

            val newCalories = editCaloriesEditText.text.toString().toInt()
            val quantity = editQuantityEditText.toString().toInt()

            // Update the record in the database
            val updatedFoodItem = FoodItem(foodItemId, newName, System.currentTimeMillis(), newCalories, quantity)

            foodDatabaseHelper.updateFoodItem(updatedFoodItem)
            navigateToFoodTrackerActivity()
        }

        discardButton.setOnClickListener {
            navigateToFoodTrackerActivity()
        }
    }

    private fun navigateToFoodTrackerActivity() {
        val intent = Intent(this, FoodTrackerActivity::class.java)
        startActivity(intent)
        finish()
    }
}
