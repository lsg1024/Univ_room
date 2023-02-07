package com.HORoom.HORoom.ui.userpage

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.HORoom.HORoom.DTO.roomHeart
import com.HORoom.HORoom.DTO.room_result
import com.example.toyproject.R
import com.HORoom.HORoom.`interface`.Retrofit_API
import com.example.toyproject.databinding.RListItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



// 유저가 누른 찜한 방 목록 정렬
class UserAdapter(val context: Context): RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

    private val call by lazy { Retrofit_API.getInstance() }
    private lateinit var binding: RListItemBinding

    inner class UserViewHolder(private val binding: RListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item : room_result){
            binding.id.text = item.room_pk.toString()
            binding.listTitle.text = item.roomName
            binding.listAdd.text = item.location
            binding.listPrice1.text = item.price1.toString()
            binding.listPrice2.text = item.price2.toString()
            binding.heart.setImageResource(R.drawable.favorite)
            binding.heart.setOnClickListener {
                postHeart(item.heart_user!!.toInt(), item.room_pk)
            }
            Glide.with(binding.appCompatImageView.context)
                .load("http://oceanit.synology.me:8002/image/${item.room_pk}.png")
                .override(150, 200)
                .into(binding.appCompatImageView)

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        binding = RListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<room_result>(){
        override fun areItemsTheSame(oldItem: room_result, newItem: room_result): Boolean {
            return oldItem.room_pk == newItem.room_pk
        }

        override fun areContentsTheSame(oldItem: room_result, newItem: room_result): Boolean {
            return oldItem == newItem
        }
    }

    // 이 콜벡을 레트로핏 내에서 실행할 수 있게 해야된다
    val differ = AsyncListDiffer(this, differCallback)

    fun postHeart(u_pk: Int, r_pk: Int){
        call!!.postHeart(u_pk, r_pk).enqueue(object  : Callback<roomHeart>{
            override fun onResponse(call: Call<roomHeart>, response: Response<roomHeart>) {
                if (response.isSuccessful){
                    val postResult = response.body()!!.result

                    Log.d("retrofit", "$postResult")

                    try {
                        if (postResult == true){
                            Toast.makeText(context, "등록되었습니다!!", Toast.LENGTH_SHORT).show()
                            binding.heart.setImageResource(R.drawable.favorite)
                            binding.numHeart.text = "1"
                        } else{
                            Toast.makeText(context, "취소되었습니다!!", Toast.LENGTH_SHORT).show()
                            binding.heart.setImageResource(R.drawable.favorite_24)
                            binding.numHeart.text = "0"

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