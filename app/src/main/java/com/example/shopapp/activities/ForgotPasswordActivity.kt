package com.example.shopapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.shopapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_register.*

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setupActionBar()

        

        btn_submit.setOnClickListener {
            val email : String = et_forgot_email.text.toString().trim { it <=  ' ' }
            if (email.isEmpty()) {
                showErrorSnackBar("Please enter an e-mail",true)
            }else {
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                Toast.makeText(this,resources.getString(R.string.email_sent_success),Toast.LENGTH_LONG).show()

                                finish()
                            }else {
                                showErrorSnackBar(task.exception!!.message.toString(),true)
                            }
                        }
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_forgot_password_activity)

        val actionBar = supportActionBar

        if (actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }

        toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }
    }
}