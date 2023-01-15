package com.example.toyproject

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.`interface`.Retrofit_API
import com.example.toyproject.databinding.ScrollItemBinding
import com.example.toyproject.ui.map.RecyclerAdapter
import retrofit2.Callback

class TopScrollHolder(binding: ScrollItemBinding) : RecyclerView.ViewHolder(binding.root){
    val btn_name : TextView = binding.scrollBtn
}

class TopScrollRecyclerAdapter(mainActivity: MainActivity, p2 : Int) : RecyclerView.Adapter<TopScrollHolder>(){

    private val call by lazy { Retrofit_API.getInstance() }

    private val N_list = arrayOf("전체보기","정문", "중문", "후문", "서틀뒤", "동막골뒤", "농가마트 앞", "기숙사 근처", "마을회관")

    private val spinner_position = p2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopScrollHolder {
        val binding = ScrollItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopScrollHolder(binding)
    }

    override fun onBindViewHolder(holder: TopScrollHolder, position: Int) {
        holder.btn_name.text = N_list[position]


        // 클릭시 해당 범위 값들 출력 position 1 부터 시작
//       holder.btn_name.setOnClickListener {
//            call?.getRoom_spinner("1")!!.enqueue(object : Callback<>{
//
//            })
//        }

        // 전체보기 postion 0으로 고정

    }

    override fun getItemCount(): Int {
        return N_list.size
    }

}
