package com.example.aeroluggage

import RoomDataItem
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
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
import javax.net.ssl.*

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
            val room = binding.roomEditText.text.toString()
            val bagtag = binding.tagEditText.text.toString()
            if (room.isNotEmpty() && bagtag.isNotEmpty()) {
                val tag = Tag(0, room, bagtag)
                db.insertTag(tag)
                tagAdapter.refreshData(db.getAllTags())
                binding.roomEditText.text.clear()
                binding.tagEditText.text.clear()
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
                        setupSpinner(roomLabels)
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
                Log.e("BarcodeScreen", "API call failed: ${t.message}", t)
                Toast.makeText(this@BarcodeScreen, "Failed to fetch room labels", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpinner(labels: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.textId.adapter = adapter
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun onResume() {
        super.onResume()
        tagAdapter.refreshData(db.getAllTags())
    }
}
