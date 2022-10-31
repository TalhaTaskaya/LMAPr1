package com.example.lmapr1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private val mHandler: Handler = Handler()
    private var mTimer: Runnable? = null
    private var xWertAccel : Double = 0.0
    private var xWertLicht : Double = 0.0
    private var xWertGyro : Double = 0.0
    var counterAccel = 0.0

    var jsonObject = JSONObject()
    var jsonArray = JSONArray()
    private lateinit var series : LineGraphSeries<DataPoint>
    private lateinit var series2 : LineGraphSeries<DataPoint>
    private lateinit var series3 : LineGraphSeries<DataPoint>
    var lichtOn = false
    var accelOn = false
    var gpsOn: Boolean = false
    var gyroOn = false
    val values : MutableList<DataPoint> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION"
                ), 0
            )
            return
        }

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        val lichtSwitch = findViewById<Switch>(R.id.lichtSwitch)
        val accelSwitch = findViewById<Switch>(R.id.accelSwitch)
        val gpsSwitch = findViewById<Switch>(R.id.gpsSwitch)
        val gyroSwitch = findViewById<Switch>(R.id.gyroSwitch)

        val lichtText = findViewById<TextView>(R.id.lichtText)
        val accelText = findViewById<TextView>(R.id.accelText)
        val gpsText = findViewById<TextView>(R.id.gpsText)
        val gyroText = findViewById<TextView>(R.id.gyroText)
        val seekText = findViewById<TextView>(R.id.seekText)

        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val graph = findViewById<View>(R.id.graph) as GraphView







        series = LineGraphSeries()
        series.title = "Accelerometer"
        series.color = Color.RED

        series2 = LineGraphSeries()
        series2.title = "Licht"
        series2.color = Color.BLUE

        series3 = LineGraphSeries()
        series3.title = "Gyroscope"
        series3.color = Color.GREEN


        graph.viewport.isScalable = true
        graph.viewport.setScalableY(true)
        graph.viewport.setScrollableY(true)
        graph.viewport.isScrollable = true

        graph.viewport.setMinY(-10.0)
        graph.viewport.setMaxY(10.0)
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(150.0)
        graph.viewport.setXAxisBoundsManual(true);
        graph.viewport.setYAxisBoundsManual(true);

        graph.addSeries(series)
        graph.addSeries(series2)
        graph.addSeries(series3)
        graph.legendRenderer.isVisible = true
        graph.legendRenderer.align = LegendRenderer.LegendAlign.BOTTOM


        val locationListener = object : LocationListener {
            @SuppressLint("SetTextI18n")
            override fun onLocationChanged(location: Location) {
                gpsText.text =
                    "Latitude: ${location?.latitude}\nLongitude: ${location?.longitude}"
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                Log.d("GPS", "${status}")
            }
            override fun onProviderEnabled(provider: String) {
                Log.d("GPS", "Enable")
            }
            override fun onProviderDisabled(provider: String) {
                Log.d("GPS", "Disable")
            }
        }




        val sensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {


                    }
                    if (event.sensor.type == Sensor.TYPE_LIGHT) {

                    }
                }

                when (event?.sensor?.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelText.text =
                            "Accelerometer:\n(X: ${event.values[0]}) \n(Y: ${event.values[1]}) \n(Z:${event.values[2]})"
                        xWertAccel = event.values[0].toDouble()
                        jsonObject.put("Accelerometer",null)
                        jsonObject.put("X",event?.values?.get(0))
                        jsonObject.put("Y",event?.values?.get(1))
                        jsonObject.put("Z",event?.values?.get(2))
                        jsonArray.put(jsonObject)

                    }

                    Sensor.TYPE_LIGHT -> {
                        lichtText.text = "Licht: ${event.values[0]}"
                        xWertLicht = event.values[0].toDouble()
                        jsonObject.put("Licht",null)
                        jsonObject.put("Licht:",event?.values?.get(0))
                        jsonArray.put(jsonObject)
                    }

                    Sensor.TYPE_GYROSCOPE -> {
                        gyroText.text =
                            "Gyroskop:\n(X: ${event.values[0]}) \n(Y: ${event.values[1]}) \n(Z:${event.values[2]})"
                        xWertGyro = event.values[0].toDouble()
                        jsonObject.put("Gyroscope",null)
                        jsonObject.put("X",event?.values?.get(0))
                        jsonObject.put("Y",event?.values?.get(1))
                        jsonObject.put("Z",event?.values?.get(2))
                        jsonArray.put(jsonObject)
                    }
                }
            }
        }





        var seekBarVal: Int = 0
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarVal = progress*10
                seekText.text = "${(seekBarVal / 10000)} ms"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Nothing to do
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (gyroOn) {
                    sensorManager?.unregisterListener(
                        sensorListener,
                        sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
                    )
                    sensorManager?.registerListener(
                        sensorListener,
                        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        seekBarVal
                    )
                }
                if (accelOn) {
                    sensorManager?.unregisterListener(
                        sensorListener,
                        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                    )
                    sensorManager?.registerListener(
                        sensorListener,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        seekBarVal
                    )
                }
                if (lichtOn) {
                    sensorManager?.unregisterListener(
                        sensorListener,
                        sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
                    )
                    sensorManager?.registerListener(
                        sensorListener,
                        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                        seekBarVal
                    )
                }

            }
        })


        lichtSwitch.setOnCheckedChangeListener { _, isChecked ->
            lichtOn = isChecked
            Toast.makeText(this, "Licht-Sensor angeschaltet", Toast.LENGTH_SHORT).show()

            if (lichtOn) {
                sensorManager?.registerListener(
                    sensorListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                    seekBarVal
                )
            } else {
                sensorManager?.unregisterListener(
                    sensorListener,
                    sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
                )
                Toast.makeText(this, "Licht-Sensor ist aus", Toast.LENGTH_SHORT).show()

            }
        }

        accelSwitch.setOnCheckedChangeListener { _, isChecked ->
            accelOn = isChecked
            Toast.makeText(this, "Accel.-Sensor angeschaltet", Toast.LENGTH_SHORT).show()

            if (accelOn) {
                sensorManager?.registerListener(
                    sensorListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    seekBarVal
                )
            } else {
                sensorManager?.unregisterListener(
                    sensorListener,
                    sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                )
                Toast.makeText(this, "Accel.-Sensor ist aus", Toast.LENGTH_SHORT).show()

            }
        }

        gpsSwitch.setOnCheckedChangeListener { _, isChecked ->
            gpsOn = isChecked
            Toast.makeText(this, "GPS angeschaltet", Toast.LENGTH_SHORT).show()

            if (gpsOn) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0f,
                    locationListener
                )
            } else {
                Toast.makeText(this, "GPS ausgeschaltet", Toast.LENGTH_SHORT).show()
                locationManager.removeUpdates(locationListener)
            }

        }


        gyroSwitch.setOnCheckedChangeListener { _, isChecked ->
            gyroOn = isChecked
            Toast.makeText(this, "Gyroskop-Sensor angeschaltet", Toast.LENGTH_SHORT).show()

            if (gyroOn) {
                sensorManager?.registerListener(
                    sensorListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    seekBarVal
                )
            } else {
                sensorManager?.unregisterListener(
                    sensorListener,
                    sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                )
                Toast.makeText(this, "Gyroskop-Sensor ausgeschaltet", Toast.LENGTH_SHORT).show()


            }
        }
    }
    fun drawGraph() {
        for (x in values){
            series.appendData(x, true, 100)
        }
    }

    override fun onResume() {
        super.onResume()

        mTimer = object : Runnable {

            override fun run() {
                if(!accelOn && !lichtOn && !gyroOn){
                    counterAccel = 0.0

                }else {
                    counterAccel += 1
                }
                if(accelOn){
                    series.appendData(DataPoint(counterAccel,xWertAccel), false, 1000)
                }
                if(lichtOn){
                    series2.appendData(DataPoint(counterAccel,xWertLicht/100), false, 1000)}
                if(gyroOn){
                    series3.appendData(DataPoint(counterAccel,xWertGyro), false, 1000)}

                mHandler.postDelayed(this, 1000)
                Log.d("Debug", jsonArray.toString())
            }
        }

        mHandler.postDelayed(mTimer as Runnable, 0)

    }
}