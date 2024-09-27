package me.ijachok.owtesttask.di

import android.app.Application
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App:Application(){
    private val TAG = "AFApplication"
    private val AF_DEV_KEY = "sQ84wpdxRTR4RMCaE9YqS4"

    @Override
    override fun onCreate() {
        super.onCreate()
        val conversionDataListener: AppsFlyerConversionListener =
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                    for (attrName in conversionData.keys) {
                        Log.d(
                            TAG, "ConversionData: " + attrName + " = " +
                                    conversionData[attrName]
                        )
                    }
                }

                override fun onConversionDataFail(s: String) {
                    Log.d(TAG, "Failure: $s")
                }

                override fun onAppOpenAttribution(conversionData: Map<String, String>) {
                    for (attrName in conversionData.keys) {
                        Log.d(
                            TAG, "OnAppOpenAttribute: " + attrName + " = " +
                                    conversionData[attrName]
                        )
                    }
                }

                override fun onAttributionFailure(errorMessage: String) {
                    Log.d(TAG, "error onAttributionFailure : $errorMessage")
                }
            }
        AppsFlyerLib.getInstance().setDebugLog(true) // TODO: disable for release
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionDataListener, applicationContext)
        AppsFlyerLib.getInstance().start(this)
    }
}