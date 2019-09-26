package com.example.todayzero.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.example.todayzero.findstore.StoreActivity.Companion.storeList
import com.example.todayzero.data.Store


class DBHelper(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){


    val zone= mutableMapOf<Int,String>(1 to "강남구",2 to "강동구", 3 to "강북구", 4 to "강서구", 5 to "관악구", 6 to "광진구", 7 to "구로구", 8 to "금천구", 9 to "노원구", 10 to "도봉구", 11 to "동대문구", 12 to "동작구", 13 to "마포구", 14 to "서대문구", 15  to "서초구", 16 to "성동구", 17 to "성북구", 18 to "송파구", 19 to "양천구", 20 to "영등포구", 21 to "용산구", 22 to "은평구", 23 to "종로구", 24 to "중구", 25 to "중랑구")
    val wdb=writableDatabase
    val rdb=readableDatabase

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        //DB 수정이 필요한 경우
        db.execSQL("DROP TABLE IF EXISTS '${users.TABLE_NAME}'")
        for(gu in 1..25){
            var sql= "DROP TABLE IF EXISTS '${zone[gu]}'"
            db.execSQL(sql)
            Log.i("create db","create stable ${zone[gu]}")
        }
        db.execSQL("DROP TABLE IF EXISTS '${deals.TABLE_NAME}'")
        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase) {

        //zeroPayDB에 users 와 stores 테이블 생성
        db.execSQL(SQL_CREATE_TABLE_USERS)
        Log.i("create db","create utable")

        //table name : stores강남구, stores마포구
        for(gu in 1..25){
            var sql= SQL_CREATE_TABLE_STORES_PRE+"${zone[gu]}"+ SQL_CREATE_TABLE_STORES_POST
            db.execSQL(sql)
            Log.i("create db","create stable ${zone[gu]}")
        }


        db.execSQL(SQL_CREATE_TABLE_DEALS)
        Log.i("create db","create dtable")

    }

    fun createTable(){

        //zeroPayDB에 users 와 stores 테이블 생성
        wdb.execSQL(SQL_CREATE_TABLE_USERS)
        Log.i("create db","create utable")

        for(gu in 1..25){
            var sql= SQL_CREATE_TABLE_STORES_PRE+"${zone[gu]}"+ SQL_CREATE_TABLE_STORES_POST
            wdb.execSQL(sql)
            Log.i("create db","create stable ${zone[gu]}")
        }


        wdb.execSQL(SQL_CREATE_TABLE_DEALS)
        Log.i("create db","create dtable")
    }

    companion object {
        val DATABASE_VERSION=1
        val DATABASE_NAME="zeroPayDB"

        val SQL_CREATE_TABLE_USERS="CREATE TABLE ${users.TABLE_NAME}"+
                "(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+ users.KEY_NAME+" TEXT,"+
                users.KEY_EXPENDITURE+" INTEGER,"+users.KEY_INCOME+" TEXT );"

        /*
        val SQL_CREATE_TABLE_STORES="CREATE TABLE ${stores.TABLE_NAME}"+
                "(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+ stores.KEY_NAME+" TEXT,"+
                stores.KEY_ADDR+" TEXT,"+stores.KEY_INFO+" TEXT);"
         */


        val SQL_CREATE_TABLE_STORES_PRE="CREATE TABLE ${stores.TABLE_NAME}"
        val SQL_CREATE_TABLE_STORES_POST="(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+ stores.KEY_NAME+" TEXT,"+
                stores.KEY_ADDR+" TEXT,"+stores.KEY_GU+" TEXT,"+stores.KEY_INFO+" TEXT);"


        val SQL_CREATE_TABLE_DEALS="CREATE TABLE ${deals.TABLE_NAME}"+
                "(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+
                deals.KEY_DATE+" TEXT,"+ deals.KEY_NAME+" TEXT,"+deals.KEY_PRICE+" TEXT,"+deals.KEY_CATEGORY+" TEXT,"+deals.KEY_MEMO+" TEXT,"+deals.KEY_ISZERO+" INTEGER);"


    }

    class users:BaseColumns{
        companion object {
            val TABLE_NAME="users"
            val KEY_NAME="u_name"
            val KEY_EXPENDITURE="u_expenditure"
            val KEY_INCOME="u_income"
        }
    }
    class stores:BaseColumns{
        companion object {
            val TABLE_NAME="stores"
            val KEY_ID="s_id"
            val KEY_NAME="s_name"
            val KEY_ADDR="s_addr"
            val KEY_GU="s_gu"
            val KEY_INFO="s_info"
        }
    }

    class deals:BaseColumns{
        companion object {
            val TABLE_NAME="deals"
            val KEY_ID="d_id"
            val KEY_DATE="date"
            val KEY_NAME="store"
            val KEY_PRICE="price"
            val KEY_CATEGORY="category"
            val KEY_MEMO="memo"
            val KEY_ISZERO="is_zeropay"
        }
    }




    //db 에 삽입
    fun insertUser(user: user){

        val values=ContentValues().apply {

            put(users.KEY_NAME,user.uname)
            put(users.KEY_EXPENDITURE,user.expenditure)
            put(users.KEY_INCOME,user.income)
        }

        val success=wdb.insert(users.TABLE_NAME,null,values)
        Log.i("InsertedUserID: ","$success ${user.uname}")
    }

    fun insertStores( Stores:ArrayList<Store>){
        wdb.beginTransaction()
        try{
            for (i in 0..Stores.size-1){
                val values=ContentValues().apply {
                    put(stores.KEY_NAME,Stores.get(i).name)
                    put(stores.KEY_ADDR,Stores.get(i).addr)
                    put(stores.KEY_GU,Stores.get(i).locality)
                    put(stores.KEY_INFO,Stores.get(i).type)
                }
                val success=wdb.insert(stores.TABLE_NAME+Stores.get(i).locality,null,values)
                //  Log.i("test ","${Stores.get(i).locality} +$success + storekeyID+ ${Stores.get(i).sid}")
            }
            wdb.setTransactionSuccessful()
        }finally {
            wdb.endTransaction()
        }
    }


    fun insertStore(store: Store){
        val stoAddr=store.addr
        if(stoAddr.length>0){
            if(!findSameStore(stoAddr,store.locality)){

                val values=ContentValues().apply {
                    put(stores.KEY_NAME,store.name)
                    put(stores.KEY_ADDR,store.addr)
                    put(stores.KEY_GU,store.locality)
                    put(stores.KEY_INFO,store.type)
                }

                val success=wdb.insert(stores.TABLE_NAME+store.locality,null,values)
              //  Log.i("InsertedStoreID: ","${store.locality} +$success + storekeyID+ ${store.sid}")

            }
            else {
                Log.i("insert_db","이미 등록한 상점")
                return
            }
        }
    }
    fun findSameStore(newaddr:String,gu: String):Boolean{

        val str=stores.TABLE_NAME+gu
        val selectAllQuery="SELECT * FROM $str WHERE  ${stores.KEY_ADDR} = ?"
        val selectionArgs=arrayOf(newaddr)
        val cursor=rdb.rawQuery(selectAllQuery,selectionArgs)

        if(cursor!=null){

            while (cursor.moveToNext()) {
                return true
            }
            return false
        }
        else
            return false
    }

    fun insertDeal(deal: deal){

        val values=ContentValues().apply {

            put(deals.KEY_DATE,deal.date)
            put(deals.KEY_NAME,deal.store)
            put(deals.KEY_PRICE,deal.price)
            put(deals.KEY_CATEGORY,deal.category)
            put(deals.KEY_MEMO,deal.memo)
            put(deals.KEY_ISZERO,deal.isZero)
        }

        val success=wdb.insert(deals.TABLE_NAME,null,values)
        Log.i("InsertedDealID: ", " ${BaseColumns._ID}")

    }

    //db 에서 삭제
    fun deleteUser(){
        wdb.delete(users.TABLE_NAME,null,null)
    }
    fun deleteStore(sid:String,gu:String){   //gu 에 삭제할 store.locatlity  입력

        val selection="${BaseColumns._ID} = ?"
        val selectionArgs=arrayOf(sid)
        wdb.delete(stores.TABLE_NAME+gu,selection,selectionArgs)
    }
    fun deleteDeal(did: String){
        val selection="${BaseColumns._ID} = ?"
        val selectionArgs=arrayOf(did)
        wdb.delete(deals.TABLE_NAME,selection,selectionArgs)

    }

    //db 변경
    fun updateUserIncome(income:String){

        val value=ContentValues()
        value.put(users.KEY_INCOME,income)
        //val selection="${users.KEY_ID} LIKE ?"
        //val selectionArgs=arrayOf(uid)
        //val count=wdb.update(users.TABLE_NAME,value,selection,selectionArgs)
        val count=wdb.update(users.TABLE_NAME,value,null,null)

        Log.i("updateDB_user","$count")
    }
    fun updateUserExpenditure(expenditure:String){
        //expenditure 변경을 많이 쓸 것같아 따로.
        val value=ContentValues()
        value.put(users.KEY_EXPENDITURE,expenditure)
        //val selection="${users.KEY_ID} LIKE ?"
        //val selectionArgs=arrayOf(uid)
        val count=wdb.update(users.TABLE_NAME,value,null,null)

        Log.i("updateDB_user","$count")
    }

    fun updateStore(sid: String,store: Store){

        val value=ContentValues().apply {
            put(stores.KEY_NAME,store.name)
            put(stores.KEY_ADDR,store.addr)
            put(stores.KEY_GU,store.locality)
            put(stores.KEY_INFO,store.type)
        }

        val selection="${BaseColumns._ID} LIKE ?"
        val selectionArgs=arrayOf(sid)
        val count=wdb.update(stores.TABLE_NAME+store.locality,value,selection,selectionArgs)

        Log.i("update_db: ","$count")

    }
    fun updateDeal(did: String,deal: deal){

        val value=ContentValues().apply {
            put(deals.KEY_DATE,deal.date)
            put(deals.KEY_NAME,deal.store)
            put(deals.KEY_PRICE,deal.price)
            put(deals.KEY_CATEGORY,deal.category)
            put(deals.KEY_MEMO,deal.memo)
            put(deals.KEY_ISZERO,deal.isZero)
        }

        val selection="${BaseColumns._ID} LIKE ?"
        val selectionArgs=arrayOf(did)
        val count=wdb.update(deals.TABLE_NAME,value,selection,selectionArgs)

        Log.i("update_db: ","${deal.store}//$count")
    }

    //db  조회
    fun getUser(): user {
        val selectAllQuery="SELECT * FROM ${users.TABLE_NAME}"
        val cursor=rdb.rawQuery(selectAllQuery,null)

        if(cursor!=null){
            Log.i("finduser",cursor.columnCount.toString())

            with(cursor){
                while(moveToNext()){
                    val name=cursor.getString(cursor.getColumnIndex(users.KEY_NAME))
                    val income=cursor.getString(cursor.getColumnIndex(users.KEY_INCOME))
                    val expenditure=cursor.getInt(cursor.getColumnIndex(users.KEY_EXPENDITURE))
                    val result=user(name,expenditure,income)
                    return result
                }

                Log.i("findUser","no user")
            }
        }
        val fail=user("",0,"")
        return fail
    }

    fun getStorebyQuery(sid:Int,gu: String):Store{
        // 스토어 빨리 찾기
        val projection=arrayOf(BaseColumns._ID,stores.KEY_NAME,stores.KEY_ADDR,stores.KEY_GU,stores.KEY_INFO)

        val selection="${BaseColumns._ID} = ?"
        val selectionArgs=arrayOf(sid.toString())
        lateinit var store:Store
        val cursor=rdb.query(stores.TABLE_NAME, projection, selection,  selectionArgs,null,null,null)
        while (cursor.moveToNext()) {
            val sid = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val name = cursor.getString(cursor.getColumnIndex(stores.KEY_NAME))
            val addr = cursor.getString(cursor.getColumnIndex(stores.KEY_ADDR))
            val gu = cursor.getString(cursor.getColumnIndex(stores.KEY_GU))
            val info = cursor.getString(cursor.getColumnIndex(stores.KEY_INFO))
            store = Store(sid, name, addr, gu,"", info)
            Log.i("getStore",store.sid.toString())
        }
        return store
    }

    fun getStore(sid:Int,gu:String):Store{ //gu: 찾으려는 store의 locality
        var store=getStores(gu).get(sid-1)
        return store
    }

    fun getStores(gu:String):ArrayList<Store>{

        val storeList=ArrayList<Store>()
        val projection=arrayOf(BaseColumns._ID, DBHelper.stores.KEY_NAME, DBHelper.stores.KEY_ADDR,stores.KEY_GU,
            stores.KEY_INFO)
        val cursor=rdb.query(stores.TABLE_NAME+gu,projection,null,null,null,null,null,null)
        if(cursor!=null) {
            Log.i("getStore","cursor")
            while (cursor.moveToNext()) {
                val sid = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val name = cursor.getString(cursor.getColumnIndex(stores.KEY_NAME))
                val addr = cursor.getString(cursor.getColumnIndex(stores.KEY_ADDR))
                val gu = cursor.getString(cursor.getColumnIndex(stores.KEY_GU))
                val info = cursor.getString(cursor.getColumnIndex(stores.KEY_INFO))
                val store = Store(sid, name, addr, gu, "",info)
                Log.i("getStore",store.sid.toString())

                storeList.add(store)
            }
        }
        else{
            Log.i("searchStores","no store")
        }
        return storeList
    }

    fun getStores(gu:Int):ArrayList<Store>{

        val storeList=ArrayList<Store>()
        val projection=arrayOf(BaseColumns._ID, DBHelper.stores.KEY_NAME, DBHelper.stores.KEY_ADDR,stores.KEY_GU,
            stores.KEY_INFO)
        val cursor=rdb.query(stores.TABLE_NAME+zone[gu],projection,null,null,null,null,null,null)
        if(cursor!=null) {
            Log.i("getStore","cursor")
            while (cursor.moveToNext()) {
                val sid = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val name = cursor.getString(cursor.getColumnIndex(stores.KEY_NAME))
                val addr = cursor.getString(cursor.getColumnIndex(stores.KEY_ADDR))
                val gu = cursor.getString(cursor.getColumnIndex(stores.KEY_GU))
                val info = cursor.getString(cursor.getColumnIndex(stores.KEY_INFO))
                val store = Store(sid, name, addr, gu, "",info)
                Log.i("getStore",store.sid.toString())

                storeList.add(store)
            }
        }
        else{
            Log.i("searchStores","no store")
        }
        return storeList
    }

    fun getDeal(did:Int,date:String):deal{

        var deal=getDeals(date).get(did-1)
        return deal
    }
    fun getDeals(date:String):ArrayList<deal>{

        val dealList=ArrayList<deal>()
        dealList.clear()

        val projection=arrayOf(BaseColumns._ID,deals.KEY_NAME,deals.KEY_DATE,deals.KEY_PRICE,deals.KEY_CATEGORY,deals.KEY_MEMO,deals.KEY_ISZERO)
        val selection="${deals.KEY_DATE} LIKE ?"
        val selectionArgs=arrayOf("$date%")
        val sortOrder="${deals.KEY_DATE} DESC"
        val cursor=rdb.query(deals.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder,null)

        //val cursor=rdb.query(deals.TABLE_NAME,projection,null,null,null,null,sortOrder,null)
        if(cursor!=null) {
            while (cursor.moveToNext()) {
                val did = cursor.getString(cursor.getColumnIndex(BaseColumns._ID))
                val sname = cursor.getString(cursor.getColumnIndex(deals.KEY_NAME))
                val date = cursor.getString(cursor.getColumnIndex(deals.KEY_DATE))
                val price = cursor.getString(cursor.getColumnIndex(deals.KEY_PRICE))
                val category = cursor.getString(cursor.getColumnIndex(deals.KEY_CATEGORY))
                val memo=cursor.getString(cursor.getColumnIndex(deals.KEY_MEMO))
                val isZero = cursor.getInt(cursor.getColumnIndex(deals.KEY_ISZERO))

                val deal = deal(did, date, sname, price, category, memo, isZero)
                dealList.add(deal)
                Log.i("searchdeal",deal.date)

            }
        }else{
            Log.i("searchdeal","nodeal")
        }
        return dealList
    }

    fun getExpense(date:String):String{

        var expense=0
        val sql="SELECT SUM(${deals.KEY_PRICE}) FROM ${deals.TABLE_NAME} WHERE ${deals.KEY_DATE} LIKE ?"
        val selectionArgs=arrayOf("$date%")
        val cursor=rdb.rawQuery(sql,selectionArgs)
        if(cursor!=null) {
            while (cursor.moveToNext()) {
                expense=cursor.getInt(0)
            }
        }
        return expense.toString()

    }


}