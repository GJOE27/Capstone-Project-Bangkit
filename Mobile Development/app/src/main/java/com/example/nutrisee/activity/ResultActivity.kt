package com.example.nutrisee.activity

import android.annotation.SuppressLint
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
import com.example.nutrisee.R
import com.example.nutrisee.ViewModelFactory
import com.example.nutrisee.activity.CameraActivity.Companion.EXTRA_PICTURE
import com.example.nutrisee.activity.CameraActivity.Companion.URI_IMAGE
import com.example.nutrisee.databinding.ActivityResultBinding
import com.example.nutrisee.utils.Helper.Companion.rotateFile
import com.example.nutrisee.utils.Helper.Companion.uriToFile
import com.example.nutrisee.utils.Result
import com.example.nutrisee.viewmodel.PredictViewModel
import java.io.File

@ExperimentalGetImage
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: PredictViewModel

    private var number: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        putImage()
        setupButton()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(application)
        )[PredictViewModel::class.java]
    }

    private fun setupButton() {
        binding.apply {
            btnBack.setOnClickListener {
                val intent = Intent(this@ResultActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
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
                        setupVisibility(false)
                    }

                    is Result.Success -> {
                        val response = result.data
                        val name = response?.name
                        val kalori = response?.kalori
                        val formattedName =
                            name?.replace("_", " ")?.replaceFirstChar { it.uppercase() }
                        binding.tvResultName.text = formattedName
                        binding.tvResultKalori.text = "$kalori ${getString(R.string.calory)}"
                        binding.apply {
                            setupVisibility(true)
                            btnPlus.setOnClickListener {
                                number++
                                binding.numberServing.text = number.toString()
                                val amount = kalori?.times(number)
                                binding.tvResultKalori.text = "$amount ${getString(R.string.calory)}"
                            }
                            btnMinus.setOnClickListener {
                                if (number > 1) {
                                    number--
                                    binding.numberServing.text = number.toString()
                                    val amount = kalori?.times(number)
                                    binding.tvResultKalori.text = "$amount ${getString(R.string.calory)}"
                                } else {
                                    Toast.makeText(
                                        this@ResultActivity,
                                        getString(R.string.cant_less),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            btnUpload.text = getString(R.string.close)
                            btnUpload.setOnClickListener {
                                val intent = Intent(this@ResultActivity, ArchiveActivity::class.java)
                                startActivity(intent)
                            }
                        }

                        if (formattedName != null) {
                            successPredict(formattedName)
                        }
                    }

                    is Result.Error -> {
                        binding.apply {
                            pbLoading.visibility = View.GONE
                            btnPlus.visibility = View.GONE
                            btnMinus.visibility = View.GONE
                            tvServing.visibility = View.GONE
                            numberServing.visibility = View.GONE
                        }
                        val response = result.data
                        val error = result.message
                        val errorMessage = result.data?.message
                        val name = response?.name
                        if (errorMessage != null) {
                            val errorPosition =
                                "Error position: ${Thread.currentThread().stackTrace[2]}"
                            Log.e("API Error", "$errorPosition $errorMessage")
                        }
                        Log.e("TAG", "Error : $errorMessage")
                        binding.tvResultName.text = "[$error]\n${getString(R.string.try_again)}"
                        binding.btnUpload.text = getString(R.string.close)
                        binding.btnUpload.setOnClickListener {
                            val intent = Intent(this@ResultActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }
                            if (name != null) {
                                successPredict(name)
                            }
                        Toast.makeText(
                            this@ResultActivity,
                            "${getString(R.string.something_wrong)} $error",
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
                        setupVisibility(false)
                    }

                    is Result.Success -> {
                        val response = result.data
                        val name = response?.name
                        val kalori = response?.kalori
                        val formattedName =
                            name?.replace("_", " ")?.replaceFirstChar { it.uppercase() }
                        binding.tvResultName.text = formattedName
                        binding.tvResultKalori.text = "$kalori ${getString(R.string.calory)}"
                        binding.apply {
                            setupVisibility(true)
                            btnPlus.setOnClickListener {
                                number++
                                binding.numberServing.text = number.toString()
                                val amount = kalori?.times(number)
                                binding.tvResultKalori.text = "$amount ${getString(R.string.calory)}"
                            }
                            btnMinus.setOnClickListener {
                                if (number > 1) {
                                    number--
                                    binding.numberServing.text = number.toString()
                                    val amount = kalori?.times(number)
                                    binding.tvResultKalori.text = "$amount ${getString(R.string.calory)}"
                                } else {
                                    Toast.makeText(
                                        this@ResultActivity,
                                        getString(R.string.cant_less),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            btnUpload.text = getString(R.string.close)
                            btnUpload.setOnClickListener {
                                val intent = Intent(this@ResultActivity, ArchiveActivity::class.java)
                                startActivity(intent)
                            }
                        }

                        if (formattedName != null) {
                            successPredict(formattedName)
                        }
                    }

                    is Result.Error -> {
                        binding.apply {
                            pbLoading.visibility = View.GONE
                            btnPlus.visibility = View.GONE
                            btnMinus.visibility = View.GONE
                            tvServing.visibility = View.GONE
                            numberServing.visibility = View.GONE
                        }
                        val response = result.data
                        val error = result.message
                        val errorMessage = result.data?.message
                        val name = response?.name
                        if (errorMessage != null) {
                            val errorPosition =
                                "Error position: ${Thread.currentThread().stackTrace[2]}"
                            Log.e("API Error", "$errorPosition $errorMessage")
                        }
                        Log.e("TAG", "Error : $errorMessage")
                        binding.tvResultName.text = "[$error]\n${getString(R.string.try_again)}"
                        binding.btnUpload.text = getString(R.string.close)
                        binding.btnUpload.setOnClickListener {
                            val intent = Intent(this@ResultActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        if (name != null) {
                            successPredict(name)
                        }
                        Toast.makeText(
                            this@ResultActivity,
                            "${getString(R.string.something_wrong)}: $error",
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
            .setTitle(getString(R.string.success))
            .setMessage("${getString(R.string.predict_result)} : $message")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun setupVisibility(isBoolean: Boolean) {
        if(isBoolean) {
            binding.apply {
                pbLoading.visibility = View.GONE
                btnPlus.visibility = View.VISIBLE
                btnMinus.visibility = View.VISIBLE
                tvServing.visibility = View.VISIBLE
                numberServing.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                pbLoading.visibility = View.VISIBLE
                btnPlus.visibility = View.GONE
                btnMinus.visibility = View.GONE
                tvServing.visibility = View.GONE
                numberServing.visibility = View.GONE
            }
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val EXTRA_SUCCESS_MESSAGE = "EXTRA_SUCCESS_MESSAGE"
    }
}