package com.example.beebrationv4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.example.beebrationv4.R
import com.example.beebrationv4.weatherapi.WeatherAPI
import com.example.beebrationv4.weatherapi.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var textViewVoltage: TextView
    private lateinit var textViewBatteryPercentage: TextView
    private lateinit var textViewLightBulbStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        // Get a reference to the Firebase Realtime Database
        database =
            FirebaseDatabase.getInstance("https://beebration-v2-52386-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        // Initialize UI elements
        textViewVoltage = findViewById(R.id.textView_voltage)
        textViewBatteryPercentage = findViewById(R.id.textView_batteryPercentage)
        textViewLightBulbStatus = findViewById(R.id.textView_lightBulbStatus)

        // Fetch data and listen for changes
        fetchData()

        // Fetch weather data
        fetchWeather("La Union")
    }

    private fun fetchWeather(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherAPI = retrofit.create(WeatherAPI::class.java)

        val apiKey = "6683d0f61594dcd86f9d23ca9e6ce47a" // Replace this with your OpenWeatherMap API key
        val call = weatherAPI.getCurrentWeather(city, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    // Handle the weather data here
                } else {
                    Log.e(TAG, "Failed to fetch weather data: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch weather data.", t)
            }
        })
    }


    private fun fetchData() {
        // Get a reference to the "data" child node in the Realtime Database
        val dataRef = database.child("data")

        // Attach a ValueEventListener to the "data" child node to listen for changes
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    // Handle the Voltage data fetched from the Realtime Database
                    val voltageValue = dataSnapshot.child("Voltage").getValue()
                    Log.d(TAG, "Voltage value is: $voltageValue")
                    // Update the TextView with the fetched Voltage data
                    when (voltageValue) {
                        is Long -> textViewVoltage.text = "Voltage: ${voltageValue}"
                        is Double -> textViewVoltage.text = "Voltage: ${voltageValue}"
                        else -> textViewVoltage.text = "Voltage: No data available"
                    }

                    // Handle the Battery Percentage data fetched from the Realtime Database
                    val batteryPercentageValue = dataSnapshot.child("Battery Percentage").getValue()
                    Log.d(TAG, "Battery Percentage value is: $batteryPercentageValue")

                    // Update the TextView with the fetched Battery Percentage data
                    when (batteryPercentageValue) {
                        is Long -> textViewBatteryPercentage.text = "Battery Percentage: ${batteryPercentageValue}"
                        is Double -> textViewBatteryPercentage.text = "Battery Percentage: ${batteryPercentageValue}"
                        else -> textViewBatteryPercentage.text = "Battery Percentage: No data available"
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Failed to read value.", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occur while fetching the data
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

        // Get a reference to the "status" child node in the Realtime Database
        val statusRef = database.child("status")

        // Attach a ValueEventListener to the "status" child node to listen for changes
        statusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    // Handle the Light Bulb Status data fetched from the Realtime Database
                    val lightBulbStatusValue = dataSnapshot.child("Bulb Status").getValue(Boolean::class.java)
                    Log.d(TAG, "Light Bulb Status value is: $lightBulbStatusValue")

                    // Update the TextView with the fetched Light Bulb Status data
                    textViewLightBulbStatus.text = when (lightBulbStatusValue) {
                        true -> "Light Bulb Status: On"
                        false -> "Light Bulb Status: Off"
                        else -> "Light Bulb Status: No data available"
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Failed to read value.", e)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occur while fetching the data
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    companion object {
        private const val TAG = "MainActivity"
    }



}


