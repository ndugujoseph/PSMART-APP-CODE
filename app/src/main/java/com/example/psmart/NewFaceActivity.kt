package com.example.psmart

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class NewFaceActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION = 100
    private var tv: TextView? = null
    private var progressBar: ProgressBar? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_face)


        tv = findViewById(R.id.message)
        progressBar = findViewById(R.id.progressBar)

        val btCapturePhoto = findViewById<Button>(R.id.btCapturePhoto)
        btCapturePhoto.setOnClickListener {
            openCamera()
        }

        val btOpenGallery = findViewById<Button>(R.id.btOpenGallery)
        btOpenGallery.setOnClickListener {
            openGallery()
        }
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION)
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_PICK_IMAGE)
            }
        }
    }

    private fun saveImage(finalBitmap: Bitmap) {
        val root = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString()
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-$n.png"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        // Tell the media scanner about the new file so that it is
       // immediately available to the user.
        MediaScannerConnection.scanFile(
            this, arrayOf(file.toString()), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val bitmap = data?.extras?.get("data") as Bitmap
                val ivImage = findViewById<ImageView>(R.id.ivImage)
                ivImage.setImageBitmap(bitmap)

                val con = findViewById<Button>(R.id.con_face)
                con.setOnClickListener {
                    saveImage(bitmap)
                    uploadImageUsingRetrofit(bitmap)
//                    val intent = Intent(applicationContext, RegisterHomeStudent::class.java)
//                    startActivity(intent)
                }
            }

            else if (requestCode == REQUEST_PICK_IMAGE) {
                val uri = data?.data
                val ivImage = findViewById<ImageView>(R.id.ivImage)
                ivImage.setImageURI(uri)
            }
        }
    }


    private fun uploadImageUsingRetrofit(bitmap: Bitmap) {

        val intent = intent
        val extras = intent.extras

        val role = extras!!.getString("Role")
        val id = extras.getString("ID")

        progressBar?.visibility = View.VISIBLE
        tv?.text = ""
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        val name = Calendar.getInstance().timeInMillis.toString()
        val retrofit = Retrofit.Builder()
            .baseUrl(MyImageInterface.IMAGEURL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        val myImageInterface = retrofit.create(MyImageInterface::class.java)
        val call = myImageInterface.getImageData(name, image, id, role)
        call.enqueue(object : Callback<String?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                progressBar?.visibility = View.GONE
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Toast.makeText(
                            this@NewFaceActivity,
                            "Saved Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        tv?.text = "Saved Successfully"
                        tv?.setTextColor(Color.parseColor("#008000"))
                    } else {
                        tv?.text = "No response from the server"
                        tv?.setTextColor(Color.parseColor("#FF0000"))
                    }
                } else {
                    tv?.text = "Response not successful $response"
                    tv?.setTextColor(Color.parseColor("#FF0000"))
                    Toast.makeText(
                        applicationContext,
                        "Response not successful $response",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<String?>, t: Throwable) {
                progressBar?.visibility = View.GONE
                Toast.makeText(applicationContext, "Error occurred!", Toast.LENGTH_SHORT).show()
                tv?.text = "Error occurred during upload"
                tv?.setTextColor(Color.parseColor("#FF0000"))
            }
        })
    }


}