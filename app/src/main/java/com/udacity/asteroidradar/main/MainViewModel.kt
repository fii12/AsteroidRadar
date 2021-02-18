package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidRepo = AsteroidsRepository(database)
    //show all by default
    private val _asteroidFilter = MutableLiveData<AsteroidsFilter>(AsteroidsFilter.SHOW_ALL)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private var _navigateToDetailPage = MutableLiveData<Asteroid>()
    val navigateToDetailPage: LiveData<Asteroid>
        get() = _navigateToDetailPage


    init {
        viewModelScope.launch {
            asteroidRepo.refreshAsteroids()
            _pictureOfDay.value = asteroidRepo.getPictureOfTheDay()
        }
    }

    val asteroids = asteroidRepo.asteroids

    val asteroidFilter = Transformations.switchMap(_asteroidFilter) {
        when (it!!) {
            AsteroidsFilter.SHOW_TODAY -> asteroidRepo.todaysAsteroids
            AsteroidsFilter.SHOW_WEEK -> asteroidRepo.asteroidsByDate
            else -> asteroids
        }
    }
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    fun choseAsteroidNavigate(asteroid: Asteroid) {
        _navigateToDetailPage.value = asteroid
    }

    fun doneNavigatingToDetailPage() {
        _navigateToDetailPage.value = null
    }

    fun filterAsteroids(asteroidFilter: AsteroidsFilter) {
        _asteroidFilter.postValue(asteroidFilter)
    }

    enum class AsteroidsFilter { SHOW_TODAY, SHOW_WEEK, SHOW_ALL }
}