package uz.dkamaloff

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Created at February 2020
 *
 * @project CurrencyConverter
 * @author Dostonbek Kamalov (aka @ddk9499)
 */

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAssetManager(@ApplicationContext context: Context): AssetManager = context.assets

    @Singleton
    @Provides
    fun provideGson(): Gson = Gson()

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient = OkHttpClient()

}