package com.example.shopapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.shopapp.activities.*
import com.example.shopapp.fragments.DashboardFragment
import com.example.shopapp.fragments.OrdersFragment
import com.example.shopapp.fragments.ProductFragment
import com.example.shopapp.fragments.SoldProductFragment
import com.example.shopapp.models.*
import com.example.shopapp.utils.Constans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class FirestoreClass {


    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constans.USERS).document(userInfo.id).set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.userRegistrationSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while registering the user"
                    )
                }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Constans.USERS).document(getCurrentUserID()).get()
                .addOnSuccessListener { document ->

                    Log.i(activity.javaClass.simpleName, document.toString())

                    val user = document.toObject(User::class.java)

                    val sharedPreferences =
                            activity.getSharedPreferences(Constans.MYSHOPPAL_PREGERENCES,
                                    Context.MODE_PRIVATE
                            )
                    // saving firstname and lastname
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(
                            Constans.LOGGED_IN_USERNAME,
                            "${user?.firstName} ${user?.lastName}"
                    )
                    editor.apply()

                    when (activity) {
                        is LoginActivity -> {
                            activity.userLoggedInSuccess(user!!)
                        }
                        is SettingsActivity -> {

                            activity.userDetailsSuccess(user!!)

                        }
                    }


                }
                .addOnFailureListener { e ->

                }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFireStore.collection(Constans.ADDRESSES)
                .document(addressId)
                .delete()
                .addOnSuccessListener {

                    activity.deleteAddressSuccess()
                }.addOnFailureListener { e ->


                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while deleting the address. ",
                            e
                    )
                }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constans.USERS)
                .document(getCurrentUserID())
                .update(userHashMap)
                .addOnSuccessListener {

                    when (activity) {
                        is UserProfileActivity -> {
                            activity.userProfileUpdateSuccess()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                        is SettingsActivity -> {
                            activity.hideProgressDialog()

                        }
                    }

                    Log.e(activity.javaClass.simpleName, "Error while updating the user details")
                }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                imageType + System.currentTimeMillis() + "." + Constans.getFileExtension(
                        activity,
                        imageFileURI
                )
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            Log.e("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL ", uri.toString())
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
        }
                .addOnFailureListener { exception ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                        is AddProductActivity -> {
                            activity.hideProgressDialog()
                        }

                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            exception.message,
                            exception
                    )

                }


    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        mFireStore.collection(Constans.PRODUCTS)
                .document()
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.productUploadSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while uploading the product details. ",
                            e
                    )
                }
    }

    fun getProductList(fragment: Fragment) {
        mFireStore.collection(Constans.PRODUCTS)
                .whereEqualTo(Constans.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener { documents ->
                    Log.e("Products List", documents.documents.toString())
                    val productsList: ArrayList<Product> = ArrayList()
                    for (i in documents) {
                        val product = i.toObject(Product::class.java)
                        product.product_id = i.id

                        productsList.add(product)

                    }

                    when (fragment) {
                        is ProductFragment -> {

                            fragment.successProductListFromFireStrore(productsList)

                        }
                    }
                }
    }


    fun getProductDetails(activity: ProductDetailActivity, productId: String) {
        mFireStore.collection(Constans.PRODUCTS)
                .document(productId)
                .get()
                .addOnSuccessListener { document ->
                    val product = document.toObject(Product::class.java)
                    if (product != null) {
                        activity.productDetailsSuccess(product)
                    }


                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                }
    }

    fun addCartItems(activity: ProductDetailActivity, addToCart: CartItem) {
        mFireStore.collection(Constans.CART_ITEMS)
                .document()
                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addToCartSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName, "Error while creating the document for cart item"
                    )
                }
    }

    fun deleteProduct(fragment: ProductFragment, productId: String) {
        mFireStore.collection(Constans.PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener {
                    fragment.productDeleteSuccess()
                }.addOnFailureListener { e ->
                    try {

                        fragment.hideProgressDialog()

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order) {
        val writeBatch = mFireStore.batch() // batch birden fazla is yapmaya yariyor.
        for (cartItem in cartList) {
            // val productHashMap = HashMap < String ,Any >()
            /* productHashMap[Constans.STOCK_QUANTITY] =
                    (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()
*/
            // Prepare the sold product details
            for (cart in cartList) {

                val soldProduct = SoldProduct(
                        cartItem.product_owner_id,
                        cart.title,
                        cart.price,
                        cart.cart_quantity,
                        cart.image,
                        order.title,
                        order.order_date_time,
                        order.sub_total_amount,
                        order.shipping_charge,
                        order.total_amount,
                        order.address
                )

                val documentReference = mFireStore.collection(Constans.SOLD_PRODUCT)
                        .document()
                writeBatch.set(documentReference, soldProduct)

            }

            for (cartItem in cartList) {

                val documentReference = mFireStore.collection(Constans.CART_ITEMS)
                        .document(cartItem.id)
                writeBatch.delete(documentReference)
            }
            writeBatch.commit().addOnSuccessListener {

                activity.allDetailsUpdatedSuccess()


            }.addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                        activity.javaClass.simpleName, "Error while updating all details after order placed ."
                )
            }
        }
    }

    fun getSoldProductLst(fragment: SoldProductFragment) {
        mFireStore.collection(Constans.SOLD_PRODUCT)
                .whereEqualTo(Constans.USER_ID,getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    val list : ArrayList<SoldProduct> = ArrayList()
                            for (i in document.documents) {

                                val soldProduct = i.toObject(SoldProduct::class.java)!!
                                soldProduct.id = i.id

                                list.add(soldProduct)

                            }
                    fragment.successSoldProductsList(list)

                }.addOnFailureListener { e->
                    fragment.hideProgressDialog()
                    Log.e(
                            fragment.javaClass.simpleName,"Error while getting the list of sold products. ",
                            e
                    )
                }
    }

        fun getMyOderList(fragment: OrdersFragment) {
            mFireStore.collection(Constans.ORDERS)
                    .whereEqualTo(Constans.USER_ID, getCurrentUserID())
                    .get()
                    .addOnSuccessListener { document ->


                        val list: ArrayList<Order> = ArrayList()
                        for (i in document.documents) {
                            val orderItem = i.toObject(Order::class.java)!!
                            orderItem.id = i.id

                            list.add(orderItem)
                        }
                        fragment.populateOrdersListInUI(list)


                    }.addOnFailureListener { e ->


                        fragment.hideProgressDialog()
                        Log.e(
                                fragment.javaClass.simpleName, "Error while getting the orders list.",
                                e
                        )
                    }
        }

        fun placeOrder(activity: CheckoutActivity, order: Order) {
            mFireStore.collection(Constans.ORDERS)
                    .document()
                    .set(order, SetOptions.merge())

                    .addOnSuccessListener {

                        activity.orderPlacedSuccess()


                    }.addOnFailureListener { e ->
                        activity.hideProgressDialog()

                        Log.e(
                                activity.javaClass.simpleName, "Error while placing an order."
                        )

                    }
        }

        fun getCartList(activity: Activity) {
            val list: ArrayList<CartItem> = ArrayList()

            mFireStore.collection(Constans.CART_ITEMS)
                    .whereEqualTo(Constans.USER_ID, getCurrentUserID())
                    .get()
                    .addOnSuccessListener { document ->
                        Log.e(activity.javaClass.simpleName, document.documents.toString())

                        for (i in document.documents) {
                            val cartItem = i.toObject(CartItem::class.java)!!
                            cartItem.id = i.id
                            list.add(cartItem)
                        }

                        when (activity) {
                            is CartListActivity -> {
                                activity.successCartItemsList(list)
                            }
                            is CheckoutActivity -> {
                                activity.successCartItemList(list)
                            }
                        }
                    }.addOnFailureListener {
                        when (activity) {
                            is CartListActivity -> {
                                activity.hideProgressDialog()
                            }
                            is CheckoutActivity -> {
                                activity.successCartItemList(list)
                            }
                        }

                        Log.e(activity.javaClass.simpleName, "Error while checking existing cart list item.")

                    }
        }

        fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

            mFireStore.collection(Constans.ADDRESSES)
                    .document(addressId)
                    // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                    .set(addressInfo, SetOptions.merge())
                    .addOnSuccessListener {

                        // Here call a function of base activity for transferring the result to it.
                        activity.addUpdateAddressSuccess()
                    }
                    .addOnFailureListener { e ->
                        activity.hideProgressDialog()
                        Log.e(
                                activity.javaClass.simpleName,
                                "Error while updating the Address.",
                                e
                        )
                    }
        }

        fun getAddressesList(activity: AddressListActivity) {
            mFireStore.collection(Constans.ADDRESSES)
                    .whereEqualTo(Constans.USER_ID, getCurrentUserID())
                    .get()
                    .addOnSuccessListener { document ->

                        Log.e(activity.javaClass.simpleName, document.documents.toString())
                        val addressList: ArrayList<Address> = ArrayList()

                        for (i in document.documents) {
                            val address = i.toObject(Address::class.java)!!
                            address.id = i.id

                            addressList.add(address)
                        }


                        activity.successAddressListFromFirestore(addressList)
                        //   activity.hideProgressDialog()
                        /*   for (i in document.documents) {
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }*/


                    }.addOnFailureListener { e ->
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error while getting existing the address.")

                    }
        }

        fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
            mFireStore.collection(Constans.ADDRESSES)
                    .document()
                    .set(addressInfo, SetOptions.merge())
                    .addOnSuccessListener {
                        activity.addUpdateAddressSuccess()

                    }.addOnFailureListener { e ->
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error while checking existing cart list item.")

                    }
        }


        fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
            mFireStore.collection(Constans.CART_ITEMS)
                    .document(cart_id)
                    .update(itemHashMap)
                    .addOnSuccessListener {

                        when (context) {
                            is CartListActivity -> {
                                context.itemUpdateSuccess()
                            }
                        }
                    }.addOnFailureListener { e ->
                        when (context) {
                            is CartListActivity -> {
                                context.hideProgressDialog()
                            }
                        }

                        Log.e(
                                context.javaClass.simpleName,
                                "Error while updating the cart item.", e
                        )

                    }
        }

        fun checkIfItemExistInCart(activity: ProductDetailActivity, productId: String) {
            mFireStore.collection(Constans.CART_ITEMS)
                    .whereEqualTo(Constans.USER_ID, getCurrentUserID())
                    .whereEqualTo(Constans.PRODUCT_ID, productId)
                    .get()
                    .addOnSuccessListener { document ->

                        Log.e(activity.javaClass.simpleName, document.documents.toString() + " Enes")

                        if (document.documents.size > 0) {
                            activity.productExistsInCart()

                        } else {
                            activity.hideProgressDialog()
                        }

                    }.addOnFailureListener { e ->
                        activity.hideProgressDialog()

                        Log.e(activity.javaClass.simpleName, "Error while checking existing cart list.")
                    }
        }

        fun getAllProductsList(activity: Activity) {
            mFireStore.collection(Constans.PRODUCTS)
                    .get()
                    .addOnSuccessListener { document ->


                        Log.e("Products List", document.documents.toString())
                        val productsList: ArrayList<Product> = ArrayList()

                        for (i in document.documents) {
                            val product = i.toObject(Product::class.java)!!
                            product.product_id = i.id
                            productsList.add(product)
                        }
                        when (activity) {
                            is CartListActivity -> {
                                activity.successProductsListFromFireStore(productsList)

                            }
                            is CheckoutActivity -> {
                                activity.successProductListFromFireStore(productsList)
                            }
                        }


                    }.addOnFailureListener { e ->

                        when (activity) {
                            is CartListActivity -> {
                                activity.hideProgressDialog()
                            }
                            is CheckoutActivity -> {
                                activity.hideProgressDialog()
                            }
                        }

                        Log.e("Get product list", "Error while getting all product list.", e)


                    }
        }

        fun removeItemFromCart(context: Context, cart_id: String) {
            mFireStore.collection(Constans.CART_ITEMS)
                    .document(cart_id)
                    .delete()
                    .addOnSuccessListener {

                        when (context) {
                            is CartListActivity -> {
                                context.itemRemovedSuccess()
                            }
                        }

                    }.addOnFailureListener { e ->

                        when (context) {
                            is CartListActivity -> {
                                context.hideProgressDialog()
                            }
                        }

                    }
        }


        fun getDashBoardItemList(fragment: DashboardFragment) {
            mFireStore.collection(Constans.PRODUCTS)
                    .get()
                    .addOnSuccessListener { document ->
                        Log.e(fragment.javaClass.simpleName, document.documents.toString())

                        val productsList: ArrayList<Product> = ArrayList()
                        for (i in document.documents) {
                            val product = i.toObject(Product::class.java)!!
                            product.product_id = i.id
                            productsList.add(product)
                        }
                        fragment.successDashboardItemsList(productsList)
                    }
                    .addOnFailureListener { e ->
                        fragment.hideProgressDialog()
                        Log.e(fragment.javaClass.simpleName, "Error")

                    }
        }
    }

