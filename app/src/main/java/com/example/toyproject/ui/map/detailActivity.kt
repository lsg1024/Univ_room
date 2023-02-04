package com.example.toyproject.ui.map

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.toyproject.DTO.detailDTO
import com.example.toyproject.DTO.detail_comment
import com.example.toyproject.DTO.detail_data
import com.example.toyproject.DTO.detail_result_comment
import com.example.toyproject.MainActivity
import com.example.toyproject.R
import com.example.toyproject.`interface`.Retrofit_API
import com.example.toyproject.databinding.ActivityDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class detailActivity : AppCompatActivity() {

    private val call by lazy { Retrofit_API.getInstance() }
    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding!!

    lateinit var title_img : ImageView
    lateinit var title : TextView
    lateinit var add : TextView
    lateinit var price1 : TextView
    lateinit var price2 : TextView
    lateinit var star_btn : AppCompatButton

    lateinit var detailDialog : Dialog

    private val default_num = 0
    private var dialog_text : String? = null

    var r_key : Int? = null
    lateinit var detailRecyclerView: RecyclerView
    private val detailAdapter by lazy { detailAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailRecyclerView = binding.recyclerView2
        detailRecyclerView.layoutManager = LinearLayoutManager(this)
        detailRecyclerView.adapter = detailAdapter

        title_img = binding.imageView
        title = binding.detailName
        add = binding.detailAddress
        price1 = binding.detailPrice
        price2 = binding.detailPrice2
        star_btn = binding.starBtn

        detailDialog = Dialog(this)
        detailDialog.setContentView(R.layout.detail_dialog)

        intent_data()
        dialogEvent()

    }

    override fun onResume() {
        super.onResume()
        call_Retrofit()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    fun intent_data(){
        // RecyclerAdapter 전송 데이터
        intent.apply {
            r_key = getIntExtra("rm_key", default_num)
            title.text = getStringExtra("title")!!
            add.text = getStringExtra("add")!!
            price1.text = getIntExtra("price1", default_num).toString()
            price2.text = getIntExtra("price2",default_num).toString()
        }

        Glide.with(title_img.context)
            .load("http://oceanit.synology.me:8002/image/${r_key}.png")
            .into(title_img)
    }

    // 메인 정보를 받아서 리사이클려 어뎁터에 전달해준다
    fun call_Retrofit(){
        call!!.getComment(r_key).enqueue(object : Callback<detailDTO>{

            override fun onResponse(call: Call<detailDTO>, response: Response<detailDTO>) {
                if (response.isSuccessful) {
                    val detail_data = response.body()!!.rm_result

                    detailAdapter.differ.submitList(detail_data)

                } else {
                    Toast.makeText(this@detailActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<detailDTO>, t: Throwable) {

            }

        })
    }

    // 별점 주는 버튼 클릭 시 다이얼로그 작동
    fun dialogEvent(){

        val imm: InputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        star_btn.setOnClickListener {
            detailDialog.show()
            detailDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val dialog_btn : AppCompatButton = detailDialog.findViewById(R.id.dialog_btn)
            val dialog_rtb : RatingBar = detailDialog.findViewById(R.id.dialog_rtb)
            val dialog_et : EditText = detailDialog.findViewById(R.id.dialog_et)

            dialog_rtb.setOnRatingBarChangeListener { _, fl, _ ->
                dialog_rtb.rating = fl
            }

            dialog_et.setOnEditorActionListener { textView, i, keyEvent ->
                var handled = false
                if (i == EditorInfo.IME_ACTION_DONE){
                    handled = true
                    dialog_text = dialog_et.text.toString()
                    imm.hideSoftInputFromWindow(dialog_et.windowToken, 0)
                    dialog_et.clearFocus()
                    Log.d("dialog_et", "$dialog_text")
                    Log.d("dialog_et", "${dialog_rtb.rating}")
                }
                handled
            }

            dialog_btn.setOnClickListener {
                if ((dialog_et != null) && (dialog_rtb.rating != 0.0f)){
                    call!!.postComment(detail_comment(r_key!!, 1, dialog_rtb.rating, dialog_text!!)).enqueue(object : Callback<detail_result_comment>{
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(call: Call<detail_result_comment>, response: Response<detail_result_comment>, ) {
                            if (response.isSuccessful){
                                call_Retrofit()
                                detailDialog.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<detail_result_comment>, t: Throwable) {

                        }

                    })
                }
                else {
                    Toast.makeText(this, "입력되지 않은 값이 있습니다!!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}