package hku.cs.comp3330_musclemonster

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Example of accessing a view
        val greetingText: TextView = findViewById(R.id.tvGreeting)
        greetingText.text = "Hello XML World!"
    }
}
