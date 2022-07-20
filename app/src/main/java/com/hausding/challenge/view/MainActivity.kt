package com.hausding.challenge.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hausding.challenge.R
import com.hausding.challenge.databinding.ActivityMainBinding
import com.hausding.challenge.model.service.DataService
import com.hausding.challenge.model.service.ServiceAction

/**
 * Main activity of this app
 * @property binding View binding for this activity
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkForPermissions()
    }

    /**
     * Function for required permissions check and request
     */
    private fun checkForPermissions() {
        val missingPermissions = mutableListOf(Manifest.permission.INTERNET,Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.WAKE_LOCK)
        missingPermissions.filter { checkForPermissions(it) }
        if (missingPermissions.isEmpty()) {
            startDataService()
        } else{
            val permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                if (result.values.any { !it }) {
                    Toast.makeText(this, R.string.toast_error_permissions, Toast.LENGTH_SHORT).show()
                } else {
                    startDataService()
                }
            }
            permissionsLauncher.launch(missingPermissions.toTypedArray())
        }

    }

    /**
     * Function for checking whether permission has been granted
     * @param permission String with permission
     */
    private fun checkForPermissions(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Function for starting data service
     */
    private fun startDataService() {
        Intent(this, DataService::class.java).also {
            it.action = ServiceAction.START.name
            startForegroundService(it)
        }
    }
}