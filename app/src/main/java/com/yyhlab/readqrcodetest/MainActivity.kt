package com.yyhlab.readqrcodetest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.yyhlab.readqrcodetest.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePickResultLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { result ->
            result?.let { uri ->
                contentResolver.openInputStream(uri)?.let { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    decodeQrcodeImage(bitmap)?.let {
                        binding.textView.text = it
                    } ?: let { binding.textView.text = "Read fail" }
                }
            }
        }

        binding.button.setOnClickListener {
            imagePickResultLauncher.launch("image/*")
        }

    }

    private fun decodeQrcodeImage(bitmap: Bitmap): String? {
        try {
            val width = bitmap.width
            val height = bitmap.height
            val pixelSize = width * height
            var pixels = IntArray(pixelSize)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            bitmap.recycle()
            val hints = Hashtable<DecodeHintType, Any>()
            hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
            hints[DecodeHintType.PURE_BARCODE] = true
            val source = RGBLuminanceSource(width, height, pixels)
            val binarizer = HybridBinarizer(source)
            val binaryBitmap = BinaryBitmap(binarizer)
            val reader = QRCodeReader()
            val result = reader.decode(binaryBitmap, hints)
            return result.text
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}