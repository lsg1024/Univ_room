package com.example.toyproject.ui.userpage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.DTO.roomDTO
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
    private val mainActivity = context as MainActivity
    private val userAdapter by lazy { UserAdapter(mainActivity, u_key) }
    lateinit var userRecyclerView : RecyclerView
    lateinit var button : Button
    lateinit var u_key : String
    var img : ImageView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel = ViewModelProvider(this)[UserPageViewModel::class.java]

        _binding = FragmentUserpageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        u_key = MySharedPreferences.getUserKey(mainActivity)

        userRecyclerView = binding.userRecyclerView
        userRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        userRecyclerView.adapter = userAdapter

        button = binding.button
        img = binding.imageView2
        userBackground()

//        userRecyclerView.adapter = UserAdapter()

        logout(mainActivity)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun logout(mainActivity : MainActivity){
        button.setOnClickListener {
            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            u_key = MySharedPreferences.removeKey(mainActivity).toString()
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

    fun retrofit(){
        call!!.getHeartList(u_key.toInt()).enqueue(object : Callback<roomDTO>{
            override fun onResponse(call: Call<roomDTO>, response: Response<roomDTO>) {
                if (response.isSuccessful){
                    val result = response.body()!!.result

                    userAdapter.differ.submitList(result)

                }
            }

            override fun onFailure(call: Call<roomDTO>, t: Throwable) {

            }

        })
    }

}