package com.example.shopapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopapp.R
import com.example.shopapp.adapters.AddressListAdapter
import com.example.shopapp.firestore.FirestoreClass
import com.example.shopapp.models.Address
import com.example.shopapp.utils.Constans
import com.example.shopapp.utils.SwipeToDeleteCallback
import com.example.shopapp.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_settings.*

class AddressListActivity : BaseActivity() {

    private var mSelectAddress : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        setupActionBar()


        tv_add_address.setOnClickListener {
            val intent = Intent(this,AddEditAddressActivity::class.java)
            startActivityForResult(intent,Constans.ADD_ADDRESS_REQUEST_CODE)
        }
        if (intent.hasExtra(Constans.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constans.EXTRA_SELECT_ADDRESS,false)
        }

        if (mSelectAddress) {
            tv_title_address_list.text = resources.getString(R.string.title_select_address)
        }

        getAddressList()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getAddressList()
        }
    }

    private fun getAddressList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressesList(this)
    }
    private  fun setupActionBar() {
        setSupportActionBar(toolbar_address_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        //icona tiklayinca geri gidecek
        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun deleteAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(
                this,
                resources.getString(R.string.err_your_address_deleted_successfully),
                Toast.LENGTH_SHORT

                        ).show()
        getAddressList()
    }
    fun successAddressListFromFirestore(addressList : ArrayList<Address>) {

        hideProgressDialog()

        if (addressList.size > 0) {

            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this)
            rv_address_list.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this, addressList, mSelectAddress)
            rv_address_list.adapter = addressAdapter

            if (!mSelectAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rv_address_list.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                                this@AddressListActivity,
                                viewHolder.adapterPosition
                        )
                    }

                }


                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)

                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteAddress(this@AddressListActivity, addressList[viewHolder.adapterPosition].id)
                    }

                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)


            }


        }else {
                rv_address_list.visibility = View.GONE
                tv_no_address_found.visibility = View.VISIBLE
            }
            /*for (i in addressList) {
            Log.i("Name and Address","${i.name} :: ${i.address}")*/


        }


}