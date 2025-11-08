package com.example.movieapp.data.tmdb

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Singleton object that manages the Volley request queue
 * This ensures that all network requests in the app share the same RequestQueue
 * instance, improving performance and avoiding memory leaks
 * Volley is used as the network library to perform asynchronous HTTP requests
 */
object VolleyClient{
    @Volatile private var queue: RequestQueue? = null

    fun getQueue(ctx: Context): RequestQueue =
        queue ?: synchronized(this) {
            queue ?: Volley.newRequestQueue(ctx.applicationContext).also { queue = it }
        }

    fun <T> add(ctx: Context, req: Request<T>) = getQueue(ctx).add(req)

}