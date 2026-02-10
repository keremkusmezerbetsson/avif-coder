/*
 * MIT License
 *
 * Copyright (c) 2023 Radzivon Bartoshyk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.radzivon.bartoshyk.avif

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.ImageLoader
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.github.awxkee.avifcoil.decoder.HeifDecoder
import com.radzivon.bartoshyk.avif.databinding.ActivityCoilGalleryBinding
import com.radzivon.bartoshyk.avif.databinding.ItemImageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CoilGalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoilGalleryBinding
    private lateinit var imageLoader: ImageLoader
    private lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCoilGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create ImageLoader with HeifDecoder for AVIF/HEIC support
        imageLoader = ImageLoader.Builder(this)
            .components {
                add(HeifDecoder.Factory())
            }
            .crossfade(true)
            .build()

        // Setup RecyclerView
        adapter = ImageAdapter(imageLoader)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CoilGalleryActivity)
            adapter = this@CoilGalleryActivity.adapter
            setHasFixedSize(true)
        }

        // Load images from assets
        loadImagesFromAssets()
    }

    private fun loadImagesFromAssets() {
        lifecycleScope.launch(Dispatchers.IO) {
            val allFiles = mutableListOf<String>()

            // Get all AVIF/HEIC/HEIF files from assets
            val files1 = getAllFilesFromAssets("").filter { isImageFile(it) }
            val files2 = getAllFilesFromAssets("hdr").filter { isImageFile(it) }

            allFiles.addAll(files2)
            allFiles.addAll(files1)

            Log.d(TAG, "Found ${allFiles.size} image files")

            withContext(Dispatchers.Main) {
                adapter.submitList(allFiles)
            }
        }
    }

    private fun isImageFile(filename: String): Boolean {
        return filename.endsWith(".avif", ignoreCase = true) ||
                filename.endsWith(".heic", ignoreCase = true) ||
                filename.endsWith(".heif", ignoreCase = true)
    }

    private fun getAllFilesFromAssets(path: String): List<String> {
        val fileList = mutableListOf<String>()
        try {
            val files = assets.list(path) ?: arrayOf()
            for (file in files) {
                val fullPath = if (path.isEmpty()) file else "$path/$file"
                fileList.add(fullPath)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading assets: ${e.message}", e)
        }
        return fileList
    }

    override fun onDestroy() {
        super.onDestroy()
        imageLoader.shutdown()
    }

    companion object {
        private const val TAG = "CoilGalleryActivity"
    }

    /**
     * RecyclerView Adapter for displaying images with Coil
     */
    inner class ImageAdapter(
        private val imageLoader: ImageLoader
    ) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

        private var images: List<String> = emptyList()

        fun submitList(newImages: List<String>) {
            images = newImages
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val binding = ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ImageViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.bind(images[position])
        }

        override fun getItemCount(): Int = images.size

        inner class ImageViewHolder(
            private val binding: ItemImageBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(imagePath: String) {
                binding.textFileName.text = imagePath

                // Load image using Coil with HeifDecoder
                // Using file:///android_asset/ URI to load from assets
                binding.imageView.load("file:///android_asset/$imagePath", imageLoader) {
                    crossfade(true)
                    // Use explicit pixel size for sampling to prevent OOM and crashes
                    size(800, 800)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                    listener(
                        onStart = { Log.d(TAG, "Loading: $imagePath") },
                        onSuccess = { _, _ -> Log.d(TAG, "Loaded: $imagePath") },
                        onError = { _, result ->
                            Log.e(TAG, "Error loading $imagePath: ${result.throwable.message}")
                        }
                    )
                }
            }
        }
    }
}
