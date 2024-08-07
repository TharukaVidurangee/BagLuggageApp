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
                Log.e("BarcodeScreen", "API call failed: ${t.message}", t)
                Toast.makeText(this@BarcodeScreen, "Failed to fetch room labels", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAutoCompleteTextView(labels: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, labels)
        val roomEditText = findViewById<AutoCompleteTextView>(R.id.roomEditText)
        roomEditText.setAdapter(adapter)
        roomEditText.threshold = 1 // Start showing suggestions after 1 character
    }

    // Get the current date and time as a formatted string
    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // Trust all certificates (Unsafe)
    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
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
}
