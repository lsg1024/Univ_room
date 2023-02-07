package com.HORoom.HORoom

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.HORoom.HORoom.DTO.checkDTO
import com.HORoom.HORoom.DTO.loginDTO
import com.HORoom.HORoom.DTO.login_data
import com.HORoom.HORoom.`interface`.MySharedPreferences
import com.HORoom.HORoom.`interface`.Retrofit_API
import com.example.toyproject.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private var joinView : ConstraintLayout? = null
    private var mainView : ConstraintLayout? = null
    private var id : EditText? = null
    private var pw : EditText? = null
    private var l_btn : Button? = null
//    private var guest : TextView? = null
    private var c_btn : TextView? = null
    private var c_id : EditText? = null
    private var f_pw : EditText? = null
    private var s_pw : EditText? = null
    private var check_btn : AppCompatButton? = null
    private var join_btn : AppCompatButton? = null
    private val call by lazy { Retrofit_API.getInstance() }
    var wait:Long = 0
    var ch_id : Boolean = false
    lateinit var u_key : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainView = binding.mainView
        joinView = binding.joinView
        joinView!!.visibility = View.INVISIBLE
        id = binding.id
        pw = binding.pw
        l_btn = binding.loginBtn
//        guest = binding.textView
        c_btn = binding.cId
        c_id = binding.createId
        f_pw = binding.fPw
        s_pw = binding.sPw
        check_btn = binding.checkId
        join_btn = binding.joinBtn
        u_key = MySharedPreferences.getUserKey(this)

    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onResume() {
        super.onResume()

        val imm: InputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        editTextEnd(c_id!!, imm)
        editTextEnd(s_pw!!, imm)
        c_btn!!.setOnClickListener {
            mainView!!.visibility = View.INVISIBLE
            joinView!!.visibility = View.VISIBLE

            // 아이디 중복 확인 // 회원가입만들기
            check_btn!!.setOnClickListener {

                call!!.idCheck(c_id!!.text.toString()).enqueue(object : Callback<checkDTO>{
                    override fun onResponse(call: Call<checkDTO>, response: Response<checkDTO>) {
                        if (response.isSuccessful){
                            val ch_result = response.body()!!.check_result

                            if (ch_result == false){
                                check_btn!!.text = "중복 확인"
                                Toast.makeText(this@LoginActivity, "아이디가 중복됩니다", Toast.LENGTH_SHORT).show()
                            } else {
                                ch_id = ch_result
                                check_btn!!.text = "확인 완료"
                            }
                        }
                    }

                    override fun onFailure(call: Call<checkDTO>, t: Throwable) {
                    }

                })

            }

            join_btn!!.setOnClickListener {
                if (ch_id == true && f_pw!!.text.toString() == s_pw!!.text.toString()){
                    call!!.createID(login_data(c_id!!.text.toString(), f_pw!!.text.toString())).enqueue(object : Callback<checkDTO>{
                        override fun onResponse(call: Call<checkDTO>, response: Response<checkDTO>, ) {
                            if (response.isSuccessful){

                                Toast.makeText(this@LoginActivity, "회원가입이 완료되었습니다!!", Toast.LENGTH_SHORT).show()
                                mainView!!.visibility = View.VISIBLE
                                joinView!!.visibility = View.INVISIBLE

                            }
                        }

                        override fun onFailure(call: Call<checkDTO>, t: Throwable) {

                        }

                    })
                } else {
                    Toast.makeText(this@LoginActivity, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val intent = Intent(this@LoginActivity, PickActivity::class.java)

        if (u_key.isNotEmpty()){
            startActivity(intent)
            finish()
        } else{
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

                            MySharedPreferences.setUserKey(this@LoginActivity, result.login_result.user_pk)

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

//            guest?.setOnClickListener {
//                startActivity(intent)
//                Toast.makeText(this@LoginActivity, "게스트 모드", Toast.LENGTH_SHORT).show()
//                finish()
//            }
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

    fun editTextEnd(edit : EditText, imm : InputMethodManager){
        edit.setOnEditorActionListener { _, i, _ ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE){
                handled = true
                imm.hideSoftInputFromWindow(edit.windowToken, 0)
                edit.clearFocus()

            }
            handled
        }
    }
}