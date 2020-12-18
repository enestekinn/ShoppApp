
package com.example.shopapp.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.shopapp.R
import com.example.shopapp.firestore.FirestoreClass
import com.example.shopapp.models.User
import com.example.shopapp.utils.Constans
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() , View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN

            )

        }

        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)


    }

    fun userLoggedInSuccess(user : User) {

        hideProgressDialog()

        Log.i("First Name: ",user.firstName)
        Log.i("Last Name: ",user.lastName)
        Log.i("Email : ",user.email)

        if (user.profileCompleted == 0) { // kulanicinin bilgileri tam degilse

            val intent = Intent(this,UserProfileActivity::class.java)
            intent.putExtra(Constans.EXTRA_USER_DETAILS,user) //parceable uyguladik obje olarak gonderidik.
            startActivity(intent)
            finish()
        }else {

            startActivity(Intent(this,DashboardActivity::class.java))
            finish()
        }


    }

    override fun onClick(view: View?) {
        if (view!=null) {
           when (view.id){

               R.id.tv_forgot_password -> {

                   val intent = Intent(this,ForgotPasswordActivity::class.java)
                   startActivity(intent)
               }

               R.id.btn_login -> {
                   loginRegisteredUser()
               }

               R.id.tv_register -> {
                   val intent =Intent(this@LoginActivity,RegisterActivity::class.java)
startActivity(intent)
               }

           }

        }
    }

    private fun  validateLoginDetails() : Boolean {

        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            else -> {
                showErrorSnackBar("Your details are valid.",false)
                true
            }

        }
    }
    private  fun loginRegisteredUser() {

        if (validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))


            val email = et_email.text.toString().trim { it <= ' '}
            val password = et_password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->

                    //hideProgressDialog()

                    if (task.isSuccessful) {

                        FirestoreClass().getUserDetails(this)
                       // showErrorSnackBar("You are logged in successfully ",false)
                    }else {

                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }

                }


        }
    }
}