package com.example.toyproject.ui.map

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.DTO.roomDTO
import com.example.toyproject.DTO.room_result
import com.example.toyproject.databinding.RListItemBinding

class RoomViewHolder(binding: RListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    val ls_title = binding.listTitle
    val ls_add = binding.listAdd
    val ls_price1 = binding.listPrice1
    val ls_price2 = binding.listPrice2
}

class RecyclerAdapter(private val RoomList : ArrayList<room_result>) : RecyclerView.Adapter<RoomViewHolder>(){

    private var listener : View.OnClickListener? = null


    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {

        Log.d("onBindViewHolder", "onBindViewHolder")
        listener.apply {
            holder.ls_title.text = RoomList[position].roomName
            holder.ls_add.text = RoomList[position].location
            holder.ls_price1.text = RoomList[position].price1.toString()
            holder.ls_price2.text = RoomList[position].price2.toString()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = RListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return RoomList.size
    }
}