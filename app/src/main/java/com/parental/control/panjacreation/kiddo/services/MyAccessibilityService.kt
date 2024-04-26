package com.parental.control.panjacreation.kiddo.services

import android.accessibilityservice.AccessibilityService
import android.app.Dialog
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.parental.control.panjacreation.kiddo.util.SharedPreferencesHelper

class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val restrictedPackageSet = SharedPreferencesHelper.getHashSet(applicationContext)
            val packageName = event.packageName?.toString()
            if (restrictedPackageSet.contains(packageName)){
                performGlobalAction(GLOBAL_ACTION_BACK)
            }
            Log.d("onAccessibilityEvent: ", packageName.toString())
        }
    }

    override fun onInterrupt() {
        // Handle interruption
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
