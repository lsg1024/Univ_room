package com.example.toyproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.example.toyproject.databinding.ActivityLoginBinding
import com.example.toyproject.databinding.ActivityMainBinding
import com.example.toyproject.databinding.ActivityPickBinding

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
        val townHall : AppCompatButton = binding.townHall
        val doogmak : AppCompatButton = binding.domak
        val bus : AppCompatButton = binding.bus
        val dormitory : AppCompatButton = binding.dormitory

        // 여기서 클릭한 정보를 MapFragment로 전달해야한다


    }
}