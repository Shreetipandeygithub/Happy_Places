package com.shreeti.happyplaces.myDatabase

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.os.persistableBundleOf
import com.shreeti.happyplaces.models.HappyPlaceModel

class DatabaseHandler(context: Context):
    SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){
        companion object{

            private const val DATABASE_VERSION=1
            private const val DATABASE_NAME="HappyPlacesDatabase"
            private const val TABLE_HAPPY_PLACE="HappyPlacesTable"

            //ALL COLUMN NAMES
            private const val KEY_ID ="_id"
            private const val KEY_TITLE="title"
            private const val KEY_IMAGE="image"
            private const val KEY_DESCRIPTION="description"
            private const val KEY_DATE="date"
            private const val KEY_LOCATION="location"
            private const val KEY_LATITUDE="latitude"
            private const val KEY_LONGITUDE="longitude"

        }

    override fun onCreate(p0: SQLiteDatabase?) {
        val CREATE_HAPPY_PLACE_TABLE=(" CREATE TABLE "+ TABLE_HAPPY_PLACE +"("
                + KEY_ID+" INTEGER PRIMARY KEY,"
                + KEY_TITLE+" TEXT,"
                + KEY_IMAGE+" TEXT,"
                + KEY_DESCRIPTION+" TEXT,"
                + KEY_DATE+" TEXT,"
                + KEY_LOCATION+" TEXT,"
                + KEY_LATITUDE+" TEXT,"
                + KEY_LONGITUDE+" TEXT)")
        p0?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(p0)
    }

    //create
    fun addHappyPlace(happyPlace:HappyPlaceModel):Long{
        val  db=this.writableDatabase

        val contentValues=ContentValues()
        contentValues.put(KEY_TITLE,happyPlace.title)
        contentValues.put(KEY_IMAGE,happyPlace.image)
        contentValues.put(KEY_DESCRIPTION,happyPlace.description)
        contentValues.put(KEY_DATE,happyPlace.date)
        contentValues.put(KEY_LOCATION,happyPlace.location)
        contentValues.put(KEY_LATITUDE,happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE,happyPlace.longitude)

        //inserting Row
        val result=db.insert(TABLE_HAPPY_PLACE,null,contentValues)
        //2nd argument is containing nullColumnHack

        db.close()  //cloasing database connection
        return result
    }

    fun updateHappyPlace(happyPlace: HappyPlaceModel):Int{
        val  db=this.writableDatabase

        val contentValues=ContentValues()
        contentValues.put(KEY_TITLE,happyPlace.title)
        contentValues.put(KEY_IMAGE,happyPlace.image)
        contentValues.put(KEY_DESCRIPTION,happyPlace.description)
        contentValues.put(KEY_DATE,happyPlace.date)
        contentValues.put(KEY_LOCATION,happyPlace.location)
        contentValues.put(KEY_LATITUDE,happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE,happyPlace.longitude)

        //update
        val success=db.update(TABLE_HAPPY_PLACE,contentValues, KEY_ID+"=" + happyPlace.id,null)

        db.close()
        return success
    }

    fun deleteHappyPlace(happyPlace: HappyPlaceModel):Int{
        val db=this.writableDatabase
        val success=db.delete(TABLE_HAPPY_PLACE, KEY_ID+"="+happyPlace.id,null)
        db.close()
        return success
    }

    //retrieve database data

    @SuppressLint("Range")
    fun getHappyPlacesList():ArrayList<HappyPlaceModel>{
        val happyPlaceList=ArrayList<HappyPlaceModel>()
        val selectQuery="SELECT * FROM $TABLE_HAPPY_PLACE"
        val db=this.readableDatabase

        try {
            //create cursor
            val cursor:Cursor=db.rawQuery(selectQuery,null)

            if(cursor.moveToFirst()){
                do {

                    val place=HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))

                        )
                    happyPlaceList.add(place)

                }while (cursor.moveToNext())
            }
            cursor.close()

        }catch (e:SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return happyPlaceList
    }

}