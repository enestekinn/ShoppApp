package com.example.shopapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
        val id: String ="",
        val firstName: String = "",
        val lastName: String = "",
        val email: String = "",
        val image: String = "",
        val mobile: Long = 0,
        val gender: String = "",
        val profileCompleted: Int =0)  : Parcelable /*{
        constructor(parcel: Parcel) : this (
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readLong(),
                parcel.readString(),
                parcel.readInt()
                )
        override fun describeContents() : Int{
                return  0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
                dest.writeString(id)
                dest.writeString(firstName)
                dest.writeString(lastName)
                dest.writeString(email)
                dest.writeString(image)
                dest.writeLong(mobile)
                dest.writeString(gender)
                dest.writeInt(profileCompleted)

        }
        companion object CREATOR : Parcelable.Creator<User> {
                override fun createFromParcel(source: Parcel): User {
                        return User(source)
                }

                override fun newArray(size: Int): Array<User?> {
                     return   arrayOfNulls(size)
                }

        }*/

