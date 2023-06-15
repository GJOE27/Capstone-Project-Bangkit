package com.example.nutrisee.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.ExperimentalGetImage
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrisee.R
import com.example.nutrisee.ViewModelFactory
import com.example.nutrisee.adapter.ListFeedAdapter
import com.example.nutrisee.data.local.Feed
import com.example.nutrisee.databinding.ActivityHomeBinding
import com.example.nutrisee.viewmodel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

@ExperimentalGetImage class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    private lateinit var rvFeeds: RecyclerView
    private val list = ArrayList<Feed>()

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvFeeds = findViewById(R.id.rv_feeds)
        rvFeeds.setHasFixedSize(true)

        list.addAll(getListFeeds())
        showRecyclerList()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupAction()
        initViewModel()
        setDrawer()
        setupNavigation()

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            getListFeeds()
        }
    }

    @SuppressLint("Recycle")
    private fun getListFeeds(): ArrayList<Feed> {
        val imgProfile = resources.obtainTypedArray(R.array.data_photo)
        val nameProfile = resources.getStringArray(R.array.feeds_person)
        val caption = resources.getStringArray(R.array.feeds_caption)
        val imgFeeds = resources.obtainTypedArray(R.array.feeds_photo)
        val date = resources.getStringArray(R.array.date)
        val listFeed = ArrayList<Feed>()
        for(i in nameProfile.indices) {
            val feed = Feed(imgProfile.getResourceId(i, -1), nameProfile[i], caption[i], imgFeeds.getResourceId(i, -1), date[i])
            listFeed.add(feed)
        }
        binding.swipeRefresh.isRefreshing = false
        return listFeed
    }

    private fun showRecyclerList() {
        rvFeeds.layoutManager = LinearLayoutManager(this)
        val listFeedAdapter = ListFeedAdapter(list)
        rvFeeds.adapter = listFeedAdapter
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setupAction() {
        binding.drawerHome.apply {
            btnLogOut.setOnClickListener {
                AlertDialog.Builder(this@HomeActivity).apply {
                    setTitle(R.string.log_out)
                    setMessage(R.string.logout_confirmation)
                    setPositiveButton(R.string.yes) { _, _ ->
                        viewModel.logout()
                        val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                        finish()
                    }
                    setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }
                }.show()
            }
            btnSettings.setOnClickListener {
                val intent = Intent(this@HomeActivity, SettingActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(application)
        )[HomeViewModel::class.java]
    }

    private fun setDrawer() {
        val drawerLayout = binding.drawerLayout
        val btnToggleDrawer = binding.btnToggleDrawer

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        btnToggleDrawer.setOnClickListener {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
                val email = viewModel.getEmail()
                binding.drawerHome.tvProfileEmail.text = email
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setupNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_menu_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    onResume()
                    true
                }

                R.id.navigation_camera -> {
                    val intent = Intent(this, CameraActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_archive -> {
                    val intent = Intent(this, ArchiveActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.btn_log_out -> {
                    true
                }

                else -> false
            }
        }
    }
}