package com.example.favfooddish

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.example.favfooddish.databinding.ActivityMainBinding
import com.example.favfooddish.model.notification.NotifyWorker
import com.example.favfooddish.utils.Constant
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
         val navHostFragment =
             supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_all_dishes,R.id.navigation_favorite,R.id.navigation_random_dish
        ))
        navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController,appBarConfiguration)

        startWork()

        if(intent.hasExtra(Constant.NOTIFICATION_ID)){
            val notifiationId = intent.getIntExtra(Constant.NOTIFICATION_ID,0)
            Log.d("FavFoodDish", "$notifiationId")
            binding.navView.selectedItemId = R.id.navigation_random_dish
        }


    }

    private fun createConstraints(): Constraints {
        return Constraints.Builder()
              .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
              .setRequiresCharging(false)
              .setRequiresBatteryNotLow(true)
              .build()
    }

    private fun createWorkRequest(): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<NotifyWorker>(15, TimeUnit.MINUTES)
             .setConstraints(createConstraints())
             .build()
    }

    private fun startWork(){
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "FavDish Uniqueue Work",
            ExistingPeriodicWorkPolicy.KEEP,
            createWorkRequest()
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun hideBottomNavigationView(){
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(binding.navView.height.toFloat()).duration = 300
        binding.navView.visibility = View.GONE
    }

    fun showBottomNavigationView(){
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(0f).duration = 300
        binding.navView.visibility = View.VISIBLE
    }


}