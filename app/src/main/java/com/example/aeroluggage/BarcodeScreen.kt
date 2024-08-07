package com.example.aeroluggage

import RoomDataItem
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aeroluggage.databinding.ActivityBarcodeScreenBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*
import java.text.SimpleDateFormat
import java.util.*

class BarcodeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeScreenBinding
    private lateinit var db: TagDatabaseHelper
    private lateinit var tagAdapter: TagAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Retrofit and ApiService with custom OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ulmobservicestest.srilankan.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getUnsafeOkHttpClient()) // Use custom OkHttpClient
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Initialize database and adapter
        db = TagDatabaseHelper(this)
        tagAdapter = TagAdapter(db.getAllTags(), this)

        // Set up RecyclerView
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.tagRecyclerView.adapter = tagAdapter

        // Handle save button click
        binding.saveButton.setOnClickListener {
            val bagtag = binding.tagEditText.text.toString()
            val room = binding.roomEditText.text.toString()
            if (room.isNotEmpty() && bagtag.isNotEmpty()) {
                val dateTime = getCurrentDateTime()
                val tag = Tag(0, bagtag, room, dateTime)
                db.insertTag(tag)
                tagAdapter.refreshData(db.getAllTags())
                binding.tagEditText.text.clear()
                binding.roomEditText.text.clear()
                Toast.makeText(this, "Bag Tag saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch room data
        fetchRoomData()
    }

    private fun fetchRoomData() {
        apiService.getStorageRoomList().enqueue(object : Callback<List<RoomDataItem>> {
            override fun onResponse(
                call: Call<List<RoomDataItem>>,
                response: Response<List<RoomDataItem>>
            ) {
                if (response.isSuccessful) {
                    val roomData = response.body()
                    if (roomData != null) {
                        val roomLabels = roomData.map { it.CheckLabel }
                        setupAutoCompleteTextView(roomLabels)
                    } else {
                        Log.e("BarcodeScreen", "Response body is null")
                        Toast.makeText(this@BarcodeScreen, "No room labels found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("BarcodeScreen", "Response error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@BarcodeScreen, "Error fetching room labels", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RoomDataItem>>, t: Throwable) {
                Log.e("BarcodeScreen", "Fetch failed: ${t.message}", t)
                Toast.makeText(this@BarcodeScreen, "Failed to fetch room labels", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAutoCompleteTextView(roomLabels: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roomLabels)
        (binding.roomEditText as AutoCompleteTextView).setAdapter(adapter)
    }

    fun syncTag(tag: Tag) {
        val syncData = SyncData(
            AddedDate = getCurrentDateTime(), // Adjust according to your data needs
            AddedTime = System.currentTimeMillis().toInt(), // Example conversion
            AddedUser = "User", // Replace with actual user or context
            BagTag = tag.bagtag,
            CheckId = "", // Provide actual data if available
            CheckLabel = "", // Provide actual data if available
            EndDate = "", // Provide actual data if available
            LastUpdatedDate = getCurrentDateTime(), // Example date
            LastUpdatedUser = "User", // Replace with actual user or context
            ReturnCode = "", // Provide actual data if available
            StorageRoom = tag.room,
            SyncDate = getCurrentDateTime(), // Current date-time
            TransId = tag.id,
            ValidPeriod = "" // Provide actual data if available
        )

        apiService.syncData(syncData).enqueue(object : Callback<SyncData> {
            override fun onResponse(call: Call<SyncData>, response: Response<SyncData>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@BarcodeScreen, "Tag synced successfully", Toast.LENGTH_SHORT).show()
                    db.deleteTag(tag.id)
                    tagAdapter.refreshData(db.getAllTags())
                } else {
                    Log.e("BarcodeScreen", "Sync error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@BarcodeScreen, "Error syncing tag", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SyncData>, t: Throwable) {
                Log.e("BarcodeScreen", "Sync failed: ${t.message}", t)
                Toast.makeText(this@BarcodeScreen, "Failed to sync tag", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate>? {
                    return null
                }
            })
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        builder.hostnameVerifier { _, _ -> true }
        return builder.build()
    }
}
