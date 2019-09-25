package com.example.todayzero.db

import java.io.Serializable

data class deal(val did:String, val date:String, val store:String, val price:String,val category:String,val memo:String, val isZero:Int):Serializable{
    //isZero : 1 이면 제로페이 결제, 0 이면 기타 결제
}