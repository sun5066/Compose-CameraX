package github.sun5066.cameraxcompose

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
                        permission = "android.Manifest.permission.CAMERA",
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
private fun CheckPermission(
    permission: String,
    onDenied: @Composable (requester: () -> Unit) -> Unit = {},
    onGranted: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    var grantState by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    if (grantState) {
        onGranted.invoke()
    } else {
        val launcher: ManagedActivityResultLauncher<String, Boolean> =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                grantState = isGranted
            }
        onDenied { launcher.launch(permission) }
    }
}