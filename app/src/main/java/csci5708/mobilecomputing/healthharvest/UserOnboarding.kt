package csci5708.mobilecomputing.healthharvest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class UserOnboarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_onboarding)

        val userName: EditText = findViewById(R.id.userName)
        val nextButton: Button = findViewById(R.id.nextButton)

        // start the user dashboard activity if the user name is already set
        val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
        val user = sharedPref.getString("userName", null)
        if(user != null) {
            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
        }

        nextButton.setOnClickListener {
            if(userName.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // set the user name in shared preferences
            val sharedPrefStorage = getSharedPreferences("user", MODE_PRIVATE)
            val editor = sharedPrefStorage.edit()
            editor.putString("userName", userName.text.toString())

            editor.apply()

            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
        }
    }
}