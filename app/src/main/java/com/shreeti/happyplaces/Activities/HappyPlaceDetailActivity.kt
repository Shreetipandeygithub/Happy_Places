package com.shreeti.happyplaces.Activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import com.shreeti.happyplaces.R
import com.shreeti.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {

    private lateinit var toolbar_happy_place_detail:Toolbar
    private lateinit var iv_place_image:AppCompatImageView
    private lateinit var tv_description:TextView
    private lateinit var tv_location:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail)
        var happyPlaceDetailModel:HappyPlaceModel?=null

        toolbar_happy_place_detail=findViewById(R.id.toolbar_happy_place_detail)
        tv_location=findViewById(R.id.tv_location)
        tv_description=findViewById(R.id.tv_description)
        iv_place_image=findViewById(R.id.iv_place_image)

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            happyPlaceDetailModel=intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }

        if (happyPlaceDetailModel!=null){
            setSupportActionBar(toolbar_happy_place_detail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title=happyPlaceDetailModel.title

            toolbar_happy_place_detail.setNavigationOnClickListener{
                onBackPressed()
            }

            iv_place_image.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            tv_description.text=happyPlaceDetailModel.description
            tv_location.text=happyPlaceDetailModel.location

        }
    }
}