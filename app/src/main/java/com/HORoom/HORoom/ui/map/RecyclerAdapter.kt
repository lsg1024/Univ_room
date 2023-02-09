package com.HORoom.HORoom.ui.map

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.HORoom.HORoom.DTO.roomHeart
import com.HORoom.HORoom.DTO.room_result
import com.HORoom.HORoom.MainActivity
import com.example.toyproject.R
import com.HORoom.HORoom.`interface`.MySharedPreferences
import com.HORoom.HORoom.`interface`.Retrofit_API
import com.example.toyproject.databinding.RListItemBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomViewHolder(binding: RListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    val ls_title = binding.listTitle
    val ls_add = binding.listAdd
    val ls_price1 = binding.listPrice1
    val ls_price2 = binding.listPrice2
    var ls_img = binding.appCompatImageView
    val stateMove = binding.stateMove
    val heart = binding.heart
}

class RecyclerAdapter(val roomList : ArrayList<room_result>, var mapView: MapView, var marker: MapPOIItem, val context: Context) : RecyclerView.Adapter<RoomViewHolder>() {

    val mainActivity = context as MainActivity
    lateinit var u_pk : String
    private val call by lazy { Retrofit_API.getInstance() }
    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        Log.d("onBindViewHolder", "onBindViewHolder")

        u_pk = MySharedPreferences.getUserKey(context)
        try {
            // 하트 유저가 아무도 없는 경우 개수가 0인 경우
            if (roomList[position].heart_user.isNullOrEmpty()) {
                holder.heart.setImageResource(R.drawable.favorite_24)
                var heartData = true
                holder.heart.setOnClickListener {
                    if (heartData == true) {
                        holder.heart.setImageResource(R.drawable.favorite)
                        postHeart(u_pk.toInt(), roomList[position].room_pk)
                        heartData = false

                    } else {
                        holder.heart.setImageResource(R.drawable.favorite_24)
                        postHeart(u_pk.toInt(), roomList[position].room_pk)
                        heartData = true
                    }
                }
            // 0 초과인 경우
            } else {
                // 리스트에 하트에 자기 유저 키가 있는 경우
                val split = roomList[position].heart_user?.split(",")
                Log.d("onBindViewHoldersplit", "$split")
                var heart = split!!.contains(u_pk)
                // 자신의 하트가 있는 경우
                if (heart == true){
                    holder.heart.setImageResource(R.drawable.favorite)
                    var tf = true
                    holder.heart.setOnClickListener {
                        if (tf == true) {
                            holder.heart.setImageResource(R.drawable.favorite_24)
                            postHeart(u_pk.toInt(), roomList[position].room_pk)
                            tf = false
                        }
                        else {
                            holder.heart.setImageResource(R.drawable.favorite)
                            postHeart(u_pk.toInt(), roomList[position].room_pk)
                            tf = true
                        }
                    }
                }
                // 자신의 하트가 없는 경우
                else {
                    holder.heart.setImageResource(R.drawable.favorite_24)
                    var tf = false
                    holder.heart.setOnClickListener {
                        if (tf == false) {

                            holder.heart.setImageResource(R.drawable.favorite)
                            postHeart(u_pk.toInt(), roomList[position].room_pk)
                            tf = true
                        }
                        else {
                            holder.heart.setImageResource(R.drawable.favorite_24)
                            postHeart(u_pk.toInt(), roomList[position].room_pk)
                            tf = false
                        }
                    }
                }
            }
        } catch (e : NullPointerException){}

        holder.apply {
            ls_title.text = roomList[position].roomName
            ls_add.text = roomList[position].location
            ls_price1.text = roomList[position].price1.toString()
            ls_price2.text = roomList[position].price2.toString()

            Glide.with(holder.ls_img.context)
                .load("http://oceanit.synology.me:8002/image/${roomList[position].room_pk}.png")
                .override(150, 200)
                .into(holder.ls_img)
        }


        // 클릭시 해당 값을 위치로 지도 이동 후 마커 출력
        holder.stateMove.setOnClickListener {
            mapView.setZoomLevel(0, false)
            mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(mainActivity.layoutInflater, position, roomList))
            makerEvent(roomList[position].roomName, roomList[position].latitude, roomList[position].longitude, position)
        }

        // 클릭시 전체 화면으로 전환되어야 된다
        holder.ls_img.setOnClickListener {
            Log.d("click_pos", "$position")
            Log.d("click_pos", "${roomList[position].room_pk}")
            val intent = Intent(context, detailActivity::class.java)
            intent.putExtra("rm_key", roomList[position].room_pk)
            intent.putExtra("title", roomList[position].roomName)
            intent.putExtra("add", roomList[position].location)
            intent.putExtra("price1", roomList[position].price1)
            intent.putExtra("price2", roomList[position].price2)
            context.startActivity(intent)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = RListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    fun makerEvent(itemName: String, latitude: Double, longitude: Double, position: Int) {

        mapView.removeAllPOIItems()
        Log.d("makerEvent", "${position}")
        marker.itemName = itemName
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true)
        marker.markerType = MapPOIItem.MarkerType.CustomImage
        marker.customImageResourceId = R.drawable.vec2
        marker.isShowCalloutBalloonOnTouch = false
        marker.isCustomImageAutoscale = true
        marker.setCustomImageAnchor(0.5f, 1.0f)
        mapView.addPOIItem(marker)
        Log.d("makerEvent", "marker_event")

    }

    fun postHeart(u_pk: Int, r_pk: Int){
        call!!.postHeart(u_pk, r_pk).enqueue(object  : Callback<roomHeart> {
            override fun onResponse(call: Call<roomHeart>, response: Response<roomHeart>) {
                if (response.isSuccessful){
                    val postResult = response.body()!!.result

                    Log.d("retrofit", "$postResult")

                }
            }

            override fun onFailure(call: Call<roomHeart>, t: Throwable) {
                Toast.makeText(context, "오류가 발생했습니다!!", Toast.LENGTH_SHORT).show()
            }

        })
    }
}