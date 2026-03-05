package edu.cnm.deepdive.codebreaker.app.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ActivityContext
import edu.cnm.deepdive.codebreaker.app.R
import jakarta.inject.Inject
import java.util.Collections

class SymbolMap @Inject constructor(
  @param:ActivityContext private val context: Context
) {

  private val symbols: Map<Int, SymbolAttributes>

  init {
    val names = context.resources.getStringArray(R.array.color_names)
    val keys = context.resources.getStringArray(R.array.color_keys)
    val values = getColors(R.array.color_values)
    val drawables = getDrawables(R.array.color_drawables)

    symbols = keys.indices.associate { i ->
      keys[i].codePointAt(0) to SymbolAttributes(values[i], names[i], drawables[i])
    }
  }

  /**
   * Returns an unmodifiable list of the symbol key codepoints.
   */
  fun getKeys(): List<Int> = Collections.unmodifiableList(symbols.keys.toList())

  /**
   * Returns the color value associated with the specified key codepoint.
   * @throws NoSuchElementException if the key is not found.
   */
  fun getColor(key: Int): Int = symbols.getValue(key).value

  /**
   * Returns the name associated with the specified key codepoint.
   * @throws NoSuchElementException if the key is not found.
   */
  fun getName(key: Int): String = symbols.getValue(key).name

  /**
   * Returns the drawable associated with the specified key codepoint.
   * @throws NoSuchElementException if the key is not found.
   */
  fun getDrawable(key: Int): Drawable = symbols.getValue(key).drawable

  private fun getColors(arrayResId: Int): List<Int> {
    val typedArray = context.resources.obtainTypedArray(arrayResId)
    return try {
      List(typedArray.length()) { i -> typedArray.getColor(i, Color.TRANSPARENT) }
    } finally {
      typedArray.recycle()
    }
  }

  private fun getDrawables(arrayResId: Int): List<Drawable> {
    val ids = context.resources.getIntArray(arrayResId)
    return ids.map { id ->
      ContextCompat.getDrawable(context, id)
        ?: throw IllegalArgumentException("Drawable resource not found")
    }
  }

  private data class SymbolAttributes(
    val value: Int,
    val name: String,
    val drawable: Drawable
  )

}
