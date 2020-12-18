package com.example.shopapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.R
import com.example.shopapp.adapters.MyOrdersListAdapter
import com.example.shopapp.firestore.FirestoreClass
import com.example.shopapp.models.Order
import kotlinx.android.synthetic.main.orders_fragment.*


class OrdersFragment : BaseFragment() {


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.orders_fragment, container, false)
        return root
    }


    fun populateOrdersListInUI(ordersList: ArrayList<Order>) {
        hideProgressDialog()

        if (ordersList.size >0) {
            rv_my_order_items.visibility = View.VISIBLE
            tv_no_orders_found.visibility = View.GONE

            rv_my_order_items.layoutManager = LinearLayoutManager(activity)
            rv_my_order_items.setHasFixedSize(true)

            val myOrderAdapter = MyOrdersListAdapter(requireActivity() , ordersList)
            rv_my_order_items.adapter = myOrderAdapter

            val myOrdersListAdapter = MyOrdersListAdapter(requireActivity() , ordersList)
            rv_my_order_items.adapter = myOrdersListAdapter
        }else {
            rv_my_order_items.visibility = View.GONE
            tv_no_orders_found.visibility = View.VISIBLE
        }

    }

    fun getMyOrdersList() {

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMyOderList(this)

    }

    override fun onResume() {
super.onResume()
        getMyOrdersList()
    }

}