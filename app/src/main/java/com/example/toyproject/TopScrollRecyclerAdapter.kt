package com.example.toyproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.DTO.roomDTO
import com.example.toyproject.`interface`.Retrofit_API
import com.example.toyproject.databinding.FragmentMapBinding
import com.example.toyproject.databinding.ScrollItemBinding
import com.example.toyproject.ui.map.MapFragment
import com.example.toyproject.ui.map.RecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.coroutineContext

class TopScrollRecyclerAdapter(val dataList: Array<String>, spinnerData: String, val context: Context, binding: FragmentMapBinding) : RecyclerView.Adapter<TopScrollRecyclerAdapter.TopScrollHolder>(){

    private val call by lazy { Retrofit_API.getInstance() }

    val mainActivity = context as MainActivity
    val sortData : String = spinnerData
    val bundle = Bundle()
    val intent = Intent(context, PickActivity::class.java)
    val RoomRecyclerView : RecyclerView = binding.recyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopScrollHolder {
        val binding = ScrollItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TopScrollHolder(binding)
    }

    override fun onBindViewHolder(holder: TopScrollHolder, position: Int) {

        holder.btn_name.text = dataList[position]

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
        // 클릭시 해당 범위 값들 출력 position 1 부터 시작
//       holder.btn_name.setOnClickListener {
//           Log.d("Top_btn_click", sortData)
//           Log.d("Top_btn_click", dataList[position])
//
//            call?.getRoom("desc", dataList[position])!!.enqueue(object : Callback<roomDTO>{
//                @SuppressLint("NotifyDataSetChanged")
//                override fun onResponse(call: Call<roomDTO>, response: Response<roomDTO>) {
//
//                    if (response.isSuccessful){
//                        val topResult : roomDTO = response.body()!!
//
//                        // 여기서 어떻게 갱신할지???
//
//                    }
//
//                }
//
//                override fun onFailure(call: Call<roomDTO>, t: Throwable) {
//
//                }
//
//            })
//        }

    }
    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

    override fun getItemCount(): Int {
        return dataList.size
    }

    class TopScrollHolder(binding: ScrollItemBinding) : RecyclerView.ViewHolder(binding.root){
        val btn_name : TextView = binding.scrollBtn

    }
}
