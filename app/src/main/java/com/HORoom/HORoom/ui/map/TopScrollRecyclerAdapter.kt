package com.HORoom.HORoom.ui.map

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HORoom.HORoom.PickActivity
import com.example.toyproject.databinding.ScrollItemBinding

class TopScrollRecyclerAdapter(val dataList: Array<String>, val context: Context) :
    RecyclerView.Adapter<TopScrollRecyclerAdapter.TopScrollHolder>() {

    val intent = Intent(context, PickActivity::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopScrollHolder {
        val binding = ScrollItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TopScrollHolder(binding)
    }

    override fun onBindViewHolder(holder: TopScrollHolder, position: Int) {

        holder.btn_name.text = dataList[position]

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }

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
