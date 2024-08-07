package com.example.aeroluggage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TagDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bagtag.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allbagtags"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TAG = "bagtag"
        private const val COLUMN_ROOM = "room"
        private const val COLUMN_DATE_TIME = "dateTime"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TAG TEXT, $COLUMN_ROOM TEXT, $COLUMN_DATE_TIME TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    // To add data to the database
    fun insertTag(tag: Tag) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TAG, tag.bagtag)
            put(COLUMN_ROOM, tag.room)
            put(COLUMN_DATE_TIME, tag.dateTime)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // To read data from the database
    fun getAllTags(): List<Tag> {
        val tagsList = mutableListOf<Tag>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        // Using a while loop to retrieve data
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val bagtag = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAG))
            val room = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROOM))
            val dateTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME))

            // When all the data are retrieved, pass them as an argument and store it in a tag variable and add it into the tagsList
            val tag = Tag(id, bagtag, room, dateTime)
            tagsList.add(tag)
        }
        cursor.close()
        db.close()
        return tagsList // This tagsList acts as a list which consists of all the data retrieved from the db
    }

    // Update function
    fun updateTag(tag: Tag) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TAG, tag.bagtag)
            put(COLUMN_ROOM, tag.room)
            put(COLUMN_DATE_TIME, tag.dateTime)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(tag.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getTagByID(tagId: Int): Tag {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $tagId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val bagtag = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAG))
        val room = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROOM))
        val dateTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME))

        cursor.close()
        db.close()
        return Tag(id, bagtag, room, dateTime)
    }

    // Delete function
    fun deleteTag(tagId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(tagId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}
