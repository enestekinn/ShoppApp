package com.example.shopapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.R
import com.example.shopapp.adapters.CartItemsListAdapter
import com.example.shopapp.firestore.FirestoreClass
import com.example.shopapp.models.Address
import com.example.shopapp.models.CartItem
import com.example.shopapp.models.Order
import com.example.shopapp.models.Product
import com.example.shopapp.utils.Constans
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : BaseActivity() {

    private  var mAddressDetails : Address? = null
    private  lateinit var  mProductLst : ArrayList<Product>
    private lateinit var  mCartItemList : ArrayList<CartItem>

    private var mSubTotal : Double = 0.0

    private var mTotalAmount : Double = 0.0

    private lateinit var  mOrderDetails : Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setupActionBar()

        if (intent.hasExtra(Constans.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra(Constans.EXTRA_SELECTED_ADDRESS)
        }

        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text =mAddressDetails?.otherDetails
            }
            
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
            
        }

        getProductList()

        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }

    fun allDetailsUpdatedSuccess() {
        hideProgressDialog()
        Toast.makeText(this,"Your order was placed successfully",Toast.LENGTH_LONG).show()

        val intent = Intent(this,DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    fun orderPlacedSuccess() {

        FirestoreClass().updateAllDetails(this,mCartItemList,mOrderDetails)
    }

    fun successProductListFromFireStore(productList : ArrayList<Product>) {

        mProductLst = productList
        getCartItemsList()


    }

    private  fun getCartItemsList() {
        FirestoreClass().getCartList(this)
    }

    private fun placeAnOrder() {
        showProgressDialog(resources.getString(R.string.please_wait))

        mOrderDetails = Order (
                FirestoreClass().getCurrentUserID(),
                mCartItemList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemList[0].image,
                mSubTotal.toString(),
                "10.0",

            mTotalAmount.toString(),
            System.currentTimeMillis()
                )

        FirestoreClass().placeOrder(this,mOrderDetails)


    }

    fun successCartItemList(cartList : ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProductLst) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity

                }
            }
        }


        mCartItemList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this,mCartItemList,false)
        rv_cart_list_items.adapter = cartListAdapter

        for (item in mCartItemList) {
            val availableQuantity =item.stock_quantity.toInt()
            if (availableQuantity > 0 ) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price  * quantity )


            }
        }

        tv_checkout_sub_total.text = "$$mSubTotal"
        tv_checkout_shipping_charge.text = "$10.0"

        if (mSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE
            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text = "$$mTotalAmount"
        }


    }
    private  fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this)
    }

    private  fun setupActionBar() {
        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        //icona tiklayinca geri gidecek
        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }

}