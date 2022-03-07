package com.example.psmart.ui.login


import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.psmart.MainActivity
import com.example.psmart.R
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)

       val login = findViewById<Button>(R.id.buttonLogin)


        //if user presses on login
        //calling the method login
       login.setOnClickListener { userLogin() }

        //if user presses on forgot password
        findViewById<TextView>(R.id.forgot_password).setOnClickListener {
            startActivity(Intent(applicationContext,ForgotPasswordActivity::class.java))
        }
    }

    private fun userLogin() {
        //first getting the values
        val email: String = editTextEmail!!.text.toString()
        val password: String = editTextPassword!!.text.toString()

        //validating inputs
        if (TextUtils.isEmpty(email)) {
            editTextEmail!!.error = "Please enter your email"
            editTextEmail!!.requestFocus()
            return
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword!!.error = "Please enter your password"
            editTextPassword!!.requestFocus()
            return
        }

        //if everything is fine
        class UserLogin :
            AsyncTask<Void?, Void?, String?>() {
            @SuppressLint("StaticFieldLeak")
            var progressBar: ProgressBar? = null
            override fun onPreExecute() {
                super.onPreExecute()
                progressBar = findViewById(R.id.progressBar)
                progressBar!!.visibility = View.VISIBLE
            }

            override fun onPostExecute(s: String?) {
                super.onPostExecute(s)
                progressBar!!.visibility = View.GONE
                try {
                    //converting response to json object
                    val obj = JSONObject(s)

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(
                            applicationContext,
                            obj.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()


                        //getting the user from the response
                        val userJson = obj.getJSONObject("user")
                        finish()
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        //creating a new user object
                        User(
                            userJson.getInt("id"),
                            userJson.getString("email"),
                            userJson.getString("name"),
                        )

                        //storing the user in shared preferences
                       //  SharedPrefManager.getInstance(applicationContext).userLogin(user)

                        //starting the main activity

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Invalid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun doInBackground(vararg p0: Void?): String {
                //creating request handler object
                val requestHandler = RequestHandler()

                //creating request parameters
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params)
            }
        }

        val ul = UserLogin()
        ul.execute()
    }
}