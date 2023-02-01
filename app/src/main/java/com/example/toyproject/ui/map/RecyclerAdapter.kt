package com.example.toyproject.ui.map

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatDrawableManager.preload
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.toyproject.DTO.room_result
import com.example.toyproject.MainActivity
import com.example.toyproject.R
import com.example.toyproject.databinding.RListItemBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class RoomViewHolder(binding: RListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    val cardview = binding.cardView
    val ls_title = binding.listTitle
    val ls_add = binding.listAdd
    val ls_price1 = binding.listPrice1
    val ls_price2 = binding.listPrice2
    var ls_img = binding.appCompatImageView
    val stateMove = binding.stateMove
    val chat = binding.chat
    val heart = binding.heart
    val n_heart = binding.numHeart

}

class RecyclerAdapter(val roomList : ArrayList<room_result>, var mapView: MapView, var marker: MapPOIItem, val context: Context) : RecyclerView.Adapter<RoomViewHolder>() {

    val mainActivity = context as MainActivity

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        Log.d("onBindViewHolder", "onBindViewHolder")

        val img = roomList[position]

        holder.apply {
            ls_title.text = roomList[position].roomName
            ls_add.text = roomList[position].location
            ls_price1.text = roomList[position].price1.toString()
            ls_price2.text = roomList[position].price2.toString()
            n_heart.text = roomList[position].heart.toString()

//            ls_img = roomList[position]

//            Glide.with(context).load(img.image)
//                .override(150, 200)
//                .into(ls_img)
        }

//        if (position <= roomList.size) {
//            val endPosition = if (position + 5 > roomList.size) {
//                roomList.size
//            }
//            else {
//                position + 5
//            }
//            roomList.subList(position, endPosition).map { it.image }.forEach {
//                preload(context, it)
//            }
//        }

        // 클릭시 하트 이벤트
//        if (heartResult){
//
//        }

        // 클릭시 해당 값을 위치로 지도 이동 후 마커 출력
        holder.stateMove.setOnClickListener {
            mapView.setZoomLevel(0, false)
            mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(mainActivity.layoutInflater, position, roomList))
            makerEvent(roomList[position].roomName, roomList[position].latitude, roomList[position].longitude, position)
        }

        // 클릭시 전체 화면으로 전환되어야 된다
        holder.cardview.setOnClickListener {
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

    fun preload(context: Context, url : String){
        Glide.with(context).load(url)
            .preload(150, 200)
    }

    fun <T> heartData(array: Array<T>, target: T): Boolean {
        return target in array
    }

    fun heartResult(){

    }
}