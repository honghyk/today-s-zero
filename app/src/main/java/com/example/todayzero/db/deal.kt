package com.example.todayzero.db

data class deal(val did:String, val date:String, val store:String, val price:String,val category:String, val isZero:Int){
    //isZero : 1 이면 제로페이 결제, 0 이면 기타 결제
}