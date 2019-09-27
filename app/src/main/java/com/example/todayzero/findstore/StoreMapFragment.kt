package com.example.todayzero.findstore


import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.todayzero.R
import com.example.todayzero.data.Store
import com.example.todayzero.expense.ExpenseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class StoreMapFragment : Fragment(), OnMapReadyCallback {

    lateinit var googleMap: GoogleMap
    lateinit var mapView: MapView
    var addrList: MutableList<Address>? = null
    lateinit var addExpenseBtn: Button
    lateinit var  storenameTextview:TextView
    lateinit var storeaddrTextView: TextView
    lateinit var storeremarkTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.store_map_frag, container, false)

        with(root) {

            storenameTextview=findViewById(R.id.store_name_text_view)
            storeaddrTextView=findViewById(R.id.store_addr_text_view)
            storeremarkTextView=findViewById(R.id.store_remark_text_view)


            mapView = findViewById(R.id.mapView)
            addExpenseBtn = findViewById<Button>(R.id.map_add_expense_button).apply {
                setOnClickListener { showExpenseActivity() }
            }

        }

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0!!

        val geocoder = Geocoder(context!!)
        val storeLat: Double
        val storeLng: Double

        try {
            storenameTextview.text=store.name
            storeaddrTextView.text=store.addr
            storeremarkTextView.text= store.type

            addrList = geocoder.getFromLocationName(store.addr, 3)
        } catch(e: Exception) {
            e.printStackTrace()
        }
        if(addrList != null) {
            if(addrList!!.isEmpty()) {
                Toast.makeText(context!!, "가맹점 위치를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            } else {
                storeLat = addrList!![0].latitude
                storeLng = addrList!![0].longitude
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(storeLat, storeLng), zoomLevel))
                val markerOption = MarkerOptions()
                markerOption.position(LatLng(storeLat, storeLng))
                googleMap.addMarker(markerOption)


            }
        }
    }

    private fun showExpenseActivity() {
        val intent = Intent(context!!, ExpenseActivity::class.java)
        intent.putExtra(ExpenseActivity.STORE_NAME_TAG, store.name)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }


    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        lateinit var store: Store
        var zoomLevel = 15f

        fun newInstance(store: Store): StoreMapFragment {
            this.store = store
            return StoreMapFragment()
        }
    }
}
