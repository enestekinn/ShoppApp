package com.example.shopapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.shopapp.R
import com.example.shopapp.utils.Constans
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {


    private var mSelectedImageFileUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


/*        val sharedPreferences =
                getSharedPreferences(Constans.MYSHOPPAL_PREGERENCES,Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constans.LOGGED_IN_USERNAME,"")
        tv_main.text = "Hello $username"
        tv_main.setTextSize(60f)*/

        btn_select_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(galleryIntent,121)

                //Select image
            }else {
                //Request Permission
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),121)

            }
        }

        btn_upload_image.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                // veirnin hangi turde oldugunu anliyoruz jpeg mp4 gibi
                val imageExtension = MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(contentResolver.getType(mSelectedImageFileUri!!))

                val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                        "Image" + System.currentTimeMillis() + "." + imageExtension)

                sRef.putFile(mSelectedImageFileUri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.metadata!!.reference!!.downloadUrl
                                    .addOnSuccessListener { url ->
                                    tv_image_upload_success.text = "Your image was uploaded successfully :: $url"

                                        Glide.with(this).load(url)
                                                .placeholder(R.mipmap.ic_launcher)
                                                .into(image_view)

                                    }.addOnFailureListener { exception ->
                                        Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
                                        Log.e(javaClass.simpleName,exception.message,exception)
                                    }

                        }

            }else {
                Toast.makeText(this,
                "Please select the image to upload",
                Toast.LENGTH_LONG)
                        .show()
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 121) {
            val intentGallery =Intent (
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
            startActivityForResult(intentGallery,121)
        }else {
            Toast.makeText(this,"You just denied the permission for storage",Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK ) {
            if (requestCode == 121) {
                if (data != null) {
                    try {

                        mSelectedImageFileUri = data.data

                        //glide ile yapiyoruz
                        //image_view.setImageURI(mSelectedImageFileUri)

                        Glide.with(this).load(mSelectedImageFileUri)
                                .into(image_view)



                    }catch (e : IOException) {
                        e.printStackTrace()
                        Toast.makeText(this,"Image selection failed",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}