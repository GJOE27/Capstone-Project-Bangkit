package com.example.nutrisee.activity

import android.R
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.nutrisee.ViewModelFactory
import com.example.nutrisee.activity.CameraActivity.Companion.EXTRA_PICTURE
import com.example.nutrisee.activity.CameraActivity.Companion.URI_IMAGE
import com.example.nutrisee.activity.fragment.DetailFragment
import com.example.nutrisee.databinding.ActivityResultBinding
import com.example.nutrisee.utils.Helper.Companion.rotateFile
import com.example.nutrisee.utils.Helper.Companion.uriToFile
import com.example.nutrisee.utils.Result
import com.example.nutrisee.viewmodel.PredictViewModel
import kotlinx.coroutines.Job
import java.io.File

@ExperimentalGetImage class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: PredictViewModel

    private var getFile: File? = null
    private var uploadJob: Job = Job()
    private var isProcessing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        putImage()
        setupButton()

        binding.btnUpload.setOnClickListener {
            Toast.makeText(this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show()
//            upload()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(application)
        )[PredictViewModel::class.java]
    }

    private fun setupButton() {
        binding.apply {
            tvResultName.setOnClickListener {
//                setupFragment()
            }

            btnBack.setOnClickListener {
                val intent = Intent(this@ResultActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun putImage() {
        val picturePath = intent.getStringExtra(EXTRA_PICTURE)
        val pictureGallery = intent.getStringExtra(URI_IMAGE)
        if (picturePath != null) {
            val imageFile = File(picturePath)
            val imageUri = Uri.fromFile(imageFile)
            rotateFile(imageFile)
            Glide.with(this)
                .load(imageUri)
                .into(binding.previewView)
            viewModel.predict(imageFile).observe(this@ResultActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
//                        binding.previewView.visibility = View.GONE
                    }

                    is Result.Success -> {
                        binding.pbLoading.visibility = View.GONE
//                        binding.previewView.visibility = View.VISIBLE
                        val response = result.data
                        val message = response?.message
                        val name = response?.name
                        val kalori = "${response?.kalori} kalori per 1 serving"
                        val formattedName =
                            name?.replace("_", " ")?.replaceFirstChar { it.uppercase() }
                        val print = "$formattedName\n$kalori kalori"
                        binding.tvResultName.text = formattedName
                        binding.tvResultKalori.text = kalori
                        if (name != null) {
                            successPredict(name)
                        }
                    }

                    is Result.Error -> {
                        binding.pbLoading.visibility = View.GONE
                        val response = result.data
                        val error = result.message
                        val errorMessage = result.data?.message
                        val name = response?.name
                        val kalori = response?.kalori
                        val message = response?.message
                        if (errorMessage != null) {
                            val errorPosition =
                                "Error position: ${Thread.currentThread().stackTrace[2]}"
                            Log.e("API Error", "$errorPosition $errorMessage")
                        }
                        Log.e("TAG", "Error : $errorMessage")
                        val formattedText =
                            message?.replace("_", " ")?.replaceFirstChar { it.uppercase() }
                        val print = "[ERROR] $formattedText \n $name \n $kalori\n Error : $error"
                        binding.tvResultName.text = print
//                        if (name != null) {
//                            successPredict(name)
//                        }
                        Toast.makeText(
                            this@ResultActivity,
                            "Terjadi kesalahan: $print",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }

        if (pictureGallery != null) {
            val file = uriToFile(Uri.parse(pictureGallery), this)
            val bitmap =
                MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(pictureGallery))
            binding.previewView.setImageBitmap(bitmap)
            viewModel.predict(file).observe(this@ResultActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
//                        binding.previewView.visibility = View.GONE
                    }

                    is Result.Success -> {
                        binding.pbLoading.visibility = View.GONE
//                        binding.previewView.visibility = View.VISIBLE
                        val response = result.data
                        val message = response?.message
                        val name = response?.name
                        val kalori = "${response?.kalori} kalori"
                        val formattedName =
                            name?.replace("_", " ")?.replaceFirstChar { it.uppercase() }
                        val print = "$formattedName\n$kalori kalori"
                        binding.tvResultName.text = formattedName
                        binding.tvResultKalori.text = kalori
                    }

                    is Result.Error -> {
                        binding.pbLoading.visibility = View.GONE
                        val errorMessage = result.message
                        if (errorMessage != null) {
                            val errorPosition =
                                "Error position: ${Thread.currentThread().stackTrace[2]}"
                            Log.e("API Error", "$errorPosition $errorMessage")
                        }
                        Toast.makeText(
                            this@ResultActivity,
                            "Terjadi kesalahan: $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun successPredict(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage("Hasil prediksi : $message")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    override fun onResume() {
        super.onResume()
//        if (isProcessing) {
//            Log.d("ResultActivity", "API processing in progress")
//            binding.pbLoading.visibility = View.VISIBLE
//        } else {
//            Log.d("ResultActivity", "API processing completed")
//            binding.pbLoading.visibility = View.GONE
//        }
    }


    private fun upload() {
        val file = getFile as File
        val name = binding.tvResultName as String
        val kalori = binding.tvResultKalori.toString()
        viewModel.upload(file, name, kalori).observe(this@ResultActivity) { result ->
            when(result) {
                is Result.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.pbLoading.visibility = View.GONE
                    val intent = Intent(this@ResultActivity, HomeActivity::class.java)
                    startActivity(intent)
                }
                is Result.Error -> {
                    binding.pbLoading.visibility = View.GONE
                    Toast.makeText(this, "Gagal mengunggah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupFragment() {
        val fragmentManager = supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
        val detailFragment = DetailFragment()
        fragmentTransaction.add(R.id.content, detailFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val EXTRA_SUCCESS_MESSAGE = "EXTRA_SUCCESS_MESSAGE"
    }
}