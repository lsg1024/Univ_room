package com.example.toyproject.ui.userpage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.DTO.roomDTO
import com.example.toyproject.DTO.roomHeart
import com.example.toyproject.DTO.room_result
import com.example.toyproject.LoginActivity
import com.example.toyproject.MainActivity
import com.example.toyproject.R
import com.example.toyproject.`interface`.MySharedPreferences
import com.example.toyproject.`interface`.Retrofit_API
import com.example.toyproject.databinding.FragmentUserpageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserPageFragment : Fragment() {

    private var _binding: FragmentUserpageBinding? = null
    private val binding get() = _binding!!
    private val call by lazy { Retrofit_API.getInstance() }
    private var userAdapter : UserAdapter? = null
    lateinit var userRecyclerView : RecyclerView
    lateinit var button : Button
    lateinit var u_key : String
    private var quest : TextView?= null
    var r_pk : ArrayList<room_result>? = null
    var img : ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel = ViewModelProvider(this)[UserPageViewModel::class.java]

        _binding = FragmentUserpageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mainActivity = context as MainActivity

        u_key = MySharedPreferences.getUserKey(mainActivity)

        button = binding.button
        img = binding.imageView2
        quest = binding.quest

        userRecyclerView = binding.userRecyclerView
        userRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        userBackground()
        logout(mainActivity)

        return root
    }

    override fun onResume() {
        super.onResume()
        val mainActivity = context as MainActivity
        userAdapter = UserAdapter(mainActivity)
        userRecyclerView.adapter = userAdapter
        listRetrofit()

        quest!!.setOnClickListener {
            val email = Intent(Intent.ACTION_SEND)
            email.type = "plain/text"
            val address = arrayOf("knk7691@gmail.com")
            email.putExtra(Intent.EXTRA_EMAIL, address)
            email.putExtra(Intent.EXTRA_SUBJECT, "[Horoom] 제목")
            email.putExtra(Intent.EXTRA_TEXT, "수정, 삭제 기타 요청 사항을 입력해주세요\n* 이름과 이메일 필수 *")
            startActivity(email)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun logout(mainActivity : MainActivity){
        val intent = Intent(context, LoginActivity::class.java)
        if (u_key.isEmpty()){
            button.text = "로그인"
            button.setOnClickListener{
                startActivity(intent)
            }
        } else {
            button.text = "로그아웃"
            button.setOnClickListener {
                Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                u_key = MySharedPreferences.removeKey(mainActivity).toString()
                startActivity(intent)
            }
        }
    }

    fun userBackground(){
        if (u_key.isNotEmpty()){
            img!!.setImageResource(R.drawable.school)
        }
        else {
            img!!.setImageResource(R.drawable.school2)
        }
    }

    fun listRetrofit(){
        call!!.getHeartList(u_key.toInt()).enqueue(object : Callback<roomDTO>{
            override fun onResponse(call: Call<roomDTO>, response: Response<roomDTO>) {
                if (response.isSuccessful){
                    val result = response.body()!!.result

                    userAdapter!!.differ.submitList(result)

                }
            }

            override fun onFailure(call: Call<roomDTO>, t: Throwable) {

            }

        })
    }

}