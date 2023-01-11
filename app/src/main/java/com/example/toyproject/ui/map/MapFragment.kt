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
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.DTO.roomDTO
import com.example.toyproject.DTO.room_result
import com.example.toyproject.MainActivity
import com.example.toyproject.R
import com.example.toyproject.`interface`.Retrofit_API
import com.example.toyproject.databinding.FragmentMapBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    lateinit var behavior : BottomSheetBehavior<View>
    lateinit var RoomRecyclerView : RecyclerView
    private var search_edit : EditText? = null
    private var search : String? = null
    private var spinner : Spinner? = null

    var dataArr = arrayOf("전체보기","최고가순", "최저가순", "랭킹순", "조회수순")
    var sp_hash = HashMap<String, Any>()

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        mainActivity = context as MainActivity

        val dashboardViewModel = ViewModelProvider(this)[MapViewModel::class.java]
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapViewContainer = binding.mapView
        marker = MapPOIItem()
        u_location = binding.location
        behavior = BottomSheetBehavior.from(binding.bottomLayout)
        RoomRecyclerView = binding.recyclerView
        RoomRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        search_edit = binding.searchEdit
        val adapter1 = ArrayAdapter(mainActivity, R.layout.item_spinner, dataArr)
        spinner = binding.spinner
        spinner!!.adapter = adapter1

        return root
    }

    override fun onResume() {
        super.onResume()

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.d("statechanged", "statechanged")
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("statechanged", "onslide")
            }

        })

        // 처음 로딩을 위한 창
        call!!.getRoom().enqueue(object : Callback<roomDTO> {
            override fun onResponse(call: Call<roomDTO>, response: Response<roomDTO>) {
                if (response.isSuccessful){
                    val r_result : roomDTO? = response.body()

                    r_result!!.apply {
                        marker_name = result[0].roomName
                        marker_add = result[0].location
                        marker_price = result[0].price1
                    }

                    val roomList = r_result.result
                    Log.d("getRoom", "$roomList")
                    RoomRecyclerView.adapter = RecyclerAdapter(roomList, mapView!!, marker!!, context!!)

                    // 커스텀 마커 이벤트 구간 마커 이름과 좌표 입력
//                    makerEvent(itemName = marker_name!!, MapPoint.mapPointWithGeoCoord(36.739601,  127.075356), 1)


                    Log.d("getRoom", r_result.result[0].roomName)
                }
            }

            override fun onFailure(call: Call<roomDTO>, t: Throwable) {
                Log.d("fail_getRoom", "${t.message}")
            }

        })

        // 검색 이벤트
        search_edit!!.setOnEditorActionListener { textView, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                search = search_edit!!.text.toString()
                Toast.makeText(context, search, Toast.LENGTH_SHORT).show()
                call!!.getSearch(search).enqueue(object : Callback<roomDTO>{
                    override fun onResponse(call: Call<roomDTO>, response: Response<roomDTO>) {

                        if (response.isSuccessful) {
                            val search_result = response.body()
                            RoomRecyclerView.adapter = RecyclerAdapter(RoomList = search_result!!.result, mapView!!, marker!!, context!!)
                        }

                        handled = true
                    }

                    override fun onFailure(call: Call<roomDTO>, t: Throwable) {
                        Toast.makeText(context, "검색결과를 찾지 못했습니다", Toast.LENGTH_SHORT).show()
                    }

                })
            }
            handled
        }

        // spinner 이벤트
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                val router = arrayOf("","asc", "desc", "rank", "hits")
                call?.getRoom_spinner(router[p2])!!.enqueue(object : Callback<roomDTO>{
                    override fun onResponse(call: Call<roomDTO>, response: Response<roomDTO>) {
                        if(response.isSuccessful){
                            val spinner_result = response.body()

                            RoomRecyclerView.adapter = RecyclerAdapter(RoomList = spinner_result!!.result, mapView!!, marker!!, context!!)

                        }
                    }

                    override fun onFailure(call: Call<roomDTO>, t: Throwable) {
                        Toast.makeText(context, "오류가 발생하였습니다", Toast.LENGTH_SHORT).show()
                    }

                })
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

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
    }

    override fun onPause() {
        super.onPause()
        mapViewContainer.removeView(mapView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                    builder.setPositiveButton("설정으로 이동") { _, _ ->

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
    }

    override fun onMapViewCenterPointMoved(mapView: MapView, mapCenterPoint: MapPoint) {
    }

    override fun onMapViewZoomLevelChanged(mapView: MapView, zoomLevel: Int) {
    }

    override fun onMapViewSingleTapped(mapView: MapView, mapPoint: MapPoint) {
    }

    override fun onMapViewDoubleTapped(mapView: MapView, mapPoint: MapPoint) {
    }
    override fun onMapViewLongPressed(mapView: MapView, mapPoint: MapPoint) {
    }

    override fun onMapViewDragStarted(mapView: MapView, mapPoint: MapPoint) {
    }

    override fun onMapViewDragEnded(mapView: MapView, mapPoint: MapPoint) {
    }

    override fun onMapViewMoveFinished(mapView: MapView, mapPoint: MapPoint) {
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
