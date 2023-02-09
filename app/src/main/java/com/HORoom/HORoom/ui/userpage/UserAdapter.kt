package com.HORoom.HORoom.ui.userpage

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
import com.HORoom.HORoom.`interface`.MySharedPreferences
import com.example.toyproject.R
import com.HORoom.HORoom.`interface`.Retrofit_API
import com.HORoom.HORoom.ui.map.detailActivity
import com.example.toyproject.databinding.RListItem2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewHolder(binding: RListItem2Binding) : RecyclerView.ViewHolder(binding.root) {

    val ls_title = binding.listTitle
    val ls_add = binding.listAdd
    val ls_price1 = binding.listPrice1
    val ls_price2 = binding.listPrice2
    var ls_img = binding.appCompatImageView
    val heart = binding.heart

}

// 유저가 누른 찜한 방 목록 정렬
class UserAdapter(val context: Context, val roomList : ArrayList<room_result>): RecyclerView.Adapter<UserViewHolder>(){

    private val call by lazy { Retrofit_API.getInstance() }
    val u_key = MySharedPreferences.getUserKey(context).toInt()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = RListItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

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

        holder.heart.setImageResource(R.drawable.favorite)
        holder.heart.setOnClickListener {
            postHeart(u_key, roomList[position].room_pk, holder)
        }

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

    override fun getItemCount(): Int {
        return roomList.size
    }

//    private val differCallback = object : DiffUtil.ItemCallback<room_result>(){
//        override fun areItemsTheSame(oldItem: room_result, newItem: room_result): Boolean {
//            return oldItem.room_pk == newItem.room_pk
//        }
//
//        override fun areContentsTheSame(oldItem: room_result, newItem: room_result): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    // 이 콜벡을 레트로핏 내에서 실행할 수 있게 해야된다
//    val differ = AsyncListDiffer(this, differCallback)

    fun postHeart(u_pk: Int, r_pk: Int, holder: UserViewHolder){
        call!!.postHeart(u_pk, r_pk).enqueue(object  : Callback<roomHeart>{
            override fun onResponse(call: Call<roomHeart>, response: Response<roomHeart>) {
                if (response.isSuccessful){
                    val postResult = response.body()!!.result

                    Log.d("retrofit", "$postResult")

                    try {
                        if (postResult == true){
                            Toast.makeText(context, "등록되었습니다!!", Toast.LENGTH_SHORT).show()
                            holder.heart.setImageResource(R.drawable.favorite)
                        } else{
                            Toast.makeText(context, "취소되었습니다!!", Toast.LENGTH_SHORT).show()
                            holder.heart.setImageResource(R.drawable.favorite_24)

                        }
                    } catch (e: NullPointerException){

                    }

                }
            }

            override fun onFailure(call: Call<roomHeart>, t: Throwable) {
                Toast.makeText(context, "오류가 발생했습니다!!", Toast.LENGTH_SHORT).show()
            }

        })
    }

}