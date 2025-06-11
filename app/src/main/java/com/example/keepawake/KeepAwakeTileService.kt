package com.example.keepawake

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class KeepAwakeTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val tile = qsTile ?: return

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
}