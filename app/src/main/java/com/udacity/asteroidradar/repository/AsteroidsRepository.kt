package com.udacity.asteroidradar.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AsteroidsRepository(private val databaseAsteroid: AsteroidDatabase) {
    @RequiresApi(Build.VERSION_CODES.O)
    private val startDate = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val endDate = LocalDateTime.now().minusDays(7)

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(databaseAsteroid.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }


    @RequiresApi(Build.VERSION_CODES.O)
    val todaysAsteroids: LiveData<List<Asteroid>> = Transformations.map(
        databaseAsteroid.asteroidDao
            .getTodayAsteroids(startDate.format(DateTimeFormatter.ISO_DATE))
    ) {
        it.asDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val asteroidsByDate: LiveData<List<Asteroid>> = Transformations.map(
        databaseAsteroid.asteroidDao
            .getAsteroidsByDate(
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE)
            )
    ) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val response =
                    Network.asteroidsService.getAsteroids(Constants.API_KEY)
                        .await()
                val asteroidsJson = JSONObject(response)
                val asteroidsList = AsteroidContainer(parseAsteroidsJsonResult(asteroidsJson))
                databaseAsteroid.asteroidDao.insertAll(*asteroidsList.asDatabaseModel())
            } catch (ex: Exception) {
                Log.e("AsteroidsRepository", "refreshAsteroids: Exception ex: $ex")
            }

        }
    }

    suspend fun getPictureOfTheDay(): PictureOfDay? {
        var pictureOfDay: PictureOfDay? = null
        withContext(Dispatchers.IO) {
            pictureOfDay =
                Network.asteroidsService.getPictureOfTheDay(Constants.API_KEY)
                    .await()
        }
        return pictureOfDay
    }

}