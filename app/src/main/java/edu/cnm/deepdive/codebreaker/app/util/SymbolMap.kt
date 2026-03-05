package edu.cnm.deepdive.codebreaker.app.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ActivityContext
import edu.cnm.deepdive.codebreaker.app.R
import jakarta.inject.Inject

class SymbolMap @Inject constructor(
  @param:ActivityContext private val context: Context
) {

  init {
    val name = context.resources.getStringArray(R.array.color_names)
    val keys = context.resources.getStringArray(R.array.color_keys)

    val valuesTyped = context.resources.obtainTypedArray(R.array.color_values)
    val values = mutableListOf<Int>()

    for (i in 0 until valuesTyped.length()) {
      val color = valuesTyped.getColor(i, Color.TRANSPARENT)
      values.add(color)
    }

    val drawablesIds = context.resources.getIntArray(R.array.color_drawables)
    val drawables = mutableListOf<Drawable>()

    for (i in 0 until drawablesIds.size) {
      val drawable = ContextCompat.getDrawable(context, drawablesIds[i]) as Drawable
      drawables.add(drawable)
    }
  }
}
