package com.example.shopapp.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle

import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.R
import com.example.shopapp.activities.AddProductActivity
import com.example.shopapp.adapters.MyProductsListAdapter
import com.example.shopapp.firestore.FirestoreClass
import com.example.shopapp.models.Product
import kotlinx.android.synthetic.main.product_fragment.*


class ProductFragment : BaseFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun deleteProduct(productID : String) {

        showAlertDialogToDeleteProduct(productID)
     /*   Toast.makeText(
                requireActivity(),
                "You can now delete the product. $productID",
                Toast.LENGTH_SHORT
        ).show()*/
    }

    fun productDeleteSuccess() {
        hideProgressDialog()

        Toast.makeText(requireActivity(),
        resources.getString(R.string.product_delete_success_message),
        Toast.LENGTH_LONG
        ).show()
        getProductListFromFireStore()
    }
    private fun showAlertDialogToDeleteProduct(productID : String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteProduct(this,productID)
        dialogInterface.dismiss()

        }

        builder.setNegativeButton(resources.getString(R.string.no)) {dialogInterface, _  ->
            dialogInterface.dismiss()
        }

        val alertDialog :AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    fun successProductListFromFireStrore(productsList : ArrayList<Product>) {
        hideProgressDialog()

        if (productsList.size > 0) {
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)

            val adapterProducts = MyProductsListAdapter(requireActivity(),
                productsList,this)
            rv_my_product_items.adapter=adapterProducts

        }else {
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
/*
        for (i in productsList) {
            Log.i("Product " ,i.title)
        }*/
    }

    private fun getProductListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductList(this)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_fragment, container, false)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when(id) {
            R.id.action_add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

}