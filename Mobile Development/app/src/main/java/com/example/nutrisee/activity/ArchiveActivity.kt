package com.example.nutrisee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrisee.R
import com.example.nutrisee.ViewModelFactory
import com.example.nutrisee.adapter.ArchiveAdapter
import com.example.nutrisee.data.remote.response.ListImageResponse
import com.example.nutrisee.data.remote.response.ListImageResult
import com.example.nutrisee.databinding.ActivityArchiveBinding
import com.example.nutrisee.databinding.ActivityCameraBinding
import com.example.nutrisee.utils.Result
import com.example.nutrisee.viewmodel.ArchiveViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ArchiveActivity : AppCompatActivity(), ArchiveAdapter.OnItemClickAdapter {
    private lateinit var binding: ActivityArchiveBinding
    private lateinit var viewModel: ArchiveViewModel
    private lateinit var archiveAdapter: ArchiveAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArchiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        archiveAdapter = ArchiveAdapter(this, this)

        initViewModel()
        setupNavigation()
        getListImages()

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            getListImages()
        }
    }

    private fun getListImages() {
        binding.rvArchives.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ArchiveActivity)
            adapter = archiveAdapter
        }

        viewModel.apply {
            getListImages()
            responseListImage.observe(this@ArchiveActivity) { result ->
                when(result) {
                    is Result.Success -> {
                        val listImage = result.data?.listImageResult
                        if(listImage != null) {
                            archiveAdapter.setData(listImage)
                            binding.pbLoading.visibility = View.GONE
                            binding.swipeRefresh.isRefreshing = false
                        }
                    }
                    is Result.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                        binding.swipeRefresh.isRefreshing = true
                    }
                    is Result.Error -> {
                        binding.pbLoading.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        val errorMessage = result.message
                        Toast.makeText(this@ArchiveActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.e("TAG", "Error: $errorMessage")
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(application)
        )[ArchiveViewModel::class.java]
    }

    @Suppress("DEPRECATION")
    private fun setupNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_menu_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_camera -> {
                    val intent = Intent(this, CameraActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_archive -> {
                    onResume()
                    true
                }

                R.id.btn_log_out -> {
                    true
                }

                else -> false
            }
        }
    }

    override fun onItemClicked(listImage: ListImageResult, optionsCompat: ActivityOptionsCompat) {
        Toast.makeText(this@ArchiveActivity, "Fitur belum tersedia", Toast.LENGTH_SHORT).show()
    }
}