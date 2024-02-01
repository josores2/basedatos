package com.josesorli.misamigos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MyDatabase"
        private const val TABLE_NAME = "Contacts"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_NAME($KEY_ID INTEGER PRIMARY KEY,$KEY_NAME TEXT,$KEY_EMAIL TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addContact(name: String, email: String): Long {
        val db = this.writableDatabase
        //Las siguientes lineas crean una lista Key,Valor con name, email
        val values = ContentValues()
        values.put(KEY_NAME, name)
        values.put(KEY_EMAIL, email)
        val success = db.insert(TABLE_NAME, null, values)
        db.close()
        return success
    }

    fun getAllContacts(): List<Contact>{
        val contactList = mutableListOf<Contact>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)

        cursor.use {
            if(it.moveToFirst()){
                do{
                    val id=it.getInt(it.getColumnIndex(KEY_ID))
                    val name = it.getString(it.getColumnIndex(KEY_NAME))
                    val email = it.getString(it.getColumnIndex(KEY_EMAIL))
                    val contact = Contact(id,name,email)
                    contactList.add(contact)
                }while(it.moveToNext())

            }
        }
       return contactList
    }
}
