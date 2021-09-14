package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest{

    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createRepository(){

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        remindersLocalRepository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Unconfined
        )
    }

    @Test
    fun saveReminders_toDatabase()= runBlockingTest{

        val reminder1 = ReminderDTO(
            description = "Test1 description",
            title = "Test1 title",
            latitude = 21.0,
            location = "Test1 location",
            longitude = 31.0
        )

        remindersLocalRepository.saveReminder(reminder1)

        val result : ReminderDTO = remindersLocalRepository.getReminder(reminder1.id) as ReminderDTO

        ViewMatchers.assertThat(result, Is.`is`(reminder1))
    }

    @Test
    fun getReminderErrorMessage_ReminderNotFound()= runBlockingTest{

        val reminder1 = ReminderDTO(
            description = "Test1 description",
            title = "Test1 title",
            latitude = 21.0,
            location = "Test1 location",
            longitude = 31.0
        )

        val result : Result.Error = remindersLocalRepository.getReminder(reminder1.id) as Result.Error

        ViewMatchers.assertThat(result.message, Is.`is`("Reminder not found!"))
    }
}