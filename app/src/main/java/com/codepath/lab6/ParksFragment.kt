package com.codepath.lab6

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException


private const val TAG = "ParksFragment"
private val API_KEY = BuildConfig.API_KEY

private val PARKS_URL = "https://developer.nps.gov/api/v1/parks?api_key=${API_KEY}"

class ParksFragment : Fragment() {

    private val parks = mutableListOf<Park>()
    private lateinit var parksRecyclerView: RecyclerView
    private lateinit var parksAdapter: ParksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_parks, container, false)

        parksRecyclerView = view.findViewById(R.id.parks)

        parksAdapter = ParksAdapter(view.context, parks)
        parksRecyclerView.adapter = parksAdapter
        parksRecyclerView.layoutManager = LinearLayoutManager(context)

        fetchParks()

        return view
    }

    private fun fetchParks() {
        val client = AsyncHttpClient()

        client.get(PARKS_URL, object : JsonHttpResponseHandler() {

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch parks")
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "Fetched parks")

                try {
                    val parsedJson = createJson().decodeFromString(
                        ParksResponse.serializer(),
                        json.jsonObject.toString()
                    )

                    parsedJson.data?.let {
                        parks.addAll(it)
                        parksAdapter.notifyDataSetChanged()
                    }

                } catch (e: JSONException) {
                    Log.e(TAG, "Exception $e")
                }
            }
        })
    }
}