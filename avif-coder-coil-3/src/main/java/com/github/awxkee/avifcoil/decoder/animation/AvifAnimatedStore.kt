package com.github.awxkee.avifcoil.decoder.animation

import android.graphics.Bitmap
import com.radzivon.bartoshyk.avif.coder.AvifAnimatedDecoder
import com.radzivon.bartoshyk.avif.coder.PreferredColorConfig
import com.radzivon.bartoshyk.avif.coder.ScaleMode

@Suppress("unused", "UNUSED_PARAMETER")
class AvifAnimatedStore(
    private val avifAnimatedDecoder: AvifAnimatedDecoder,
    scaleMode: ScaleMode,
    private val preferredColorConfig: PreferredColorConfig,
    targetWidth: Int = 0,
    targetHeight: Int = 0,
) : AnimatedFrameStore {

    // Cache original dimensions - these are returned to let the display layer handle scaling
    // Native scaling is avoided due to crash bug in RescaleSourceImage (SIGSEGV in weave_scale_u8)
    private val cachedOriginalWidth: Int = avifAnimatedDecoder.getImageSize().width
    private val cachedOriginalHeight: Int = avifAnimatedDecoder.getImageSize().height

    // Return original dimensions - the animation renderer should handle scaling at display time
    override val width: Int
        get() = cachedOriginalWidth
    override val height: Int
        get() = cachedOriginalHeight

    override fun getFrame(frame: Int): Bitmap {
        // IMPORTANT: Use non-scaling getFrame() to avoid native crash in RescaleSourceImage.
        // The native getScaledFrame() -> RescaleSourceImage() -> weave_scale_u8()
        // has a memory access bug (SIGSEGV SEGV_ACCERR) during image scaling.
        // The animation playback layer should handle scaling at display time instead.
        return avifAnimatedDecoder.getFrame(
            frame = frame,
            preferredColorConfig = preferredColorConfig
        )
    }

    override fun getFrameDuration(frame: Int): Int {
        return avifAnimatedDecoder.getFrameDuration(frame)
    }

    private var storedFramesCount: Int = -1

    override val framesCount: Int
        get() = if (storedFramesCount == -1) {
            storedFramesCount = avifAnimatedDecoder.getFramesCount()
            storedFramesCount
        } else {
            storedFramesCount
        }
}
