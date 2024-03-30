package csci5708.mobilecomputing.healthharvest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FoodTrackerActivity : AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var foodDatabaseHelper: FoodDatabaseHelper
    private lateinit var accordionLayout: LinearLayout
    private lateinit var layoutInflater: LayoutInflater



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_tracker)

        accordionLayout = findViewById(R.id.accordionLayout)
        layoutInflater = LayoutInflater.from(this)


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.menu.findItem(R.id.food).isChecked = true

        val fabAddFood: FloatingActionButton = findViewById(R.id.fabAddFood)
        fabAddFood.setOnClickListener {
            val intent = Intent(this, AddFoodItemActivity::class.java)
            startActivity(intent)
        }

        foodDatabaseHelper = FoodDatabaseHelper(this)

        val foodList = foodDatabaseHelper.getAllFoodItemsForToday()

        // Get the total calories consumed today
        val totalCaloriesToday = foodDatabaseHelper.getTotalCaloriesToday()

        // Set the ProgressBar values
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.max = 2000 // Max value is 2000 (adjust this as needed)
        progressBar.progress = totalCaloriesToday


        accordionLayout.removeAllViews()

        for (foodItem in foodList) {
            val accordionItemView = layoutInflater.inflate(R.layout.accordion_item, null)

            val foodNameTextView: TextView = accordionItemView.findViewById(R.id.foodNameTextView)
            val quantityTextView: TextView = accordionItemView.findViewById(R.id.quantityTakenTextView)
            val totalQuantityTextview : TextView = accordionItemView.findViewById(R.id.totalCaloriesTextView)
            val editImageView: ImageView = accordionItemView.findViewById(R.id.editImageView)
            val deleteImageView: ImageView = accordionItemView.findViewById(R.id.deleteImageView)


            val quantity = foodItem.quantity.toInt()
            val calories = foodItem.calories.toInt()
            val totalCalories = quantity * calories

            foodNameTextView.text = foodItem.name
            quantityTextView.text = quantity.toString()
            totalQuantityTextview.text = totalCalories.toString()


            // Handle edit action for this food item
            editImageView.setOnClickListener {
                val intent = Intent(this, EditFoodItemActivity::class.java)
                intent.putExtra("foodItemId", foodItem.id) // Pass the food item id to EditFoodItemActivity
                startActivity(intent)
            }

            // Handle delete action for this food item
            deleteImageView.setOnClickListener {
                foodDatabaseHelper.deleteFoodItem(foodItem.id)

                // Recalculate the total calories consumed today
                val updatedTotalCaloriesToday = foodDatabaseHelper.getTotalCaloriesToday()

                // Update the ProgressBar
                progressBar.progress = updatedTotalCaloriesToday

                // Update the accordionLayout by re-populating the accordion items
                updateAccordionLayout()
            }

            // Add the accordion item view to the accordionLayout
            accordionLayout.addView(accordionItemView)

            // Add a divider between accordion items
            val divider = layoutInflater.inflate(R.layout.divider, null)
            accordionLayout.addView(divider)
        }



        //Bottom Navigation bar
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> {
                    val intent = Intent(this@FoodTrackerActivity, UserDashboard::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent)
                    overridePendingTransition(0,0); //0 for no animation
                }

                R.id.food -> {
                    val intent = Intent(this@FoodTrackerActivity, FoodTrackerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent)
                    overridePendingTransition(0,0); //0 for no animation
                }

                R.id.water -> {
                    val intent = Intent(this@FoodTrackerActivity, WaterTrackerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent)
                    overridePendingTransition(0,0); //0 for no animation
                }
            }
            true
        }
    }

    fun updateAccordionLayout() {
        // Get the updated foodList and update the accordionLayout accordingly
        val updatedFoodList = foodDatabaseHelper.getAllFoodItemsForToday()

        // Clear the accordionLayout to remove all existing items
        accordionLayout.removeAllViews()

        // Add a divider between accordion items (optional)
        val divider = layoutInflater.inflate(R.layout.divider, accordionLayout, false)
        accordionLayout.addView(divider)

        for (foodItem in updatedFoodList) {
            val accordionItemView = layoutInflater.inflate(R.layout.accordion_item, null)

            val foodNameTextView: TextView = accordionItemView.findViewById(R.id.foodNameTextView)
            val quantityTextView: TextView = accordionItemView.findViewById(R.id.quantityTakenTextView)
            val totalQuantityTextview : TextView = accordionItemView.findViewById(R.id.totalCaloriesTextView)
            val editImageView: ImageView = accordionItemView.findViewById(R.id.editImageView)
            val deleteImageView: ImageView = accordionItemView.findViewById(R.id.deleteImageView)


            val quantity = foodItem.quantity.toInt()
            val calories = foodItem.calories.toInt()
            val totalCalories = quantity * calories

            foodNameTextView.text = foodItem.name
            quantityTextView.text = quantity.toString()
            totalQuantityTextview.text = totalCalories.toString()


            // Handle edit action for this food item
            editImageView.setOnClickListener {
                val intent = Intent(this, EditFoodItemActivity::class.java)
                intent.putExtra("foodItemId", foodItem.id) // Pass the food item id to EditFoodItemActivity
                startActivity(intent)
            }

            // Handle delete action for this food item
            deleteImageView.setOnClickListener {
                foodDatabaseHelper.deleteFoodItem(foodItem.id)

                // Recalculate the total calories consumed today
                val updatedTotalCaloriesToday = foodDatabaseHelper.getTotalCaloriesToday()


                // Update the accordionLayout by re-populating the accordion items
                updateAccordionLayout()
            }

            // Add the accordion item view to the accordionLayout
            accordionLayout.addView(accordionItemView)

            // Add a divider between accordion items
            val divider = layoutInflater.inflate(R.layout.divider, null)
            accordionLayout.addView(divider)
        }
    }





}