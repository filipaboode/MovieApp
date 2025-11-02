package com.example.movieapp

import android.app.Application
import com.example.movieapp.data.db.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CineShareApp : Application () {
    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            DatabaseProvider.get(this@CineShareApp)
        }
    }


}