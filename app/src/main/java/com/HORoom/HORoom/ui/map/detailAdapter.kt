package com.HORoom.HORoom.ui.map

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.HORoom.HORoom.DTO.detail_data
import com.HORoom.HORoom.DTO.postReport
import com.HORoom.HORoom.DTO.report_comment
import com.example.toyproject.R
import com.HORoom.HORoom.`interface`.MySharedPreferences
import com.HORoom.HORoom.`interface`.Retrofit_API
import com.example.toyproject.databinding.DetailItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class detailAdapter(val context: Context) : RecyclerView.Adapter<detailAdapter.detailHolder>() {

    private val call by lazy { Retrofit_API.getInstance() }
    private lateinit var binding: DetailItemBinding
    lateinit var dialog_message : String
    var lu_key : Int? = null
    var rm_key : Int? = null
    var c_key : Int? = null
    val u_key = MySharedPreferences.getUserKey(context)

    override fun onBindViewHolder(holder: detailHolder, position: Int) {


        holder.bind(differ.currentList[position])
        holder.delete.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
        holder.setIsRecyclable(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): detailHolder {
        binding = DetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return detailHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // item ??????
    inner class detailHolder(private val binding : DetailItemBinding) : RecyclerView.ViewHolder(binding.root){

        val delete = binding.delete

        @SuppressLint("SimpleDateFormat")
        fun bind(item : detail_data){
            val severTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.date)!!
            val formatTime = calculationTime(severTime.time)

            binding.itemComment.text = item.comment
            binding.ratingBar.rating = item.star
            binding.itemDate.text = formatTime

            lu_key = item.u_pk
            rm_key = item.r_pk
            c_key = item.c_pk
            // ?????? ????????? ???????????????
            binding.notification.setOnClickListener{
                declarationDialog()
            }

            if (u_key.toInt() != item.u_pk){
                binding.delete.visibility = View.INVISIBLE
            } else {
                binding.delete.visibility = View.VISIBLE
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<detail_data>(){

        override fun areItemsTheSame(oldItem: detail_data, newItem: detail_data): Boolean {
            return oldItem.c_pk == newItem.c_pk
        }

        override fun areContentsTheSame(oldItem: detail_data, newItem: detail_data): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    // ??????????????? ????????? ??? ?????? ?????? ?????? ??????
    fun calculationTime(createDateTime: Long): String{
        val nowDateTime = Calendar.getInstance().timeInMillis //?????? ?????? to millisecond
        var value = ""
        val differenceValue = nowDateTime - createDateTime //?????? ?????? - ????????? ??? ??????
        when {
            differenceValue < 60000 -> { //59??? ?????? ?????????
                value = "?????? ???"
            }
            differenceValue < 3600000 -> { //59??? ?????? ?????????
                value =  TimeUnit.MILLISECONDS.toMinutes(differenceValue).toString() + "??? ???"
            }
            differenceValue < 86400000 -> { //23?????? ?????? ?????????
                value =  TimeUnit.MILLISECONDS.toHours(differenceValue).toString() + "?????? ???"
            }
            differenceValue <  604800000 -> { //7??? ?????? ?????????
                value =  TimeUnit.MILLISECONDS.toDays(differenceValue).toString() + "??? ???"
            }
            differenceValue < 2419200000 -> { //3??? ?????? ?????????
                value =  (TimeUnit.MILLISECONDS.toDays(differenceValue)/7).toString() + "??? ???"
            }
            differenceValue < 31556952000 -> { //12?????? ?????? ?????????
                value =  (TimeUnit.MILLISECONDS.toDays(differenceValue)/30).toString() + "?????? ???"
            }
            else -> { //??? ???
                value =  (TimeUnit.MILLISECONDS.toDays(differenceValue)/365).toString() + "??? ???"
            }
        }
        return value
    }
    // ???????????????
    fun declarationDialog(){
        val dDialog = Dialog(context)
        dDialog.setContentView(R.layout.declaration_dialog)
        dDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        Log.d("requestDialog", "${u_key}  ${rm_key}")
        val requestDialog = Dialog(context)
        requestDialog.setContentView(R.layout.request_dialog)
        requestDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestDialog.setCancelable(false)
        requestDialog.show()

        val final_title : TextView = requestDialog.findViewById(R.id.final_title)
        val accept : AppCompatButton = requestDialog.findViewById(R.id.accept)
        val cancel : AppCompatButton = requestDialog.findViewById(R.id.cancel)

        final_title.text = dialog_message

        accept.setOnClickListener {
            // ????????? ?????? ??????
            call!!.postReport(u_key.toInt(), c_key!!, report_comment(dialog_message)).enqueue(object : Callback<postReport>{
                override fun onResponse(call: Call<postReport>, response: Response<postReport>) {
                    if (response.isSuccessful){
                        val dialog_result = response.body()!!.result
                        Log.d("reportSuccess", dialog_result)
                    }
                }

                override fun onFailure(call: Call<postReport>, t: Throwable) {
                }

            })
            Toast.makeText(context, "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show()
            requestDialog.dismiss()
        }

        cancel.setOnClickListener {
            requestDialog.dismiss()
        }

    }

    // (2) ????????? ???????????????
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) ???????????? ?????? ??? ????????? ??????
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener??? ????????? ?????? ??????
    private lateinit var itemClickListener : OnItemClickListener

}
