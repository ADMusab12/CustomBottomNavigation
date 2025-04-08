package com.tenclouds.fluidbottomnavigation

import android.animation.AnimatorSet
import android.view.View
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.tenclouds.fluidbottomnavigation.extension.translationYAnimator
import com.tenclouds.fluidbottomnavigation.view.CircleView
import com.tenclouds.fluidbottomnavigation.view.IconView
import com.tenclouds.fluidbottomnavigation.view.RectangleView
import com.tenclouds.fluidbottomnavigation.view.TitleView
import com.tenclouds.fluidbottomnavigation.view.TopContainerView

internal fun View.animateSelectItemView() {
        val icon = findViewById<IconView>(R.id.icon)
        val title = findViewById<TitleView>(R.id.title)
        val circle = findViewById<CircleView>(R.id.circle)
        val rectangle = findViewById<RectangleView>(R.id.rectangle)
        val topContainer = findViewById<TopContainerView>(R.id.topContainer)

        AnimatorSet()
                .apply {
                        playTogether(
                                circle.selectAnimator,
                                icon.selectAnimator,
                                title.selectAnimator,
                                rectangle.selectAnimator,
                                topContainer.selectAnimator)
                }
                .start()
}

internal fun View.animateDeselectItemView() {
        val icon = findViewById<IconView>(R.id.icon)
        val title = findViewById<TitleView>(R.id.title)
        val circle = findViewById<CircleView>(R.id.circle)
        val rectangle = findViewById<RectangleView>(R.id.rectangle)
        val topContainer = findViewById<TopContainerView>(R.id.topContainer)

        AnimatorSet()
                .apply {
                        playTogether(
                                circle.deselectAnimator,
                                icon.deselectAnimator,
                                title.deselectAnimator,
                                rectangle.deselectAnimator,
                                topContainer.deselectAnimator)
                }
                .start()
}

internal fun View.animateShow() {
        AnimatorSet()
                .apply {
                        play(translationYAnimator(
                                height.toFloat(),
                                0f,
                                3 * KEY_FRAME_IN_MS,
                                LinearOutSlowInInterpolator()))
                }
                .start()
}

internal fun View.animateHide() {
        AnimatorSet()
                .apply {
                        play(translationYAnimator(
                                0f,
                                height.toFloat(),
                                3 * KEY_FRAME_IN_MS,
                                LinearOutSlowInInterpolator()))
                }
                .start()
}
