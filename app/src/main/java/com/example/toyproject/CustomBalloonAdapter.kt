package com.example.toyproject

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.toyproject.DTO.room_result
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem

class CustomBalloonAdapter(inflater: LayoutInflater, position: Int, val RoomList : ArrayList<room_result>) : CalloutBalloonAdapter {

    @SuppressLint("InflateParams")
    val lCalloutBalloon = inflater.inflate(R.layout.customballoon, null)
    val name : TextView = lCalloutBalloon.findViewById(R.id.room_name)
    val address : TextView = lCalloutBalloon.findViewById(R.id.address)
    val price : TextView = lCalloutBalloon.findViewById(R.id.price1)
    var lposition : Int? = position

    override fun getCalloutBalloon(p0: MapPOIItem?): View {

        name.text = RoomList[lposition!!].roomName
        address.text = RoomList[lposition!!].location
        price.text = RoomList[lposition!!].price1.toString()
        Log.d("getCalloutBalloon", "${RoomList!![lposition!!].roomName}")
        Log.d("getCalloutBalloon", "${lposition}")
        Log.d("getCalloutBalloon", "${name.text}")
        return lCalloutBalloon
    }

    override fun getPressedCalloutBalloon(p0: MapPOIItem?): View {
        // 마커 클릭 후 적혀 있는 내용을 클릭했을 때 나오는 이밴트이므로 클릭했을 때 상세 정보 페이지
        return lCalloutBalloon
    }

}