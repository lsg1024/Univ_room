package com.example.toyproject.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.toyproject.DTO.roomDTO
import com.example.toyproject.MainActivity
import com.example.toyproject.R
import com.example.toyproject.`interface`.Retrofit_API
import com.example.toyproject.databinding.FragmentMapBinding
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapPOIItem.MarkerType.CustomImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapFragment : Fragment(), MapView.MapViewEventListener, MapView.POIItemEventListener {

    private val call by lazy { Retrofit_API.getInstance() }

    private var _binding: FragmentMapBinding? = null

    private val LOG_TAG = "EventsDemoActivity"
    private val REQUEST_ACCESS_FINE_LOCATION = 1000

    // 위도
    var uLatitude: Double = 0.0
    // 경도
    var uLongitude: Double = 0.0
    // 위치 탐색 성공
    var success : Boolean = true
    // 사용자 위치
    var userPosition : MapPoint? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity
    private var mapView: MapView? = null
    lateinit var mapViewContainer: ViewGroup
    private var maptext : TextView? = null
    private var u_location : ConstraintLayout? = null
    private var marker : MapPOIItem? = null

    private var marker_name : String? = null
    private var marker_add : String? = null
    private var marker_price : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val dashboardViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mainActivity = context as MainActivity

        mapViewContainer = binding.mapView
        maptext = binding.textMap
        marker = MapPOIItem()
        u_location = binding.location

        return root
    }

    override fun onResume() {
        super.onResume()

        call!!.getRoom().enqueue(object : Callback<roomDTO> {
            override fun onResponse(call: Call<roomDTO>, response: Response<roomDTO>) {
                if (response.isSuccessful){
                    val r_result : roomDTO? = response.body()
                    r_result!!.apply {
                        marker_name = result[0].roomName
                        marker_add = result[0].location
                        marker_price = result[0].price1
                    }

                    // 커스텀 마커 이벤트 구간 마커 이름과 좌표 입력
                    makerEvent(itemName = marker_name!!, MapPoint.mapPointWithGeoCoord(36.739601,  127.075356), 1)


                    Log.d("getRoom", r_result.result[0].roomName)
                }
            }

            override fun onFailure(call: Call<roomDTO>, t: Throwable) {
                Log.d("fail_getRoom", "${t.message}")
            }

        })



        mapView = MapView(context)
        // 학교 좌표에서 시작
        mapView!!.setZoomLevel(1, false)
        mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.739990, 127.074486), true)
        mapViewContainer.addView(mapView)
        mapView?.setMapViewEventListener(this)
        mapView?.setPOIItemEventListener(this)

        u_location!!.setOnClickListener {

            if (checkLocationService()) {
                permissionCheck()

            } else {
                // GPS가 꺼져있을 경우
                Toast.makeText(context, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }

        }

//        // 커스텀 마커 이벤트 구간 마커 이름과 좌표 입력
//        makerEvent(itemName = marker_name!!, MapPoint.mapPointWithGeoCoord(36.739601,  127.075356), 1)

    }

    override fun onPause() {
        super.onPause()
        mapViewContainer.removeView(mapView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun makerEvent(itemName : String, marker_point : MapPoint, tag_num : Int){

        mapView!!.setCalloutBalloonAdapter(lCustomBalloonAdapter(layoutInflater))
        val pin = R.drawable.baseline_place_black_36

        marker!!.itemName = itemName
        marker!!.mapPoint = marker_point
        marker!!.tag = tag_num
        marker!!.markerType = CustomImage
        marker!!.customImageResourceId = pin
        marker!!.isCustomImageAutoscale = true
        marker!!.setCustomImageAnchor(0.5f, 1.0f)
        mapView!!.addPOIItem(marker)

    }

    inner class lCustomBalloonAdapter(inflater: LayoutInflater) : CalloutBalloonAdapter {

        @SuppressLint("InflateParams")
        val lCalloutBalloon : View = inflater.inflate(R.layout.customballoon, null)
        val name : TextView = lCalloutBalloon.findViewById(R.id.room_name)
        val address : TextView = lCalloutBalloon.findViewById(R.id.address)
        val price : TextView = lCalloutBalloon.findViewById(R.id.price1)

        override fun getCalloutBalloon(p0: MapPOIItem?): View {

            name.text = marker_name
            address.text = marker_add
            price.text = marker_price.toString()
            return lCalloutBalloon
        }

        override fun getPressedCalloutBalloon(p0: MapPOIItem?): View {

            // 마커 클릭 후 적혀 있는 내용을 클릭했을 때 나오는 이밴트이므로 클릭했을 때 상세 정보 페이지
            return lCalloutBalloon
        }

    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = mainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치 권한 확인
    private fun permissionCheck() {
        val preference = mainActivity.getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference!!.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(mainActivity)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { _, _ ->
                    ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
                }
                builder.setNegativeButton("취소") { _, _ ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(mainActivity)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->

                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            addCategory(Intent.CATEGORY_DEFAULT)
                            data = Uri.parse("package:${activity?.packageName}")
                        }

                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { _, _ ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            startTracking()
        }
    }

    private fun startTracking(){
        // 현재위치를 잡는 코드 1번은 카카오맵 트래킹 모드를 이용해 현재좌표를 표시해주고 2번은 위치모드를 해제한다

        if (success){
            !success
            mapView!!.setZoomLevel(1, true)
            mapView!!.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            mapView!!.setShowCurrentLocationMarker(success)
            Toast.makeText(mainActivity, "현재 위치를 표시합니다.", Toast.LENGTH_SHORT).show()
            success = false
        } else {
            mapView!!.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
            mapView!!.setShowCurrentLocationMarker(success)
            Toast.makeText(mainActivity, "현재 위치를 표시를 해제합니다.", Toast.LENGTH_SHORT).show()
            success = true
        }
    }

    override fun onMapViewInitialized(mapView: MapView) {
        // MapView had loaded. Now, MapView APIs could be called safely.
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true)
//        Log.i(LOG_TAG, "onMapViewInitialized")
    }

    override fun onMapViewCenterPointMoved(mapView: MapView, mapCenterPoint: MapPoint) {
//        val mapPointGeo = mapCenterPoint.mapPointGeoCoord
//        mCameraTextView!!.text =
//            "camera position{target=" + String.format("lat/lng: (%f,%f), zoomLevel=%d",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude,
//                mapView.zoomLevel)
//        Log.i(LOG_TAG,
//            String.format("MapView onMapViewCenterPointMoved (%f,%f)",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude))
    }

    override fun onMapViewZoomLevelChanged(mapView: MapView, zoomLevel: Int) {
//        val mapPointGeo = mapView.mapCenterPoint.mapPointGeoCoord
//        mCameraTextView!!.text =
//            "camera position{target=" + String.format("lat/lng: (%f,%f), zoomLevel=%d",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude,
//                mapView.zoomLevel)
//        Log.i(LOG_TAG, String.format("MapView onMapViewZoomLevelChanged (%d)", zoomLevel))
    }

    override fun onMapViewSingleTapped(mapView: MapView, mapPoint: MapPoint) {
        val mapPointGeo = mapPoint.mapPointGeoCoord
        val mapPointScreenLocation = mapPoint.mapPointScreenLocation
        maptext!!.text =
            "single tapped, point=" + String.format("lat/lng: (%f,%f) x/y: (%f,%f)",
                mapPointGeo.latitude,
                mapPointGeo.longitude,
                mapPointScreenLocation.x,
                mapPointScreenLocation.y)
        Log.i(LOG_TAG,
            String.format("MapView onMapViewSingleTapped (%f,%f)",
                mapPointGeo.latitude,
                mapPointGeo.longitude))
    }

    override fun onMapViewDoubleTapped(mapView: MapView, mapPoint: MapPoint) {
//        val mapPointGeo = mapPoint.mapPointGeoCoord
//        val mapPointScreenLocation = mapPoint.mapPointScreenLocation
//        maptext!!.text =
//            "double tapped, point=" + String.format("lat/lng: (%f,%f) x/y: (%f,%f)",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude,
//                mapPointScreenLocation.x,
//                mapPointScreenLocation.y)
//        Log.i(LOG_TAG,
//            String.format(String.format("MapView onMapViewDoubleTapped (%f,%f)",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude)))
    }

    override fun onMapViewLongPressed(mapView: MapView, mapPoint: MapPoint) {
//        val mapPointGeo = mapPoint.mapPointGeoCoord
//        val mapPointScreenLocation = mapPoint.mapPointScreenLocation
//        maptext!!.text =
//            "long pressed, point=" + String.format("lat/lng: (%f,%f) x/y: (%f,%f)",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude,
//                mapPointScreenLocation.x,
//                mapPointScreenLocation.y)
//        Log.i(LOG_TAG,
//            String.format(String.format("MapView onMapViewLongPressed (%f,%f)",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude)))
    }

    override fun onMapViewDragStarted(mapView: MapView, mapPoint: MapPoint) {
//        val mapPointGeo = mapPoint.mapPointGeoCoord
//        val mapPointScreenLocation = mapPoint.mapPointScreenLocation
//        mDragTextView!!.text =
//            "drag started, point=" + String.format("lat/lng: (%f,%f) x/y: (%f,%f)",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude,
//                mapPointScreenLocation.x,
//                mapPointScreenLocation.y)
//        Log.i(LOG_TAG,
//            String.format("MapView onMapViewDragStarted (%f,%f)",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude))
    }

    override fun onMapViewDragEnded(mapView: MapView, mapPoint: MapPoint) {
//        val mapPointGeo = mapPoint.mapPointGeoCoord
//        val mapPointScreenLocation = mapPoint.mapPointScreenLocation
//        mDragTextView!!.text =
//            "drag ended, point=" + String.format("lat/lng: (%f,%f) x/y: (%f,%f)",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude,
//                mapPointScreenLocation.x,
//                mapPointScreenLocation.y)
//        Log.i(LOG_TAG,
//            String.format("MapView onMapViewDragEnded (%f,%f)",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude))
    }

    override fun onMapViewMoveFinished(mapView: MapView, mapPoint: MapPoint) {
//        val mapPointGeo = mapPoint.mapPointGeoCoord
//        Toast.makeText(baseContext, "MapView move finished", Toast.LENGTH_SHORT).show()
//        Log.i(LOG_TAG,
//            String.format("MapView onMapViewMoveFinished (%f,%f)",
//                mapPointGeo.latitude,
//                mapPointGeo.longitude))
    }

    // 아이콘 (마커) 클릭시 호출되는 이밴트
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {


    }

    @Deprecated("Deprecated in Java")
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

    }

    // 아이콘 (마커) 클릭후 호출되는 이밴트
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?, ) {

    }

    // 아이콘 (마커) 움직이는 이밴트
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }

}
