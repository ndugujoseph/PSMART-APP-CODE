package com.example.psmart

 import android.annotation.SuppressLint
 import android.os.Bundle
 import android.view.Menu
 import android.view.MenuItem
 import android.view.View
 import android.widget.TextView
 import androidx.appcompat.app.AppCompatActivity
 import androidx.drawerlayout.widget.DrawerLayout
 import androidx.navigation.findNavController
 import androidx.navigation.ui.AppBarConfiguration
 import androidx.navigation.ui.navigateUp
 import androidx.navigation.ui.setupActionBarWithNavController
 import androidx.navigation.ui.setupWithNavController
 import com.example.psmart.databinding.ActivityMainBinding
 import com.example.psmart.ui.login.SharedPrefManager
 import com.example.psmart.ui.login.User
 import com.google.android.material.navigation.NavigationView
 import java.text.DateFormat
 import java.text.SimpleDateFormat
 import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SimpleDateFormat", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //if the user is not logged in
        //starting the login activity
//        if (!SharedPrefManager.getInstance(this).isLoggedIn) {
//            finish()
//            startActivity(Intent(this, LoginActivity::class.java))
//        }

        //getting the current user
     //   val user = SharedPrefManager.getInstance(this).user


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

//        binding.appBarMain.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_reports, R.id.nav_account
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        //getting current date
        val navigationView : NavigationView  = findViewById(R.id.nav_view)
        val headerView : View = navigationView.getHeaderView(0)
        val date : TextView = headerView.findViewById(R.id.text_view_date)
        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy, HH:mm")
        val dateToday: String = df.format(Calendar.getInstance().time)
        date.text = dateToday


        //getting school name
        val school : TextView = headerView.findViewById(R.id.text_view_school_name)
        school.text = User.getName()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //when the user presses logout button
    //calling the logout method
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                SharedPrefManager.getInstance(applicationContext).logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}