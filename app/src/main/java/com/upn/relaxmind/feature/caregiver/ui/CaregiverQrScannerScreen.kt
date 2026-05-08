package com.upn.relaxmind.feature.caregiver.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.data.models.User
import com.upn.relaxmind.core.ui.components.RelaxBackButton
import com.upn.relaxmind.core.ui.theme.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverQrScannerScreen(
    onBack: () -> Unit,
    onLinked: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    var scannedPatient by remember { mutableStateOf<User?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Escanear QR", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 12.dp)) {
                        RelaxBackButton(onClick = onBack, modifier = Modifier.size(40.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (hasCameraPermission) {
                CameraPreview(
                    executor = cameraExecutor,
                    onQrCodeScanned = { content ->
                        if (!showDialog && content.contains("LINK_TOKEN:RELAXMIND_LINK:")) {
                            val tokenPart = content.substringAfter("LINK_TOKEN:RELAXMIND_LINK:").trim()
                            // Get only the ID part (in case there are newlines after)
                            val patientId = tokenPart.split("\n", " ").first()
                            
                            val patient = AuthManager.getRegisteredUsers(context).find { it.id == patientId }
                            if (patient != null) {
                                scannedPatient = patient
                                showDialog = true
                            }
                        }
                    }
                )
                
                // Scanner Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(2.dp)
                    ) {
                        // Guide corners (simulated)
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Corner(true, true)
                                Corner(false, true)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Corner(true, false)
                                Corner(false, false)
                            }
                        }
                    }
                }
                
                Text(
                    text = "Apunta al código QR del paciente",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Se requiere permiso de cámara para escanear", color = Color.White)
                }
            }
        }
    }

    if (showDialog && scannedPatient != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Vincular Paciente?") },
            text = { 
                Text("Se ha detectado a ${scannedPatient?.name} ${scannedPatient?.lastName}. ¿Deseas vincularte como su cuidador?") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = AuthManager.linkPatient(context, scannedPatient!!.id)
                        if (success) {
                            showDialog = false
                            onLinked()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CaregiverBlue)
                ) {
                    Text("Sí, Vincular")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

@Composable
private fun Corner(isLeft: Boolean, isTop: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(if (isLeft) 0.dp else 4.dp, if (isTop) 0.dp else 4.dp, if (isLeft) 4.dp else 0.dp, if (isTop) 4.dp else 0.dp)
    ) {
        // Simple corner implementation
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    executor: ExecutorService,
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    
    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(options)
    }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            barcodes.firstOrNull()?.rawValue?.let { content ->
                                onQrCodeScanned(content)
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
    
    // Dispose scanner and executor
    DisposableEffect(Unit) {
        onDispose {
            scanner.close()
        }
    }
}
