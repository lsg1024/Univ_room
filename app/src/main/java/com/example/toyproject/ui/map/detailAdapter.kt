package com.example.toyproject.ui.map

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.DTO.detail_data
import com.example.toyproject.MainActivity
import com.example.toyproject.R
import com.example.toyproject.databinding.DetailItemBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class detailAdapter(val context: Context) : RecyclerView.Adapter<detailAdapter.detailHolder>() {

    private lateinit var binding: DetailItemBinding
    lateinit var dialog_message : String

    override fun onBindViewHolder(holder: detailHolder, position: Int) {

        holder.bind(differ.currentList[position])
        holder.setIsRecyclable(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): detailHolder {
        binding = DetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return detailHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class detailHolder(private val binding : DetailItemBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SimpleDateFormat")
        fun bind(item : detail_data){
            val severTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.date)!!
            val formatTime = calculationTime(severTime.time)
            binding.itemComment.text = item.comment
            binding.ratingBar.rating = item.star
            binding.itemDate.text = formatTime
            // 클릭 이벤트 변경해야됨
            binding.notification.setOnClickListener{
                declarationDialog(context)
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<detail_data>(){
        override fun areItemsTheSame(oldItem: detail_data, newItem: detail_data): Boolean {
            return oldItem.comment == newItem.comment && oldItem.star == newItem.star && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: detail_data, newItem: detail_data): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    // 유튜브같은 몇시간 전 혹은 방금 시간 표현
    fun calculationTime(createDateTime: Long): String{
        val nowDateTime = Calendar.getInstance().timeInMillis //현재 시간 to millisecond
        var value = ""
        val differenceValue = nowDateTime - createDateTime //현재 시간 - 비교가 될 시간
        when {
            differenceValue < 60000 -> { //59초 보다 적다면
                value = "방금 전"
            }
            differenceValue < 3600000 -> { //59분 보다 적다면
                value =  TimeUnit.MILLISECONDS.toMinutes(differenceValue).toString() + "분 전"
            }
            differenceValue < 86400000 -> { //23시간 보다 적다면
                value =  TimeUnit.MILLISECONDS.toHours(differenceValue).toString() + "시간 전"
            }
            differenceValue <  604800000 -> { //7일 보다 적다면
                value =  TimeUnit.MILLISECONDS.toDays(differenceValue).toString() + "일 전"
            }
            differenceValue < 2419200000 -> { //3주 보다 적다면
                value =  (TimeUnit.MILLISECONDS.toDays(differenceValue)/7).toString() + "주 전"
            }
            differenceValue < 31556952000 -> { //12개월 보다 적다면
                value =  (TimeUnit.MILLISECONDS.toDays(differenceValue)/30).toString() + "개월 전"
            }
            else -> { //그 외
                value =  (TimeUnit.MILLISECONDS.toDays(differenceValue)/365).toString() + "년 전"
            }
        }
        return value
    }

    // 다이얼로그
    fun declarationDialog(context : Context){
        val dDialog = Dialog(context)
        dDialog.setContentView(R.layout.declaration_dialog)

        dDialog.show()

        val f_text : TextView = dDialog.findViewById(R.id.f_text)
        val s_text : TextView = dDialog.findViewById(R.id.s_text)
        val t_text : TextView = dDialog.findViewById(R.id.t_text)

        f_text.setOnClickListener {
            dDialog.dismiss()
            dialog_message = f_text.text.toString()
            requestDialog()
        }

        s_text.setOnClickListener {
            dDialog.dismiss()
            dialog_message = s_text.text.toString()
            requestDialog()
        }

        t_text.setOnClickListener {
            dDialog.dismiss()
            dialog_message = t_text.text.toString()
            requestDialog()
        }


    }

    fun requestDialog(){
        val requestDialog = Dialog(context)
        requestDialog.setContentView(R.layout.request_dialog)
        requestDialog.setCancelable(false)
        requestDialog.show()

        val final_title : TextView = requestDialog.findViewById(R.id.final_title)
        val accept : AppCompatButton = requestDialog.findViewById(R.id.accept)
        val cancel : AppCompatButton = requestDialog.findViewById(R.id.cancel)

        final_title.text = dialog_message

        accept.setOnClickListener {
            // 서버에 신고 접수
            Toast.makeText(context, "신고 접수가 완료되었습니다", Toast.LENGTH_SHORT).show()
        }

        cancel.setOnClickListener {
            requestDialog.dismiss()
        }

    }

}
