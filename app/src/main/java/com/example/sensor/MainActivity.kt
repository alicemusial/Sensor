package com.example.sensor

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.d("MainActivity", "onCreate called")

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        setContent {
            LightSensor(sensorManager)
        }
    }

}

@Composable
fun LightSensor(sensorManager: SensorManager){
    Log.d("LightSensor", "LightSensor Composable started")
    var lightValue by remember { mutableFloatStateOf(0f) }

    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    if (lightSensor == null) {
        Log.e("LightSensor", "Light sensor not available on this device/emulator")
        return
    }
    val lightSensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                lightValue = event.values[0]
                Log.d("LightSensor", "Lux value: $lightValue")
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose { sensorManager.unregisterListener(lightSensorListener) }
    }

    val backgroundColor = if (lightValue > 1700) Color.LightGray else Color.DarkGray

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center){
        Text(text = if (lightValue > 1700) "It's light" else "It's dark",
            color = if (lightValue > 1700) Color.Black else Color.White
        )
    }
}
