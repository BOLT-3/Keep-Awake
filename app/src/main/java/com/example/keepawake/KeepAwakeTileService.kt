package com.example.keepawake

import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class KeepAwakeTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val tile = qsTile ?: return

        if (!PermissionActivity.hasAllPermissions(this)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val intent = Intent(this, PermissionActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                startActivityAndCollapse(pendingIntent)
            }
            return
        }

        if (tile.state == Tile.STATE_ACTIVE) {
            tile.state = Tile.STATE_INACTIVE
            KeepAwakeForegroundService.stopService(this)
        } else {
            tile.state = Tile.STATE_ACTIVE
            KeepAwakeForegroundService.startService(this)
        }
        tile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile?.updateTile()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val intent = Intent(this, PermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        }
    }
}