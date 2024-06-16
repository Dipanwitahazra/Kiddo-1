package com.parental.control.panjacreation.kiddo.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.parental.control.panjacreation.kiddo.util.Constants
import com.parental.control.panjacreation.kiddo.util.SharedPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val restrictedPackageSet = SharedPreferencesHelper.getHashSet(applicationContext)
            val packageName = event.packageName?.toString()

            if (restrictedPackageSet.contains(packageName)){
                startBackgroundRecognition()
                //var isParent: Boolean
                CoroutineScope(Dispatchers.IO).launch {
                    do {
                        delay(300)
                        if (Constants.isParent == null ){
                            continue
                        }
                        //isParent = SharedPreferencesHelper.getBoolean(applicationContext, Constants.IS_PARENT)
                        Log.d("Trigger: ", "Enter")
                        Log.d("User: ","IsParent: "+Constants.isParent+" Name: "+Constants.CURRENT_USER)
                        if (!Constants.isParent) {
                            performGlobalAction(GLOBAL_ACTION_BACK)
                            performGlobalAction(GLOBAL_ACTION_BACK)
                            Log.d("Trigger: ", "back")
                            stopBackgroundRecognition()
                            cancel()
                            Constants.isParent = null
                            //SharedPreferencesHelper.saveBoolean(applicationContext, Constants.IS_PARENT, true)
                        }
                    } while (Constants.isParent == null || (Constants.isParent == true))
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
