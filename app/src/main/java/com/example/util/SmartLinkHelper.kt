package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object SmartLinkHelper {
    /**
     * Exact Adsterra SmartLink URL provided for integration.
     * Do not modify or replace.
     */
    const val SMARTLINK_URL = "https://www.profitableratecpmnetwork.com/cbni9m1hk2?key=1691fbbe4463e0e3e65f7e27606f80e7"

    /**
     * Safely opens the SmartLink in the system default browser.
     * Handles missing browser or security exceptions gracefully without crashing.
     */
    fun openSmartLink(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SMARTLINK_URL)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Unable to open browser. Please check your internet connection.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
