package com.example

import android.content.Context
import com.example.data.database.AppDatabase
import com.example.data.repository.FocusRepository
import com.example.data.repository.UsageStatsRepository
import com.example.data.api.NvidiaNimApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

interface AppContainer {
    val focusRepository: FocusRepository
    val usageStatsRepository: UsageStatsRepository
    val nvidiaNimApi: NvidiaNimApi
    val scheduleRepository: com.example.data.repository.ScheduleRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database by lazy { AppDatabase.getDatabase(context) }
    override val focusRepository: FocusRepository by lazy {
        FocusRepository(database.focusSessionDao())
    }
    override val usageStatsRepository: UsageStatsRepository by lazy {
        UsageStatsRepository(context)
    }

    override val scheduleRepository: com.example.data.repository.ScheduleRepository by lazy {
        com.example.data.repository.ScheduleRepository(database.scheduleDao())
    }

    override val nvidiaNimApi: NvidiaNimApi by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            
        Retrofit.Builder()
            .baseUrl("https://integrate.api.nvidia.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NvidiaNimApi::class.java)
    }
}
