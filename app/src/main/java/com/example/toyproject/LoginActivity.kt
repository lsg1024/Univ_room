package com.example.toyproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.toyproject.DTO.loginDTO
import com.example.toyproject.DTO.login_data
import com.example.toyproject.`interface`.Retrofit_API
import com.example.toyproject.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private var id : EditText? = null
    private var pw : EditText? = null
    private var l_btn : Button? = null
    private var guest : TextView? = null
    private val call by lazy { Retrofit_API.getInstance() }
    var wait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = binding.id
        pw = binding.pw
        l_btn = binding.loginBtn
        guest = binding.textView

    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onResume() {
        super.onResume()

        val intent = Intent(this@LoginActivity, PickActivity::class.java)

        l_btn?.setOnClickListener {

            val i_id = id?.text.toString()
            val i_pw = pw?.text.toString()

            Log.d("info_data", i_id + i_pw)

            call?.login(login_data(i_id, i_pw))?.enqueue(object : Callback<loginDTO> {
                override fun onResponse(call: Call<loginDTO>, response: Response<loginDTO>) {
                    if (response.isSuccessful){
                        val result : loginDTO? = response.body()

                        Log.d("login_result", "$result")
                        Log.d("user_key_data","${result!!.login_result.user_pk}")

                        startActivity(intent)
                        Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<loginDTO>, t: Throwable) {
                    Log.d("login_fail", "${t.message}")
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                }

            }) // call retrofit2
        } // l_btn

        guest?.setOnClickListener {
            startActivity(intent)
            Toast.makeText(this@LoginActivity, "게스트 모드", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - wait >= 2000) {
            wait = System.currentTimeMillis()
            Snackbar.make(binding.root, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show()
        } else {
            finish()
        }
    }
}