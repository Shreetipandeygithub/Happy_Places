package com.shreeti.happyplaces.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shreeti.happyplaces.Adapter.HappyPlacesAdapter
import com.shreeti.happyplaces.R
import com.shreeti.happyplaces.models.HappyPlaceModel
import com.shreeti.happyplaces.myDatabase.DatabaseHandler
import com.shreeti.happyplaces.utils.SwipeToDeleteCallback
import com.shreeti.happyplaces.utils.SwipeToEditCallback

class MainActivity : AppCompatActivity() {
    private lateinit var rv_happy_places_list:RecyclerView
    private lateinit var tv_no_records_available:TextView
    private lateinit var happyPlace:FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         happyPlace=findViewById(R.id.happyPlaces)
          rv_happy_places_list = findViewById(R.id.rv_happy_places_list)
          tv_no_records_available = findViewById(R.id.tv_no_records_available)

        happyPlace.setOnClickListener{
            val intent=Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent,ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlacesListFromLocalDB()
    }
    private fun setUpHappyPlacesRecyclerView(
        happyPlaceList: ArrayList<HappyPlaceModel>){

        rv_happy_places_list.layoutManager=LinearLayoutManager(this)
        rv_happy_places_list.setHasFixedSize(true)
        val placesAdapter=HappyPlacesAdapter(this,happyPlaceList)
        rv_happy_places_list.adapter=placesAdapter


        placesAdapter.setOnClickListener(object :HappyPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent=Intent(this@MainActivity,HappyPlaceDetailActivity::class.java)

                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }
        })

        val editSwipeHandler=object :SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter=rv_happy_places_list.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(this@MainActivity,viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }
        val editItemTouchHelper=ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_happy_places_list)

        val deleteSwipeHandler=object :SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter=rv_happy_places_list.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                getHappyPlacesListFromLocalDB()
            }
        }
        
        val deleteItemTouchHelper=ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_happy_places_list)
    }

    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler=DatabaseHandler(this)

        val getHappyPlaceList:ArrayList<HappyPlaceModel> =dbHandler.getHappyPlacesList()

        if(getHappyPlaceList.size>0){
            rv_happy_places_list.visibility=View.VISIBLE
            tv_no_records_available.visibility=View.GONE
            setUpHappyPlacesRecyclerView(getHappyPlaceList)
        }else{
            rv_happy_places_list.visibility=View.GONE
            tv_no_records_available.visibility=View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if (resultCode==Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            }else{
                Log.e("Activity","Cancelled or Back pressed")
            }
        }
    }
    companion object{
        var ADD_PLACE_ACTIVITY_REQUEST_CODE=1
        var EXTRA_PLACE_DETAILS="extra_place_detail"
    }
}