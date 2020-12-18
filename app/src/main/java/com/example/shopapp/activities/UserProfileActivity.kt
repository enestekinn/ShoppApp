package com.example.shopapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopapp.R
import com.example.shopapp.firestore.FirestoreClass
import com.example.shopapp.models.User
import com.example.shopapp.utils.Constans
import com.example.shopapp.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity() , View.OnClickListener {

  private lateinit var mUserDetails : User

    private var mSelectedImageFileUri : Uri? = null
    private var mUserProfileImageURL : String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)



        if (intent.hasExtra(Constans.EXTRA_USER_DETAILS)) {
            Log.e("HATA","okundu")
            mUserDetails = intent.getParcelableExtra(Constans.EXTRA_USER_DETAILS)!!
        }


        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)

        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)


        if (mUserDetails.profileCompleted == 0 ) {

            tv_title.text = resources.getString(R.string.title_complete_profile)

            et_first_name.isEnabled = false

            et_last_name.isEnabled = false




        }else {

            setupActionBar()

            tv_title.text =resources.getString(R.string.title_edit_profile)
            GlideLoader(this).loadUserPicture(mUserDetails.image,iv_user_photo)



            if (mUserDetails.mobile != 0L) {
                et_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constans.MALE) {
                rb_male.isChecked = true
            }else {
                rb_female.isChecked = true
            }
        }


        iv_user_photo.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED ) {

//showErrorSnackBar("You have already permission",true)
                        Constans.showImageChooser(this)


                    }else {
ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        Constans.READ_STORAGE_PERMISSION_CODE
)
                    }
                }

                R.id.btn_submit -> {


                    if (validateUserProfileDetails()) {
showProgressDialog(resources.getString(R.string.please_wait))


                        if (mSelectedImageFileUri != null)
                            FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri,Constans.USER_PROFILE_IMAGE)
                        else {

                        updateUserProfileDetails()
                        }



                    }
                }

            }
        }
    }

    private  fun updateUserProfileDetails() {


        showProgressDialog(resources.getString(R.string.please_wait))



        // showErrorSnackBar("Your details are valid . You can update them.",false)

        val userHasMap = HashMap<String,Any>()

        val firstName = et_first_name.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) { // first name ilkinden farkliysa kullanici degistirdiyse
            userHasMap[Constans.FIRST_NAME] = firstName
        }
        val lastName = et_last_name.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHasMap[Constans.LAST_NAME] = lastName
        }

        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

        val gender = if (rb_male.isChecked){
            Constans.MALE

        } else {
            Constans.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHasMap[Constans.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHasMap[Constans.MOBILE] = mobileNumber.toLong()
        }
        if (gender.isNotEmpty() && gender != mUserDetails.gender){
            userHasMap[Constans.GENDER] = gender
        }


        // key gender value male
        userHasMap[Constans.GENDER] = gender

        userHasMap[Constans.COMPLETE_PROFILE] = 1

      //  showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfileData(this,userHasMap)

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()


        Toast.makeText(
            this,resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()
startActivity(Intent(this,DashboardActivity::class.java))
        finish()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constans.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


               // showErrorSnackBar("Permission is granted",false)
                Constans.showImageChooser(this)
            }else {
                Toast.makeText(
                        this,
                        resources.getString(R.string.read_storage_permission_denied),
                        Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constans.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {

                        // the uri of selected image from phone storage
                          mSelectedImageFileUri = data.data!!

                    // iv_user_photo.setImageURI(Uri.parse(selectedImageFileUri.toString()))
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,iv_user_photo)
                    }catch (e : IOException) {

                        Toast.makeText(
                                this,
                                resources.getString(R.string.image_selection_failed),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    private fun validateUserProfileDetails() : Boolean {
        return  when {
            TextUtils.isEmpty(et_mobile_number.text.toString().trim {  it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
                false
            }else -> {
                true
            }
        }
    }

    fun imageUploadSuccess (imageURL : String) {
      //  hideProgressDialog()
        //Toast.makeText(this,"Your image is uploaded successfully. Image URL $imageURL",Toast.LENGTH_LONG).show()
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }


}