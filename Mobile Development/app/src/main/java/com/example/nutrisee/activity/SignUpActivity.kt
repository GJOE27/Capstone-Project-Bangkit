package com.example.nutrisee.activity

import android.content.ContentResolver
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nutrisee.MainActivity
import com.example.nutrisee.R
import com.example.nutrisee.ViewModelFactory
import com.example.nutrisee.databinding.ActivitySignUpBinding
import com.example.nutrisee.utils.Helper
import com.example.nutrisee.utils.Message
import com.example.nutrisee.utils.Result
import com.example.nutrisee.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalGetImage class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =
            ViewModelProvider(
                this, ViewModelFactory(application)
            )[SignUpViewModel::class.java]

        binding.btnSignUp.setOnClickListener {
            binding.apply {
                val email = edtEmail.text.toString().trim()
                val password = edtPassword.text.toString().trim()

//                val drawable = R.drawable.baseline_image_24
//                val drawableUri = Uri.parse(
//                    ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
//                            resources.getResourcePackageName(drawable) + "/" +
//                            resources.getResourceTypeName(drawable) + "/" +
//                            resources.getResourceEntryName(drawable)
//                )
//
//                val file = Helper.uriToFile(drawableUri, root.context)
//                val displayName = "User"

                viewModel.signUpUser(email, password)
                    .observe(this@SignUpActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                binding.apply {
                                    pbLoading.visibility = View.VISIBLE
                                    tvLoading.visibility = View.VISIBLE
                                    btnSignUp.visibility = View.GONE
                                    edtEmail.visibility = View.GONE
                                    edtPassword.visibility = View.GONE
                                }
                            }

                            is Result.Success -> {
                                binding.apply {
                                    pbLoading.visibility = View.GONE
                                    tvLoading.visibility = View.GONE
                                    btnSignUp.visibility = View.VISIBLE
                                    edtEmail.visibility = View.VISIBLE
                                    edtPassword.visibility = View.VISIBLE
                                }
                                val intent =
                                    Intent(this@SignUpActivity, HomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }

                            is Result.Error -> {
                                binding.apply {
                                    pbLoading.visibility = View.GONE
                                    tvLoading.visibility = View.GONE
                                    btnSignUp.visibility = View.VISIBLE
                                    edtEmail.visibility = View.VISIBLE
                                    edtPassword.visibility = View.VISIBLE
                                }
                                val errorMessage = result.message ?: "Gagal melakukan Sign Up"
                                Log.e("SignUpActivity", errorMessage)
                                Toast.makeText(
                                    applicationContext,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {}
                        }
                    }
            }
        }
    }
}