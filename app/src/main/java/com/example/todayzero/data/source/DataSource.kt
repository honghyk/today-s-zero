package com.example.todayzero.data.source

interface DataSource {
    interface  LoadDataCallback{
        fun onDataLoaded()
        fun onNetworkNotAvailable()
    }
    interface ApiListener {
        fun onDataLoaded(dataFilterType:DataFilterType)
        fun onFailure(dataFilterType:DataFilterType)
    }
}