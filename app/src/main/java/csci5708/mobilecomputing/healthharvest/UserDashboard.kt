package csci5708.mobilecomputing.healthharvest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


object AppData{


    const val calorieGoal: Double = 2000.0

    const val waterGoal: Double = 8.0

    var lastTimeFoodAdded: Long = 0
    var lastTimeWaterAdded: Long = 0

    var whatsNeeded: String = ""


}


class UserDashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val welcomeMessage: TextView = findViewById(R.id.welcomeMessage)
        // fetch the username from shared preferences
        val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
        val user = sharedPref.getString("userName", "User")

        welcomeMessage.text = "$user's Tree"

        val height : TextView = findViewById(R.id.height_tree)
        val tree: ImageView = findViewById(R.id.tree_pic)
        val soilMoisture: TextView = findViewById(R.id.soilMoisture)
        val whatsNeeded: TextView = findViewById(R.id.whatsNeeded)
        setTreeType(tree, height, soilMoisture)
        whatsNeeded.text = AppData.whatsNeeded


        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> {
                    val intent = Intent(this@UserDashboard, UserDashboard::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent)
                    overridePendingTransition(0,0); //0 for no animation
                }

                R.id.food -> {
                    val intent = Intent(this@UserDashboard, FoodTrackerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent)
                    overridePendingTransition(0,0); //0 for no animation
                }

                R.id.water -> {
                    val intent = Intent(this@UserDashboard, WaterTrackerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent)
                    overridePendingTransition(0,0); //0 for no animation
                }
            }
            true
        }
    }

    fun setTreeType(tree: ImageView, height: TextView, soilMoisture: TextView){

        val foodDatabaseHelper = FoodDatabaseHelper(this)
        val waterDatabaseHelper = WaterDatabaseHelper(this)
        val waterTracker : Double = waterDatabaseHelper.getTotalWaterIntakeForToday().toDouble()/ AppData.waterGoal
        val calorieTracker: Double = foodDatabaseHelper.getTotalCaloriesToday().toDouble()/AppData.calorieGoal
        //var calorieTracker: Double = 2.0/AppData.calorieGoal

        val lastWaterConsumption = waterDatabaseHelper.getLastWaterIntakeInMilliSecondsSinceEpochs()
        val lastFoodConsumption = foodDatabaseHelper.getLastFoodIntakeInMilliSecondsSinceEpochs()
        val timeWaterDif = (System.currentTimeMillis() - lastWaterConsumption)/(1000*60*60)
        val timeFoodDif = (System.currentTimeMillis() - lastFoodConsumption)/(1000*60*60)


        if(timeFoodDif < 0.5 || timeWaterDif < 0.5){

            AppData.whatsNeeded = ""

            if(timeFoodDif < 0.5 && timeWaterDif < 0.5) {

                if (calorieTracker < 0.33) tree.setImageResource(R.drawable.healthysappling)
                else if (0.66 > calorieTracker) tree.setImageResource(R.drawable.healthyplant)
                else if (1.0 >= calorieTracker )tree.setImageResource(R.drawable.healthytree)
                else {
                    tree.setImageResource(R.drawable.deadtree)
                    AppData.whatsNeeded = "Hey there! Your tree is feeling a bit stuffed. Remember, moderation is key to a healthy lifestyle."
                }

            }

            else if(timeFoodDif < 0.5){

                if (calorieTracker < 0.33) tree.setImageResource(R.drawable.sunnysappling)
                else if (0.66 > calorieTracker) tree.setImageResource(R.drawable.sunnyplant)
                else if (1.0 >= calorieTracker )tree.setImageResource(R.drawable.sunnytree)
                else{
                    tree.setImageResource(R.drawable.deadtree)
                    AppData.whatsNeeded = "Hey there! Your tree is feeling a bit stuffed. Remember, moderation is key to a healthy lifestyle."
                }
            }

            else{
                if (calorieTracker < 0.33) tree.setImageResource(R.drawable.rainsappling)
                else if (0.66 > calorieTracker) tree.setImageResource(R.drawable.rainplant)
                else if (1.0 >= calorieTracker )tree.setImageResource(R.drawable.raintree)
                else{
                    tree.setImageResource(R.drawable.deadtree)
                    AppData.whatsNeeded = "Hey there! Your tree is feeling a bit stuffed. Remember, moderation is key to a healthy lifestyle."
                }
            }

        }

        else{

            if (calorieTracker < 0.33) tree.setImageResource(R.drawable.regular_sapling)
            else if (0.66 > calorieTracker) tree.setImageResource(R.drawable.regular_plant)
            else if (1.0 >= calorieTracker )tree.setImageResource(R.drawable.regular_tree)
            else {
                tree.setImageResource(R.drawable.deadtree)
                AppData.whatsNeeded = "Your tree is feeling stuffed. Remember, moderation is key to a healthy lifestyle."
            }


        }

        if(waterTracker == 0.0) {
            soilMoisture.text = "Moisture: Very Low"
        }
        else if(waterTracker > 0.0 && waterTracker <= 0.3){
            soilMoisture.text = "Moisture: Low"
        }
        else if(waterTracker > 0.3 && waterTracker <= 0.6){
            soilMoisture.text = "Moisture: Okay"
        }
        else if(waterTracker > 0.6 && waterTracker <= 0.9){
            soilMoisture.text = "Moisture: Great"
        }
        else if(waterTracker > 0.9 && waterTracker <= 1.2){
            soilMoisture.text = "Moisture: Perfect"
        }
        else if(waterTracker > 1.2){
            soilMoisture.text = "Soil is waterlogged"
        }
        val heightValue: Double = calorieTracker * 100
        val formattedNumber = String.format("%.2f", heightValue)
        height.text = "Height: $formattedNumber inches"

        if(waterTracker < 0.6 && calorieTracker < 0.6) AppData.whatsNeeded = "Don't forget to eat a balanced meal and drink enough water to see your tree flourish"
        else if (waterTracker < 0.6 && 0.6 <= calorieTracker && calorieTracker <= 1.0)  AppData.whatsNeeded = "Take a sip of water now and watch your tree thrive!"
        else if(waterTracker >= 0.6 && calorieTracker < 0.6) AppData.whatsNeeded = "Remember to fuel your body with nutritious food to help your tree flourish!"
        else if (waterTracker >= 0.6 && 0.6 <= calorieTracker && calorieTracker <= 1.0) AppData.whatsNeeded = "Great job! Your tree is in full bloom and so are you!"

    }
}
