package com.example.toyproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.example.toyproject.databinding.ActivityLoginBinding
import com.example.toyproject.databinding.ActivityMainBinding
import com.example.toyproject.databinding.ActivityPickBinding
import com.example.toyproject.ui.map.MapFragment

class PickActivity : AppCompatActivity() {

    private var _binding: ActivityPickBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPickBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val main_street : AppCompatButton = binding.mainStreet
        val middle : AppCompatButton = binding.middleStreet
        val backDoor : AppCompatButton = binding.backDoor
        val food : AppCompatButton = binding.food
        val bus : AppCompatButton = binding.bus
        val dormitory : AppCompatButton = binding.dormitory
        val total : AppCompatButton = binding.total

        // 여기 하고 있음
        val bundle = Bundle()

        val intent = Intent(this@PickActivity, MainActivity::class.java)

        // 여기서 클릭한 정보를 MapFragment로 전달해야한다 /room 라우터에 전체 + 선택한 버튼 카테고리 정보 주기

        total.setOnClickListener {
            bundle.putString("bundle_data", "0")
            intent.putExtra("mainIntent", bundle)
            startActivity(intent)
            finish()
        }
        main_street.setOnClickListener {
            bundle.putString("bundle_data", "1")
            intent.putExtra("mainIntent", bundle)
            startActivity(intent)
            finish()
        }

        bus.setOnClickListener {
            bundle.putString("bundle_data", "2")
            intent.putExtra("mainIntent", bundle)
            startActivity(intent)
            finish()
        }

        middle.setOnClickListener {
            bundle.putString("bundle_data", "3")
            intent.putExtra("mainIntent", bundle)
            startActivity(intent)
            finish()
        }

        food.setOnClickListener {
            bundle.putString("bundle_data", "4")
            intent.putExtra("mainIntent", bundle)
            startActivity(intent)
            finish()
        }

        backDoor.setOnClickListener {
            bundle.putString("bundle_data", "5")
            intent.putExtra("mainIntent", bundle)
            startActivity(intent)
            finish()
        }

        dormitory.setOnClickListener {
            bundle.putString("bundle_data", "6")
            intent.putExtra("mainIntent", bundle)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}