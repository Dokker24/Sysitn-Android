package ru.den.sysitn.screens

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import ru.den.sysitn.ml.SysitnOriginal4
import java.nio.ByteBuffer
import java.nio.ByteOrder

class HomeViewModel : ViewModel() {

    var imageUri by mutableStateOf<Uri?>(null)
    var normalP by mutableFloatStateOf(0.0f)
    var pneumoniaP by mutableFloatStateOf(0.0f)

    fun classify(context: Context) = viewModelScope.launch {
        try {
            val path = getRealPathFromURI(context, imageUri)
            val model = SysitnOriginal4.newInstance(context)
            val inputTensor = TensorBuffer.createFixedSize(intArrayOf(1, 150, 150, 1), DataType.FLOAT32)
            val imageRead = Imgcodecs.imread(path, Imgcodecs.IMREAD_GRAYSCALE)
            val image = Mat()
            Imgproc.resize(imageRead, image, Size(150.0, 150.0))
            val byteBuffer = ByteBuffer.allocateDirect(4 * 150 * 150 * 1)
            byteBuffer.order(ByteOrder.nativeOrder())
            repeat(150) { i ->
                repeat(150) { j ->
                    byteBuffer.putFloat(image[i, j][0].toFloat() / 255)
                }
            }
            inputTensor.loadBuffer(byteBuffer)
            val outputs = model.process(inputTensor).outputFeature0AsTensorBuffer
            normalP = outputs.floatArray[0]
            pneumoniaP = outputs.floatArray[1]
            model.close()
        } catch (e: Exception) {
            Log.e("Model", "Error ${e.message}")
        }
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri?): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            return cursor!!.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

}