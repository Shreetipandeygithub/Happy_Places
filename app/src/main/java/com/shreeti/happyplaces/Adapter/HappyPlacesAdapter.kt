package com.shreeti.happyplaces.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.shreeti.happyplaces.Activities.AddHappyPlaceActivity
import com.shreeti.happyplaces.Activities.MainActivity
import com.shreeti.happyplaces.R
import com.shreeti.happyplaces.models.HappyPlaceModel
import com.shreeti.happyplaces.myDatabase.DatabaseHandler
import de.hdodenhof.circleimageview.CircleImageView

open class HappyPlacesAdapter(
    private val context:Context,
    private var list:ArrayList<HappyPlaceModel>
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListener:OnClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_happy_place,
                parent,
                false
            )
        )
    }

    //to bind onClicklistener
    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener=onClickListener
    }

    fun notifyEditItem(activity:Activity,position: Int, requestCode:Int){
        val intent=Intent(context,AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS,list[position])
        activity.startActivityForResult(intent,requestCode)
        notifyItemChanged(position)

    }

    fun removeAt(position: Int){
        val dbHandler=DatabaseHandler(context)
        val isDelete=dbHandler.deleteHappyPlace(list[position])
        if (isDelete>0){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]


        if (holder is MyViewHolder){
            holder.iv_place_image.setImageURI(Uri.parse(model.image))
            holder.tvTitle.text=model.title
            holder.tvDescription.text=model.description

            holder.itemView.setOnClickListener{
                if (onClickListener!=null){
                    onClickListener!!.onClick(position,model)
                }
            }

        }

    }

    interface OnClickListener{
        fun onClick(position:Int,model:HappyPlaceModel)
    }

    class MyViewHolder(view:View):RecyclerView.ViewHolder(view){
        val iv_place_image:CircleImageView=view.findViewById(R.id.iv_place_image)
        val tvTitle:TextView=view.findViewById(R.id.tvTitle)
        val tvDescription:TextView=view.findViewById(R.id.tvDescription)
    }
}