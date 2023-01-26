package com.example.toyproject.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.CustomBalloonAdapter
import com.example.toyproject.DTO.roomDTO
import com.example.toyproject.DTO.room_result
import com.example.toyproject.MainActivity
import com.example.toyproject.R
import com.example.toyproject.databinding.RListItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class RoomViewHolder(binding: RListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    val cardview = binding.cardView
    val ls_title = binding.listTitle
    val ls_add = binding.listAdd
    val ls_price1 = binding.listPrice1
    val ls_price2 = binding.listPrice2
    val ls_img = binding.appCompatImageView
    val stateMove = binding.stateMove
    val chat = binding.chat
    val heart = binding.heart

}

class RecyclerAdapter(val RoomList : ArrayList<room_result>, var mapView: MapView, var marker: MapPOIItem, context: Context) : RecyclerView.Adapter<RoomViewHolder>() {

    val mainActivity = context as MainActivity

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {

        Log.d("onBindViewHolder", "onBindViewHolder")

        holder.apply {
            ls_title.text = RoomList[position].roomName
            ls_add.text = RoomList[position].location
            ls_price1.text = RoomList[position].price1.toString()
            ls_price2.text = RoomList[position].price2.toString()
        }

        // 클릭시 해당 값을 위치로 지도 이동 후 마커 출력
        holder.stateMove.setOnClickListener {
            mapView.setZoomLevel(0, false)
            mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(mainActivity.layoutInflater, position, RoomList))
            makerEvent(RoomList[position].roomName, RoomList[position].latitude, RoomList[position].longitude, position)
        }

        // 클릭시 전체 화면으로 전환되어야 된다
        holder.cardview.setOnClickListener {
            Log.d("cardView", "cardView${position}")
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = RListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return RoomList.size
    }

    inner class be

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


}