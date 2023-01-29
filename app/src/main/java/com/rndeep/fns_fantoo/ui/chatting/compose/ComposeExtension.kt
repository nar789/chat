package com.rndeep.fns_fantoo.ui.chatting.compose

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.rndeep.fns_fantoo.R
import java.lang.Math.abs

@Composable
fun String?.getImageUrlFromCDN(): String? = when (this) {
    null -> null
    else -> stringResource(R.string.imageUrlBase, this)
}

@Composable
fun String?.getChatImageUrl(): String? = when (this) {
    null -> null
    else -> stringResource(id = R.string.chatting_image_prefix, this)
}

@Composable
fun maxScrollFlingBehavior(): FlingBehavior {
    val flingSpec = rememberSplineBasedDecay<Float>()
    return remember(flingSpec) {
        ScrollSpeedFlingBehavior(flingSpec)
    }
}

private class ScrollSpeedFlingBehavior(
    private val flingDecay: DecayAnimationSpec<Float>
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        // Prevent very fast scroll
        val newVelocity =
            if (initialVelocity > 0F) minOf(initialVelocity, 15_000F)
            else maxOf(initialVelocity, -15_000F)

        return if (abs(newVelocity) > 1f) {
            var velocityLeft = newVelocity
            var lastValue = 0f
            AnimationState(
                initialValue = 0f,
                initialVelocity = newVelocity,
            ).animateDecay(flingDecay) {
                val delta = value - lastValue
                val consumed = scrollBy(delta)
                lastValue = value
                velocityLeft = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
            velocityLeft
        } else newVelocity
    }
}