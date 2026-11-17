package com.ipvio.stream

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/** Runs [onGranted] only when this app may communicate with devices on the local network. */
@Composable
internal fun rememberLocalNetworkAccess(onGranted: () -> Unit, onDenied: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val permission = "android.permission.ACCESS_LOCAL_NETWORK"
    val grantedAction = rememberUpdatedState(onGranted)
    val deniedAction = rememberUpdatedState(onDenied)
    val request = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) grantedAction.value() else deniedAction.value()
    }
    return {
        if (Build.VERSION.SDK_INT < 37 || ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            grantedAction.value()
        } else request.launch(permission)
    }
}
