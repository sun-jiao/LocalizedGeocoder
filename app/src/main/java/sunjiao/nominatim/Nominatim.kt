package sunjiao.nominatim

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import androidx.annotation.NonNull
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

/**
 *
 * @author sun-jiao (孙娇）
 *
 */

class Nominatim//according to Nominatim ToS, user agent is necessary
    (
    @NonNull private var latitude: Float,
    @NonNull private var longitude: Float,
    @NonNull private val language: String,
    @NonNull private val useragent: String)
{
    private val TAG =  "Nominatim"
    private fun getJSON() : JSONObject? {
        val client : OkHttpClient = OkHttpClient()
        val builder = "http://nominatim.openstreetmap.org/reverse?".toHttpUrlOrNull()?.newBuilder()
        builder?.addQueryParameter("format", "json")
        builder?.addQueryParameter("lat", latitude.toString())
        builder?.addQueryParameter("lon", longitude.toString())
        builder?.addQueryParameter("zoom", "18")
        builder?.addQueryParameter("addressdetails", "1")
        builder?.addQueryParameter("accept-language", language)
        val request = Request.Builder()
            .url(builder.toString())
            .removeHeader("User-Agent").addHeader("User-Agent", useragent)
            .build()
        var addressObject : JSONObject = JSONObject()
        val response : Response = client.newCall(request).execute()
        if(response.body != null) {
            val jsonObject : JSONObject = JSONObject(response.body!!.string())
            if (jsonObject.isNull("error")){
                addressObject = JSONObject(jsonObject.getString("address"))
                addressObject.put("display_name",jsonObject.getString("display_name"))
                return addressObject
            } else
                return null
        } else
            return null

    }

    fun getAddress() : Address? {
        val json : JSONObject? = getJSON()
        if (json != null){
            return Address(
            json.getString("building"),
            json.getString("house_number"),
            json.getString("road"),
            json.getString("address29"),
            json.getString("allotments"),
            json.getString("isolated_dwelling"),
            json.getString("hamlet"),
            json.getString("locality"),
            json.getString("farm"),
            json.getString("school"),
            json.getString("neighbourhood"),
            json.getString("suburb"),
            json.getString("city_district"),
            json.getString("town"),
            json.getString("city"),
            json.getString("county"),
            json.getString("region"),
            json.getString("state_district"),
            json.getString("state"),
            json.getString("country"),
            json.getString("country_code"),
                latitude, longitude,
            json.getString("display_name"))
        } else
            return null
    }
}