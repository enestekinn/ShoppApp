package com.example.shopapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopapp.R
import com.example.shopapp.activities.ProductDetailActivity
import com.example.shopapp.models.Product
import com.example.shopapp.utils.Constans
import com.example.shopapp.utils.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class DashboardItemListsAdapter (
        private val context: Context,
        private var list : ArrayList<Product>
        ) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private  var onClickListener : OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_dashboard_layout,
                        parent,
                        false
                )
        )
    }

    fun setOnClickListener(onClickListener : OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val model = list[position]

        GlideLoader(context)
                .loadUserPicture(
                        model.image,
                        holder.itemView.iv_dashboard_item_image
                )
        holder.itemView.tv_dashboard_item_title.text = model.description
        holder.itemView.tv_dashboard_item_price.text = model.title
        holder.itemView.tv_dashboard_item_price.text = "$${model.price}"
        holder.itemView.setOnClickListener {

            val intent = Intent(context,ProductDetailActivity::class.java)
            intent.putExtra(Constans.EXTRA_PRODUCT_ID,model.product_id)
            intent.putExtra(Constans.EXTRA_PRODUCT_OWNER_ID,model.user_id)
            context.startActivity(intent)
        }


        /*holder.itemView.setOnClickListener {
            if (onClickListener !=null) {
                onClickListener!!.onClick(position,model)
            }
        }*/

    }


    override fun getItemCount(): Int {
return list.size
    }

class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)

    interface  OnClickListener {

        fun onClick(position: Int, product: Product)
    }
}