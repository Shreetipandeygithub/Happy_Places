package com.shreeti.happyplaces.Activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shreeti.happyplaces.R
import com.shreeti.happyplaces.models.HappyPlaceModel
import com.shreeti.happyplaces.myDatabase.DatabaseHandler
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(),View.OnClickListener{
    @RequiresApi(Build.VERSION_CODES.N)
    private var cal=Calendar.getInstance()
    private lateinit var datesetListener: DatePickerDialog.OnDateSetListener

    private lateinit var et_date:AppCompatEditText

    private lateinit var toolbar_add_happy_place: Toolbar


    private lateinit var tv_add_image:TextView
    private lateinit var iv_place_image:AppCompatImageView
    private lateinit var btn_save:Button
    private lateinit var et_title:AppCompatEditText
    private lateinit var et_description:AppCompatEditText
    private lateinit var et_location:AppCompatEditText




    //important btn for saving images in internal storage
    private var saveImageToInternalStorage:Uri?=null
    private var mLatitude:Double=0.0
    private var mLongitude:Double=0.0

    private var mHappyPlaceDetails:HappyPlaceModel?=null



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)
        //initialize button text....
        et_date =findViewById(R.id.et_date)
        tv_add_image=findViewById(R.id.tv_add_image)
        toolbar_add_happy_place= findViewById(R.id.toolbar_add_happy_place)
        iv_place_image=findViewById(R.id.iv_place_image)
        btn_save=findViewById(R.id.btn_save)
        et_title=findViewById(R.id.et_title)
        et_description=findViewById(R.id.et_description)
        et_location=findViewById(R.id.et_location)


        setSupportActionBar(toolbar_add_happy_place)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_add_happy_place.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mHappyPlaceDetails=intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }

        showDatePicker()
        et_date.setOnClickListener{
            showDatePicker()
        }

        if (mHappyPlaceDetails!=null){
            supportActionBar?.title="Edit Happy Place"

            et_title.setText(mHappyPlaceDetails!!.title)
            et_description.setText(mHappyPlaceDetails!!.description)
            et_date.setText(mHappyPlaceDetails!!.date)
            et_location.setText(mHappyPlaceDetails!!.location)
            mLatitude=mHappyPlaceDetails!!.latitude
            mLongitude=mHappyPlaceDetails!!.longitude

            saveImageToInternalStorage=Uri.parse(
                mHappyPlaceDetails!!.image)

            iv_place_image.setImageURI(saveImageToInternalStorage)
            btn_save.text="UPDATE"
        }


        tv_add_image.setOnClickListener(this)
        btn_save.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val myformat = dateFormat.format(selectedDate.time)
                val sdf=SimpleDateFormat(myformat,Locale.getDefault())
                et_date.setText(sdf.format(cal.time).toString())
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.tv_add_image ->{
                val pictureDialog=AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems=arrayOf("select Picture from Gallery","Capture photo using Camera")
                pictureDialog.setItems(pictureDialogItems){
                    dialog,which->
                    when(which){
                        0->choosePhotoFromGallery()
                        1->takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }

            R.id.btn_save->{
                //store or save a data
                when{
                    et_title.text.isNullOrEmpty()->{
                        Toast.makeText(this,"Please enter a title",Toast.LENGTH_SHORT).show()
                    }
                    et_description.text.isNullOrEmpty()->{
                        Toast.makeText(this,"Please enter a description",Toast.LENGTH_SHORT).show()
                    }
                    et_location.text.isNullOrEmpty()->{
                        Toast.makeText(this,"Please enter a location",Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternalStorage==null->{
                        Toast.makeText(this,"Please select an image",Toast.LENGTH_SHORT).show()
                    }else->{

                        val happPlaceModel=HappyPlaceModel(
                            if (mHappyPlaceDetails==null) 0 else mHappyPlaceDetails!!.id,
                            et_title.text.toString(),
                            saveImageToInternalStorage.toString(),
                            et_description.text.toString(),
                            et_date.text.toString(),
                            et_location.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                    val  dbHandler=DatabaseHandler(this)
                    if (mHappyPlaceDetails==null){
                        val addHappyPlace=dbHandler.addHappyPlace(happPlaceModel)
                        if(addHappyPlace > 0){
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }else{
                        val updateHappyPlace=dbHandler.updateHappyPlace(happPlaceModel)
                        if(updateHappyPlace > 0){
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK){
            if (requestCode== GALLERY){
                if (data!=null){
                    val contentURI=data.data
                    try {
                        val selectedImageBitMap=MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)

                        saveImageToInternalStorage=saveImagesTointernalStorage(selectedImageBitMap)

                        Log.e("Saved Image: ", "Path:: $saveImageToInternalStorage")
                        iv_place_image.setImageBitmap(selectedImageBitMap)
                    }catch (e:IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity,"Failed to Load image from Gallery",Toast.LENGTH_SHORT).show()
                    }
                }
            }else if(requestCode== CAMERA){
                val thumbnail:Bitmap=data!!.extras!!.get("data") as Bitmap

                saveImageToInternalStorage = saveImagesTointernalStorage(thumbnail)

                Log.e("Saved Image: ", "Path:: $saveImageToInternalStorage")

                iv_place_image.setImageBitmap(thumbnail)

            }
        }
    }


    private fun takePhotoFromCamera(){
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener{
            override fun onPermissionsChecked(
                report: MultiplePermissionsReport?)
            {
                if (report!!.areAllPermissionsGranted()){
                    val galleryIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(galleryIntent, CAMERA)
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:MutableList<PermissionRequest>,token: PermissionToken)
            {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }


    private fun choosePhotoFromGallery(){
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener{
            override fun onPermissionsChecked( report: MultiplePermissionsReport?)
            {
                if (report!!.areAllPermissionsGranted()){
                    val galleryIntent=Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:MutableList<PermissionRequest>,token: PermissionToken)
            {
                showRationalDialogForPermissions()

            }
        }).onSameThread().check()
    }

    private fun saveImagesTointernalStorage(bitmap: Bitmap): Uri{
        val wrapper=ContextWrapper(applicationContext)
        var file=wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file= File(file,"${UUID.randomUUID()}.jpg")

        try {
            val stream:OutputStream=FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }



    companion object{
        private const val GALLERY=1
        private const val CAMERA=2
        private const val IMAGE_DIRECTORY="happyPlacesImages"

    }


    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("it looks like you have turned off permission required for this feature.It can be enabled under the"+
                " Application settings")
            .setPositiveButton("GO TO SETTINGS")
            {_,_->
                try {
                    val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri=Uri.fromParts("package",packageName,null)
                    intent.data=uri
                    startActivity(intent)
                }catch (e:ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("cancel"){
                dialog, _->
                dialog.dismiss()
            }.show()
    }

}