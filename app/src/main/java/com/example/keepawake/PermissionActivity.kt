package com.example.keepawake

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionActivity : AppCompatActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            checkAndRequestOverlayPermission()
        } else {
            showPermissionDeniedDialog("Notification")
        }
    }

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            showPermissionDeniedDialog("Draw Over Apps")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED -> {
                requestNotificationPermission()
            }
            !Settings.canDrawOverlays(this) -> {
                checkAndRequestOverlayPermission()
            }
            else -> {
                Toast.makeText(this, "All permissions already granted!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.POST_NOTIFICATIONS)) {
                AlertDialog.Builder(this)
                    .setTitle("Notification Permission Required")
                    .setMessage("This app needs notification permission to show keep awake status in the notification bar.")
                    .setPositiveButton("Grant") { _, _ ->
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        finish()
                    }
                    .show()
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            checkAndRequestOverlayPermission()
        }
    }

    private fun checkAndRequestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            AlertDialog.Builder(this)
                .setTitle("Draw Over Apps Permission Required")
                .setMessage("This app needs permission to draw over other apps to keep the screen awake. Please enable this permission in the next screen.")
                .setPositiveButton("Grant") { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    overlayPermissionLauncher.launch(intent)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    finish()
                }
                .show()
        } else {
            Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showPermissionDeniedDialog(permissionType: String) {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("$permissionType permission is required for the app to function properly. Please grant the permission in app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
        finish()
    }

    companion object {
        fun hasAllPermissions(context: android.content.Context): Boolean {
            return hasNotificationPermission(context) && hasOverlayPermission(context)
        }

        private fun hasNotificationPermission(context: android.content.Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }

        private fun hasOverlayPermission(context: android.content.Context): Boolean {
            return Settings.canDrawOverlays(context)
        }
    }
}