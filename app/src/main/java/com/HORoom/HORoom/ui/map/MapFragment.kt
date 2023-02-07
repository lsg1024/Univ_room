package com.HORoom.HORoom.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.*
import com.HORoom.HORoom.DTO.roomDTO
import com.HORoom.HORoom.MainActivity
import com.HORoom.HORoom.`interface`.MySharedPreferences
import com.HORoom.HORoom.`interface`.Retrofit_API
import com.example.toyproject.databinding.FragmentMapBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class MapFragment : Fragment(){

    private val call by lazy { Retrofit_API.getInstance() }
    private var _binding: FragmentMapBinding? = null
    private val REQUEST_ACCESS_FINE_LOCATION = 1000
    // 위치 탐색 성공
    var success : Boolean = true

    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity
    private var mapView: MapView? = null
    lateinit var mapViewContainer: ViewGroup
    private var u_location : ConstraintLayout? = null
    private var marker : MapPOIItem? = null
    lateinit var behavior : BottomSheetBehavior<View>
    lateinit var roomRecyclerView : RecyclerView
    lateinit var topRecyclerView : RecyclerView
    private var search_edit : EditText? = null
    private var search : String? = null
    private var spinner : Spinner? = null
    // spinner 작동이 처음 들어왔을 때는 안되기 때문에 넣어줌
    private var spinnerData : String = "desc"

    var dataArr = arrayOf("최고가순", "최저가순", "랭킹순", "조회수순")
    var topData = arrayOf("전체보기" ,"정문", "중문", "후문", "서틀뒤", "농가마트", "기숙사 근처")
    var category : String? = null
    var t_position : Int? = null
    var heartArrayList : ArrayList<String>? = null
    lateinit var u_pk : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        category = arguments?.getString("bundle_data")
    }

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

        // 맵뷰 컨테이너
        mapViewContainer = binding.mapView
        // 마커 선언
        marker = MapPOIItem()
        // 현재 위치 버튼
        u_location = binding.location
        // 바텀시트
        behavior = BottomSheetBehavior.from(binding.bottomLayout)
        // 바텀시트 리사이클러 뷰
        roomRecyclerView = binding.recyclerView
        roomRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        // 상단 스크롤 버튼 리사이클러 뷰
        topRecyclerView = binding.scrollRecyclerView
        topRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        search_edit = binding.searchEdit
        val adapter1 = ArrayAdapter(mainActivity, R.layout.item_spinner, dataArr)
        spinner = binding.spinner
        spinner!!.adapter = adapter1
        // spinner setselection 옵션을 넣지 않으면 자동으로 true 값이 적용되어 로직이 작동해버린다
        spinner!!.setSelection(0, false)
        u_pk = MySharedPreferences.getUserKey(mainActivity)

        return root
    }

    override fun onResume() {
        super.onResume()

        val imm: InputMethodManager = mainActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        // 바텀 시트 이벤트 함수
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.d("statechanged", "$newState" + "state")
            }

            //
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("statechanged", "$slideOffset")

            }

        })

        // 룸 리사이클러 최초 로딩 -> 시작 창에서 클릭 했을 때
        retrofit("desc", category!!)

        // 룸 리상이클러 검색 이벤트
        search_edit!!.setOnEditorActionListener { _, i, _ ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                search = search_edit!!.text.toString()
                call!!.getSearch(search, "desc").enqueue(object : Callback<roomDTO>{
                    override fun onResponse(call: Call<roomDTO>, response: Response<roomDTO>) {

                        if (response.isSuccessful) {
                            val search_result = response.body()
                            roomRecyclerView.adapter = RecyclerAdapter(search_result!!.result, mapView!!, marker!!, context!!)
                            spinner!!.visibility = View.INVISIBLE
                        }

                        search_edit!!.clearFocus()
                        imm.hideSoftInputFromWindow(search_edit!!.windowToken, 0)
                        handled = true
                    }

                    override fun onFailure(call: Call<roomDTO>, t: Throwable) {
                        Toast.makeText(context, "검색결과를 찾지 못했습니다", Toast.LENGTH_SHORT).show()
                    }

                })
            }
            handled

        }

        val topAdapter = TopScrollRecyclerAdapter(topData, mainActivity)
        // 상단 스크롤 버튼 리사이클러 뷰
        topRecyclerView.adapter = topAdapter
        topAdapter.setItemClickListener(object : TopScrollRecyclerAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val category = arrayOf(0, 1, 3, 5, 2, 4, 6)
                t_position = category[position]
                spinner!!.visibility = View.VISIBLE
                retrofit("desc", t_position.toString())
            }
        })

        // spinner 이벤트 -> 스피너 이벤트는 상단 카테고리 옵션 번호를 가져와야된다
        // 문제점 스피너는 선택된 상태여서 항사 먼저 호출된다 -> 해결
        // 상단 버튼 클릭을 했을 때
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                val router = arrayOf("desc", "asc", "rank", "hits")

                spinnerData = router[p2]

                // 처음 pick창에서 선택을 한 뒤에 스피너를 선택하는 경우
                if (t_position == null){
                    retrofit(spinnerData, category.toString())
                }
                else {
                    retrofit(spinnerData, t_position.toString())
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mapView = MapView(context)
        // 학교 좌표에서 시작
        mapView!!.setZoomLevel(1, false)
        if (category.toString().toInt() == 0){
            mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.739990, 127.074486), true)
        }
        else if (category.toString().toInt() == 1){
            mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.739529, 127.076988), true)
        }
        else if (category.toString().toInt() == 2){
            mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.738205, 127.078972), true)
        }
        else if (category.toString().toInt() == 3){
            mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.740482, 127.074730), true)
        }
        else if (category.toString().toInt() == 4){
            mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.742554, 127.074226), true)
        }
        else if (category.toString().toInt() == 5){
            mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.740833, 127.072533), true)
        }
        else {
            mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.735134, 127.077866), true)
        }
        mapViewContainer.addView(mapView)
//        mapView?.setMapViewEventListener(this)
//        mapView?.setPOIItemEventListener(this)

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

    fun retrofit(spinnerData : String, position : String){

        call!!.getRoom(u_pk.toInt() ,spinnerData, position).enqueue(object : Callback<roomDTO>{
            override fun onResponse(call: Call<roomDTO>, response: Response<roomDTO>) {
                if (response.isSuccessful){
                    val data = response.body()!!
                    Log.d("mapF", "${data.result[0].heart_user}")
//                    val split = data.result[0].heart_user.split(",")
//                    Log.d("mapF", "${split.size}")
                    roomRecyclerView.adapter = RecyclerAdapter(data.result, mapView!!, marker!!, context!!)
                }
            }

            override fun onFailure(call: Call<roomDTO>, t: Throwable) {
                Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })

    }

}
