package com.example.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Url
import java.net.URI
import java.util.logging.Level.parse


class RecipeRec : AppCompatActivity() {
    private val serviceKey = "Cname"
    val items = mutableListOf<recipe>()
    private var videoPlayer: SimpleExoPlayer? = null
    private lateinit var video_player_view: PlayerView
    private var link = "https://youtu.be/K-4cPFuzD0I"
    private lateinit var link2 : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_rec)
        video_player_view = findViewById(R.id.video_player)

        var require_in = findViewById(R.id.require_in) as TextView
        var recipe_name = findViewById(R.id.recipe_name) as TextView

        val home= findViewById<ImageButton>(R.id.home)
        home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val refrigerator = findViewById<ImageButton>(R.id.refrigerator)
        refrigerator.setOnClickListener {
            val intent = Intent(this, RefrigeratorStatus::class.java)
            startActivity(intent)
        }

        var gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://jaeryurp.duckdns.org:40131/")
//            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson)) //있으나마나한 코드...
            .build()
        val api = retrofit.create(imsiapi::class.java)
        val callResult = api.getResult()
        var resultJsonArray: JsonArray?

        callResult.enqueue(object : Callback<JsonArray> {
            override fun onResponse(
                call: Call<JsonArray>,
                response: Response<JsonArray>
            ) {
//                Log.d("FeatTwo", "성공 : ${response.body()}")
                resultJsonArray = response.body()
//                Log.d("FeatTwo", "json : ${resultJsonArray.toString()}")

                val jsonArray = JSONTokener(resultJsonArray.toString()).nextValue() as JSONArray
                val convertArray =
                    MutableList<JSONObject>(jsonArray.length()) { i -> jsonArray.getJSONObject(i) }.map {
                        Gson().fromJson<HashMap<String, String>>(
                            it.toString(),
                            HashMap::class.java
                        )
                    }
                val filterMainIngredient =
                    convertArray.filter { it?.get("mainIngredient") == "tomato" }
//                val mainItems = filterMainIngredient.filter { it?.get("chief") == "A" }
//                val subItems = filterMainIngredient.filter { it?.get("chief") != "A" }
                for (i in 0 until jsonArray.length()) {
                    val name = jsonArray.getJSONObject(i).getString("name")
                    val chief = jsonArray.getJSONObject(i).getString("chief")
                    val step1 = jsonArray.getJSONObject(i).getString("step1")
                    val step2 = jsonArray.getJSONObject(i).getString("step2")
                    val step3 = jsonArray.getJSONObject(i).getString("step3")
                    val step4 = jsonArray.getJSONObject(i).getString("step4")
                    val step5 = jsonArray.getJSONObject(i).getString("step5")
                    val step6 = jsonArray.getJSONObject(i).getString("step6")
                    val step7 = jsonArray.getJSONObject(i).getString("step7")
                    val step8 = jsonArray.getJSONObject(i).getString("step8")
                    val Ingredient = jsonArray.getJSONObject(i).getString("ingredient")
                    val url = jsonArray.getJSONObject(i).getString("link")
                    val match = 0
                    val list = Ingredient.substring(1, Ingredient.length - 1).split(", ").toList()
                    items.add(
                        recipe(
                            name,
                            list,
                            chief,
                            step1,
                            step2,
                            step3,
                            step4,
                            step5,
                            step6,
                            step7,
                            step8,
                            url,
                            match
                        )
                    )
                }
//                val mainItems = items.filter { it.chief == "A" }
//                val subItems = items.filter { it.chief != "A" }
                Log.d("List", "$items")

                for (i in items.indices) {
                    val matchCounter = (items[i].Ingredient).count() - (items[i].Ingredient).minus(
                        listOf<String>(
                            "onion",
                            "rice",
                            "kimchi"
                        )
                    ).count()
                    items[i].matchCount = matchCounter
                }
                items.sortBy { it.matchCount }
                items.reverse()
                link2 = items[0].url.toUri()
                Log.d("link","$link2")
                Log.d("match", "$items")

                require_in.setText(items[0].Ingredient.toString())
                recipe_name.setText(items[0].name)
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.d("FeatTwo", "실패 : $t")
            }
        })

        videoPlayer = SimpleExoPlayer.Builder(this).build()
        video_player_view?.player = videoPlayer
        buildMediaSource()?.let {
            videoPlayer?.prepare(it)
        }

    }

    private fun initYoutubePlayer(key: String) {
        val youtubeView = binding.videoPlayer

        youtubeView.initialize("develop", object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(p0: YouTubePlayer.Provider, p1: YouTubePlayer, p2: Boolean ) {
                if (!p2) {
                    p1.cueVideo(key)
                }
            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult? ) {
            }
        })
    }
    private fun buildMediaSource(): MediaSource? {

        val dataSourceFactory = DefaultDataSourceFactory(this, "recipe")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(link))
    }
    // 일시중지
    override fun onResume() {
        super.onResume()
        videoPlayer?.playWhenReady = true
    }

    // 정지
    override fun onStop() {
        super.onStop()
        videoPlayer?.playWhenReady = false
        if (isFinishing) {
            releasePlayer()
        }
    }

    // 종료
    private fun releasePlayer() {
        videoPlayer?.release()
    }

    fun onDialogClicked2(view: View){
        val check = Check(this)
        check.show()
    }

}