package com.example.shopapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.R
import com.example.shopapp.adapters.SoldProductsListAdapter
import com.example.shopapp.firestore.FirestoreClass
import com.example.shopapp.models.SoldProduct
import kotlinx.android.synthetic.main.fragment_sold_product.*


class SoldProductFragment : BaseFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        getSoldProductList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sold_product, container, false)
    }

    private  fun getSoldProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getSoldProductLst(this)

    }

    fun successSoldProductsList(soldProductsList : ArrayList<SoldProduct>) {
        hideProgressDialog()
        if (soldProductsList.size > 0) {
            rv_sold_product_items.visibility = View.VISIBLE
            tv_no_sold_products_found.visibility = View.GONE

            rv_sold_product_items.layoutManager = LinearLayoutManager(activity)
            rv_sold_product_items.setHasFixedSize(true)

            val  soldProductsListAdapter =
                    SoldProductsListAdapter(requireActivity(), soldProductsList)
            rv_sold_product_items.adapter  = soldProductsListAdapter

        }else {
            rv_sold_product_items.visibility = View.GONE
            tv_no_sold_products_found.visibility = View.VISIBLE

        }
    }


}