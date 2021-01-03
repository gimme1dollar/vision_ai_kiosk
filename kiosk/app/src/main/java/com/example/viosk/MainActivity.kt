package com.example.viosk

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.lang.Math.min
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    companion object {
        private const val MAX_PREVIEW_WIDTH = 1920
        private const val MAX_PREVIEW_HEIGHT = 1080
        private const val REQUEST_CAMERA_PERMISSION = 1
        private const val BLUETOOTH_UUID = "00001101-0000-1000-8000-00805F9B34FB"
        private const val BLUETOOTH_DEVICE_ADDRESS = "98:D3:32:70:4E:64"
    }

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothSocket: BluetoothSocket? = null

    private var mCameraId: String? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mCameraDevice: CameraDevice? = null
    private var mPreviewSize: Size? = null
    private var mImageReader: ImageReader? = null

    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    private var mPreviewRequest: CaptureRequest? = null
    private val mCameraLock: Semaphore = Semaphore(1)

    private lateinit var mTextureView: TextureView
    private lateinit var mNoticeTextView: TextView
    private lateinit var mProgressBar: ProgressBar

    private var mBluetoothReceiver: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTextureView = findViewById(R.id.camera_view)
        mNoticeTextView = findViewById(R.id.tv_notice)
        mProgressBar = findViewById(R.id.pb_detecting)

        connectToBluetoothDevice()
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        onCustomerPending()
    }

    override fun onPause() {
        onCustomerAnalyzing()
        stopBackgroundThread()
        stopBluetoothReceivingThread()
        super.onPause()
    }

    override fun onDestroy() {
        mBluetoothSocket?.close()
        super.onDestroy()
    }

    private fun onCustomerPending() {
        mNoticeTextView.text = "화면에 얼굴을 맞춰주세요. 이후에 주문이 진행됩니다."
        mProgressBar.visibility = View.INVISIBLE

        if (mTextureView.isAvailable) {
            openCamera(mTextureView.width, mTextureView.height)
        } else {
            mTextureView.surfaceTextureListener = mSurfaceTextureListener
        }

        stopBluetoothReceivingThread()
        mBluetoothReceiver = thread(start=true) {
            try {
                val ageClass = readBluetoothInput().substring(0, 1).toInt()
                runOnUiThread { onCustomerAnalyzing() }
                readBluetoothInput()
                runOnUiThread {
                    val intent = Intent(this, MenuActivity::class.java)
                        .putExtra("age", ageClass)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e("thread", e.toString())
            }
        }
    }

    private fun onCustomerAnalyzing() {
        closeCamera()
        mNoticeTextView.text = "나이를 확인하는 중입니다. 잠시만 기다려주세요."
        mProgressBar.visibility = View.VISIBLE
    }

    private fun stopBluetoothReceivingThread() {
        if (mBluetoothReceiver != null && mBluetoothReceiver!!.isAlive) {
            mBluetoothReceiver!!.interrupt()
        }
    }

    private fun connectToBluetoothDevice() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            showToast("Bluetooth device not available")
            finish()
        } else if (!mBluetoothAdapter!!.isEnabled) {
            val turnBluetoothOn = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(turnBluetoothOn, REQUEST_CAMERA_PERMISSION)
        }

        val dispositive: BluetoothDevice =
            mBluetoothAdapter!!.getRemoteDevice(BLUETOOTH_DEVICE_ADDRESS)
        mBluetoothSocket =
            dispositive.createInsecureRfcommSocketToServiceRecord(UUID.fromString(BLUETOOTH_UUID))
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
        mBluetoothSocket!!.connect()
    }

    private fun readBluetoothInput(): String {
        val buffer = ByteArray(1024)
        val inputStream = mBluetoothSocket!!.inputStream
        var totalString = ""

        while (!totalString.endsWith("\r")) {
            val bytes = inputStream.read(buffer)
            if (Thread.interrupted()) {
                Log.d("btconnect", "interrupt!")
                throw Exception("thread stopping due to interrupt.")
            }

            if (bytes > 0) {
                val str = String(buffer, 0, bytes)
                totalString += str
            }
        }

        Log.d("btconnect", "string received: $totalString")
        return totalString
    }

    private fun openCamera(width: Int, height: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
            return
        }
        setCameraOutputs(width, height)
        configureTransform(width, height)
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!mCameraLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(mCameraId!!, mStateCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }
    }

    private fun closeCamera() {
        try {
            mCameraLock.acquire()
            mCaptureSession?.close()
            mCaptureSession = null

            mCameraDevice?.close()
            mCameraDevice = null

            mImageReader?.close()
            mImageReader = null
        } catch (e: InterruptedException) {
            throw java.lang.RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            mCameraLock.release()
        }
    }

    private fun setCameraOutputs(width: Int, height: Int) {
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    ?: continue
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing == null || facing != CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                val largest: Size? = map.getOutputSizes(ImageFormat.JPEG).maxWithOrNull(
                    CompareSizesByArea()
                )
                mImageReader = ImageReader.newInstance(
                    largest!!.width, largest.height,
                    ImageFormat.JPEG,  /*maxImages*/2
                )
                mImageReader!!.setOnImageAvailableListener(
                    null, mBackgroundHandler
                )

                val displaySize = Point()
                windowManager.defaultDisplay.getSize(displaySize)
                val maxPreviewWidth = displaySize.x.coerceAtMost(MAX_PREVIEW_WIDTH)
                val maxPreviewHeight = displaySize.y.coerceAtMost(MAX_PREVIEW_HEIGHT)

                mPreviewSize = chooseOptimalSize(
                    map.getOutputSizes(SurfaceTexture::class.java),
                    width, height, maxPreviewWidth,
                    maxPreviewHeight, largest
                )
                mCameraId = cameraId
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            showToast("Camera2 API not supported on this device")
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val texture = mTextureView.surfaceTexture!!
            texture.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)

            val surface = Surface(texture)
            mPreviewRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder!!.addTarget(surface)

            mCameraDevice!!.createCaptureSession(
                listOf(surface, mImageReader!!.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        // Camera already closed.
                        if (mCameraDevice == null) {
                            return
                        }

                        // When the session is ready, we start displaying the preview.
                        mCaptureSession = cameraCaptureSession
                        try {
                            // Auto focus should be continuous for camera preview.
                            mPreviewRequestBuilder!!.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )

                            // Finally, we start displaying the camera preview.
                            mPreviewRequest = mPreviewRequestBuilder!!.build()
                            mCaptureSession!!.setRepeatingRequest(
                                mPreviewRequest!!,
                                null, mBackgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        showToast("Failed")
                    }
                }, null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            AlertDialog.Builder(this@MainActivity)
                .setMessage("R string request permission")
                .setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { _, _ ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity, arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    })
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { _, _ -> finish() })
                .create()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@MainActivity,
                    "ERROR: Camera permissions not granted",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        mBackgroundThread!!.quitSafely()
        try {
            mBackgroundThread!!.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun chooseOptimalSize(
        choices: Array<Size>, textureViewWidth: Int,
        textureViewHeight: Int, maxWidth: Int, maxHeight: Int, aspectRatio: Size
    ): Size? {
        val w = aspectRatio.width
        val h = aspectRatio.height

        val validOptions = choices.filter { option ->
            option.width <= maxWidth
                    && option.height <= maxHeight
                    && option.height == option.width * h / w
        }
        val (optionsBigEnough, optionsNotBigEnough) = validOptions.partition { option ->
            option.width >= textureViewWidth
                    && option.height >= textureViewHeight
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        return when {
            optionsBigEnough.isNotEmpty() -> {
                Collections.min(optionsBigEnough, CompareSizesByArea())
            }
            optionsNotBigEnough.isNotEmpty() -> {
                Collections.max(optionsNotBigEnough, CompareSizesByArea())
            }
            else -> {
                Log.e("Camera2", "Couldn't find any suitable preview size")
                choices[0]
            }
        }
    }

    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        if (mPreviewSize == null) {
            return
        }
        val rotation = windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0F, 0F, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(
            0F,
            0F,
            mPreviewSize!!.height.toFloat(),
            mPreviewSize!!.width.toFloat()
        )
        val centerX: Float = viewRect.centerX()
        val centerY: Float = viewRect.centerY()
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale =
                (viewHeight.toFloat() / mPreviewSize!!.height)
                    .coerceAtLeast(viewWidth.toFloat() / mPreviewSize!!.width)
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
        } else if (rotation == Surface.ROTATION_180) {
            matrix.postRotate(180F, centerX, centerY)
        }
        mTextureView.setTransform(matrix)
    }

    internal class CompareSizesByArea : Comparator<Size?> {
        override fun compare(lhs: Size?, rhs: Size?): Int {
            // We cast here to ensure the multiplications won't overflow
            return java.lang.Long.signum(
                lhs!!.width.toLong() * lhs.height -
                        rhs!!.width.toLong() * rhs.height
            )
        }
    }

    private fun showToast(text: String) {
        runOnUiThread { Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show() }
    }

    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraLock.release()
            mCameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            mCameraLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            mCameraLock.release()
            cameraDevice.close()
            mCameraDevice = null
            finish()
        }
    }

    private val mSurfaceTextureListener: SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
    }
}