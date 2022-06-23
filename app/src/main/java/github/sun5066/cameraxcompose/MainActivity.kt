package github.sun5066.cameraxcompose

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import github.sun5066.cameraxcompose.camera.CameraView
import github.sun5066.cameraxcompose.ui.theme.CameraXComposeTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraXComposeTheme {
                // A surface container using the 'background' color from the theme
                    CheckPermission(
                        permission = Manifest.permission.CAMERA,
                        onGranted = {
                            Surface(color = MaterialTheme.colors.background) {
                            CameraView(
                                onImageCapture = { uri, fromGallery ->
                                    Log.d(TAG, "uri: $uri, fromGallery: $fromGallery")
                                }, onError = { imageCaptureException ->
                                    Log.e(TAG, "${imageCaptureException.message}")
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun CheckPermission(permission: String, onGranted: @Composable () -> Unit = {}) {
    val cameraPermissionState = rememberPermissionState(permission)

    if (cameraPermissionState.hasPermission) {
        onGranted.invoke()
    } else {
        Column {
            val textToShow = if (cameraPermissionState.shouldShowRationale) {
                "The camera is important for this app. Please grant the permission."
            } else {
                "Camera permission required for this feature to be available. Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}