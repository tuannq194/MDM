package com.ngxqt.mdm.di

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.remote.ApiInterface
import com.ngxqt.mdm.util.BASE_URL
import com.ngxqt.mdm.util.BiometricHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_data_store")
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        String
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request =
                    chain.request()
                        .newBuilder()
                        .header("Content-Type", "application/json")
                        .build()
                chain.proceed(request)
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesProvider(@ApplicationContext context: Context): UserPreferencesProvider {
        val userPreferences = UserPreferences(context)
        return UserPreferencesProviderImpl(userPreferences)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        userPreferencesProvider: UserPreferencesProvider
    ): Retrofit {
        val baseUrlFlow = userPreferencesProvider.getBaseUrl()
        var baseUrl = runBlocking { baseUrlFlow.first() }
        if (baseUrl == null) baseUrl = "http://bvdemo.qltbyt.com"
        Log.d("provideRetrofit", "RUN HERE")
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            //.addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}