package com.egyptfwd.loadapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.egyptfwd.loadapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var fileName: String

    private lateinit var status: String

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_detail
        )

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        fileName = intent.getStringExtra("fileName").toString()
        status = intent.getStringExtra("status").toString()

        binding.contentDetail.fileNameTv.text = fileName
        binding.contentDetail.statusTv.text = status


    }

}
