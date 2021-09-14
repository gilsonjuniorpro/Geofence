package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    private lateinit var reminderListViewModel: RemindersListViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel(){
        val fakeDataSource = FakeDataSource()
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun invalidateShowNoData_noDataAvailable(){
        reminderListViewModel.remindersList.value = null
        val value = reminderListViewModel.showNoData.value

        MatcherAssert.assertThat(value, Is.`is`(CoreMatchers.nullValue()))
    }

    @Test
    fun checkShowLoading(){
        mainCoroutineRule.pauseDispatcher()

        reminderListViewModel.loadReminders()

        MatcherAssert.assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), Is.`is`(true))

        mainCoroutineRule.resumeDispatcher()

        MatcherAssert.assertThat(
            reminderListViewModel.showLoading.getOrAwaitValue(),
            Is.`is`(false)
        )
    }
}