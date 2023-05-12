package com.dorogan.android.lab7
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.searchButton).setOnClickListener {
            val url = findViewById<EditText>(R.id.editText).text.toString()
            CoroutineScope(
                SupervisorJob()
            ).launch {
                try {
                    withContext(Dispatchers.Default) {
                        Retrofit
                            .Builder()
                            .baseUrl("https://api.coinpaprika.com/v1/coins/")
                            .addConverterFactory(
                                GsonConverterFactory.create()
                            )
                            .build()
                            .create(
                                ApiMethods::class.java
                            )
                            .getCurrencyDescription(url)
                            .let { response ->
                                runOnUiThread {
                                    findViewById<TextView>(R.id.nameView).text = response.name
                                    findViewById<TextView>(R.id.symbolView).text = response.symbol
                                    findViewById<TextView>(R.id.descriptionView).text =
                                        response.description
                                }
                            }
                    }
                } catch (notFound: java.lang.Exception) {
                    notFound.printStackTrace()
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, "Неправильний запит", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }
}


interface ApiMethods {
    @GET
    suspend fun getCurrencyDescription(@Url url: String): Currency
}

class Currency(
    val name: String,
    val symbol: String,
    val description: String
)