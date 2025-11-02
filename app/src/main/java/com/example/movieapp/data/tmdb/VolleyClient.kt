package com.example.movieapp.data.tmdb

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object VolleyClient{
    @Volatile private var queue: RequestQueue? = null

    fun getQueue(ctx: Context): RequestQueue =
        queue ?: synchronized(this) {
            queue ?: Volley.newRequestQueue(ctx.applicationContext).also { queue = it }
        }

    fun <T> add(ctx: Context, req: Request<T>) = getQueue(ctx).add(req)

}