package com.example.nutrisee.activity

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.nutrisee.R
import com.example.nutrisee.databinding.ActivityCameraBinding
import com.example.nutrisee.utils.Helper.Companion.createFile
import com.example.nutrisee.utils.Helper.Companion.uriToFile
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var cameraManager: CameraManager
    var isFlash = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButton()
    }

    private fun setupButton() {
        binding.apply {
            btnGallery.setOnClickListener {
                startGallery()
            }
            btnCamera.setOnClickListener {
                takePhoto()
            }
            btnFlash.setOnClickListener {
//                cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
//                flashlight(it)
                Toast.makeText(this@CameraActivity, R.string.error_flash, Toast.LENGTH_SHORT).show()
            }
            btnBack.setOnClickListener {
                val intent = Intent(this@CameraActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @Suppress("DEPRECATION")
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intentResult = Intent(this@CameraActivity, ResultActivity::class.java)
                    intentResult.putExtra(EXTRA_PICTURE, photoFile.absolutePath)
                    intentResult.putExtra(IS_BACK_CAMERA, cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    startActivityForResult(intentResult, REQUEST_RESULT)
                    startActivity(intentResult)
                    finish()
                }
            }
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_RESULT && resultCode == ResultActivity.CAMERA_X_RESULT) {
            if (data != null) {
                val successMessage = data.getStringExtra(ResultActivity.EXTRA_SUCCESS_MESSAGE)
                Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih gambar")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data

            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(URI_IMAGE, selectedImageUri.toString())
            startActivity(intent)
        }
    }

    companion object {
        const val URI_IMAGE = "URI_IMAGE"
        const val EXTRA_PICTURE = "EXTRA_PICTURE"
        const val IS_BACK_CAMERA = "IS_BACK_CAMERA"
        const val EXTRA_IS_BACK_CAMERA = "isBackCamera"
        const val REQUEST_RESULT = 200

    }
}


//    private fun flashlight(v: View?) {
//        try {
//            val cameraListId = cameraManager.cameraIdList[0]
//            val characteristics = cameraManager.getCameraCharacteristics(cameraListId)
//            val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
//
//            if (hasFlash == true) {
//                if (!isFlash) {
//                    cameraManager.setTorchMode(cameraListId, true)
//                    isFlash = true
//                } else {
//                    cameraManager.setTorchMode(cameraListId, false)
//                    isFlash = false
//                }
//            } else {
//                AlertDialog.Builder(this@CameraActivity).apply {
//                    setTitle(R.string.warning)
//                    setMessage(R.string.error_flash)
//                    setPositiveButton("Yes") { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                }
//            }
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        }
//    }