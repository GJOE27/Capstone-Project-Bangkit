package com.example.nutrisee.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.example.nutrisee.databinding.DrawerHomeBinding
import com.example.nutrisee.viewmodel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

@ExperimentalGetImage class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
//    private lateinit var bindingDrawer: DrawerHomeBinding
    private lateinit var viewModel: HomeViewModel

    private lateinit var rvFeeds: RecyclerView
    private val list = ArrayList<Feed>()

    companion object {
        const val CAMERA_X_RESULT = 200
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
//        bindingDrawer = DrawerHomeBinding.inflate(layoutInflater)
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
    }

    private fun getListFeeds(): ArrayList<Feed> {
        val imgProfile = R.drawable.baseline_account_24
        val nameProfile = resources.getStringArray(R.array.feeds_person)
        val caption = resources.getStringArray(R.array.feeds_caption)
        val imgFeeds = resources.obtainTypedArray(R.array.feeds_photo)
        val date = "Selasa 14 Juni 2023"
        val listFeed = ArrayList<Feed>()
        for(i in nameProfile.indices) {
            val feed = Feed(imgProfile, nameProfile[i], caption[i], imgFeeds.getResourceId(i, -1), date)
            listFeed.add(feed)
        }
        return listFeed
    }

    private fun showRecyclerList() {
        rvFeeds.layoutManager = LinearLayoutManager(this)
        val listFeedAdapter = ListFeedAdapter(list)
        rvFeeds.adapter = listFeedAdapter
    }

    private fun setupAction() {
        binding.drawerHome.apply {
            btnLogOut.setOnClickListener {
                AlertDialog.Builder(this@HomeActivity).apply {
                    setTitle(R.string.log_out)
                    setMessage("Are you sure want to log out?")
                    setPositiveButton("Yes") { _, _ ->
                        viewModel.logout()
                        val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                        finish()
                    }
                }.show()
            }
            btnSettings.setOnClickListener {
                Toast.makeText(this@HomeActivity, "Ini button setting", Toast.LENGTH_SHORT).show()
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
                binding.drawerHome.tvProfileName.text = "User"
            }
        }
        binding.drawerHome.btnSettings.setOnClickListener {
            Log.d("HomeActivity", "Button Settings clicked")
//            Toast.makeText(this@HomeActivity, "Ini button setting", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
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