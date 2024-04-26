package com.parental.control.panjacreation.kiddo.activities

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.parental.control.panjacreation.kiddo.R
import com.parental.control.panjacreation.kiddo.adapters.AppListRecyclerAdapter
import com.parental.control.panjacreation.kiddo.databinding.ActivityMainBinding
import com.parental.control.panjacreation.kiddo.services.MyAccessibilityService

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)
        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        window.statusBarColor = typedValue.data



        val accessibilityEnabled = isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)
        if (!accessibilityEnabled){
            showAlertDialog()
        } else Toast.makeText(this, "Enabled", Toast.LENGTH_SHORT).show()

        val installedApps = getInstalledApps(this)
        Log.d("installed: ", "Size: ${installedApps.size}")
        for (appInfo in installedApps) {
            Log.d("installed: ", "Name: ${appInfo.name}")
        }

        binding.recyclerView.adapter = AppListRecyclerAdapter(applicationContext,installedApps)
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Alert")
            setMessage("Allow accessibility permission?")
            setPositiveButton("OK") { dialog, which ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, which ->
                Toast.makeText(applicationContext, "Please Enable accessibility permission from setting", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        while (colonSplitter.hasNext()) {
            if (colonSplitter.next().equals(context.packageName + "/" + service.name, ignoreCase = true)) {
                return true
            }
        }
        return false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            // Check if the accessibility permission is granted
            val accessibilityEnabled = isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)
            if (accessibilityEnabled) {
                // Accessibility service is enabled


            } else {
                // Accessibility service is not enabled
            }
        }
    }

    private fun getInstalledApps(context: Context): List<AppInfo> {
        val packageManager = context.packageManager
        val appsList = mutableListOf<AppInfo>()
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        for (app in packages) {
            if (packageManager.getLaunchIntentForPackage(app.packageName) != null) {
                val appName = packageManager.getApplicationLabel(app).toString()
                val appIcon = packageManager.getApplicationIcon(app)
                val packageName = app.packageName
                appsList.add(AppInfo(appName, appIcon, packageName))
            }
        }

        return appsList
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addFaceMenu -> {
                startActivity(Intent(this, DetectorActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
data class AppInfo(val name: String, val icon: Drawable, val packageName: String)