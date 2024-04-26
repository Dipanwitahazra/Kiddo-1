package com.parental.control.panjacreation.kiddo.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.parental.control.panjacreation.kiddo.util.Constants
import com.parental.control.panjacreation.kiddo.util.SharedPreferencesHelper

class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val restrictedPackageSet = SharedPreferencesHelper.getHashSet(applicationContext)
            val packageName = event.packageName?.toString()

            if (restrictedPackageSet.contains(packageName)){
                startBackgroundRecognition()
                val isParent = SharedPreferencesHelper.getBoolean(applicationContext, Constants.IS_PARENT)
                if (!isParent) {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    stopBackgroundRecognition()
                    SharedPreferencesHelper.saveBoolean(applicationContext, Constants.IS_PARENT, true)
                }
            } else {
                try {
                    stopBackgroundRecognition()
                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
            Log.d("onAccessibilityEvent: ", packageName.toString())
        }
    }

    override fun onInterrupt() {
        // Handle interruption
    }

    private fun startBackgroundRecognition() {
        try {
            startService(Intent(applicationContext, DetectorBackgroungService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopBackgroundRecognition() {
        try {
            stopService(Intent(applicationContext, DetectorBackgroungService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
