package com.blackbox.callrecorder

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.blackbox.callrecorder.databinding.ActivityMainBinding
import com.blackbox.callrecorder.utils.Constants
import com.blackbox.callrecorder.utils.PermissionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHost.navController)

        showFirstLaunchDisclosureIfNeeded()

        if (!PermissionManager.hasAllRequiredPermissions(this)) {
            permissionLauncher.launch(PermissionManager.requiredPermissions())
        }
    }

    private fun showFirstLaunchDisclosureIfNeeded() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        if (prefs.getBoolean(Constants.KEY_DISCLOSURE_ACCEPTED, false)) return

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.privacy_disclosure_title))
            .setMessage(getString(R.string.privacy_disclosure_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.accept)) { _, _ ->
                prefs.edit().putBoolean(Constants.KEY_DISCLOSURE_ACCEPTED, true).apply()
            }
            .setNegativeButton(getString(R.string.exit)) { _, _ -> finish() }
            .show()
    }
}
