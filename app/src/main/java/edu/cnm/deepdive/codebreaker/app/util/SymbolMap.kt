package edu.cnm.deepdive.codebreaker.app.util

import android.content.Context
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject
import javax.inject.Singleton

class ContextProvider @Inject constructor(
    @ActivityContext private val context: Context
) {
    // Add logic here if needed. For now, this meets the requirement.
}
