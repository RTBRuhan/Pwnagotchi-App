package com.example.pwnagotchi

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.HttpAuthHandler
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton



class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var showWarning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Load saved settings
        val savedOrientation = sharedPreferences.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        val savedUrl = sharedPreferences.getString("url", "http://10.0.0.2:8080")
        showWarning = sharedPreferences.getBoolean("showWarning", true)

        // Set orientation
        requestedOrientation = savedOrientation

        // Set up WebView
        webView = findViewById(R.id.webview)
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedHttpAuthRequest(view: WebView?, handler: HttpAuthHandler?, host: String?, realm: String?) {
                showHttpAuthDialog(handler)
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.loadUrl(savedUrl ?: "http://10.0.0.2:8080")

        // Set up Floating Action Button
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { showMenu() }

        // Check and request BLUETOOTH_CONNECT permission for Android 12 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
            } else {
                enableBluetoothTethering()
            }
        } else {
            enableBluetoothTethering()
        }
    }

    private fun showHttpAuthDialog(handler: HttpAuthHandler?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Authentication Required")

        val view = layoutInflater.inflate(R.layout.dialog_http_auth, null)
        builder.setView(view)

        val username = view.findViewById<EditText>(R.id.username)
        val password = view.findViewById<EditText>(R.id.password)

        builder.setPositiveButton("OK") { _, _ ->
            val user = username.text.toString()
            val pass = password.text.toString()
            handler?.proceed(user, pass)
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            handler?.cancel()
        }
        builder.show()
    }

    private fun showMenu() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Menu")
        builder.setItems(arrayOf("Tweaks", "Change URL", "Credit")) { _, which ->
            when (which) {
                0 -> showTweaksMenu()
                1 -> showChangeURLMenu()
                2 -> showCreditMenu()
            }
        }
        builder.show()
    }

    private fun showTweaksMenu() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tweaks")

        val view = layoutInflater.inflate(R.layout.tweeks_menu, null)
        builder.setView(view)

        val rotationSwitch = view.findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.rotationSwitch)
        val fullScreenSwitch = view.findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.fullScreenSwitch)
        val disableWarningSwitch = view.findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.disableWarningSwitch)

        rotationSwitch.isChecked = requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        fullScreenSwitch.isChecked = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN != 0
        disableWarningSwitch.isChecked = !showWarning

        builder.setPositiveButton("OK") { _, _ ->
            requestedOrientation = if (rotationSwitch.isChecked) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            saveOrientation(requestedOrientation)

            window.decorView.systemUiVisibility = if (fullScreenSwitch.isChecked) {
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            } else {
                View.SYSTEM_UI_FLAG_VISIBLE
            }

            showWarning = !disableWarningSwitch.isChecked
            saveShowWarning(showWarning)
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }


    private fun showChangeURLMenu() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change URL")

        val input = EditText(this)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val currentUrl = sharedPreferences.getString("url", "http://192.168.44.44:8080")
        input.setText(currentUrl ?: "")
        builder.setView(input)

        builder.setPositiveButton("Load") { _, _ ->
            val url = input.text.toString()
            if (url.isNotBlank()) {
                webView.loadUrl(url)
                saveUrl(url)
            } else {
                Toast.makeText(this, "URL cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)

        builder.show()
    }

    private fun showCreditMenu() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Credit")

        val view = layoutInflater.inflate(R.layout.credit_menu, null)
        builder.setView(view)

        builder.setPositiveButton("OK", null)

        builder.show()
    }

    fun openGitHub(view: View) {
        val url = "https://github.com/RTBRuhan"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun saveOrientation(orientation: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit().putInt("orientation", orientation).apply()
    }

    private fun saveUrl(url: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit().putString("url", url).apply()
    }

    private fun saveShowWarning(show: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit().putBoolean("showWarning", show).apply()
    }

    private fun ensureBluetoothTethering() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        try {
            val method = wifiManager.javaClass.getDeclaredMethod("setWifiApEnabled", Boolean::class.javaPrimitiveType)
            method.invoke(wifiManager, true)
            Toast.makeText(this, "Bluetooth tethering enabled", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("Tethering", "Error enabling Bluetooth tethering", e)
            if (showWarning) showBluetoothTetheringWarning()
        }
    }

    private fun showBluetoothTetheringWarning() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enable Bluetooth Tethering")
        builder.setMessage("Please enable Bluetooth tethering in the settings.")
        builder.setPositiveButton("OK") { _, _ ->
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun enableBluetoothTethering() {
        ensureBluetoothTethering()
    }
}

