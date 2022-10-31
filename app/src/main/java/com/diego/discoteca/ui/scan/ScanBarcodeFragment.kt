package com.diego.discoteca.ui.scan

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diego.discoteca.R
import com.diego.discoteca.databinding.FragmentScanBarcodeBinding
import com.diego.discoteca.util.materialSharedAxisEnterReturnTransition
import com.diego.discoteca.util.materialSharedAxisExitReenterTransition
import com.google.android.material.transition.MaterialSharedAxis
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

typealias BarcodeListener = (barcode: String) -> Unit

class ScanBarcodeFragment : Fragment(R.layout.fragment_scan_barcode), MenuProvider {

    private val args: ScanBarcodeFragmentArgs by navArgs()
    private val mScanBarcodeViewModel: ScanBarcodeViewModel by viewModels {
        ScanBarcodeViewModelFactory(args.destination)
    }

    private lateinit var binding: FragmentScanBarcodeBinding
    private var processingBarcode = AtomicBoolean(false)
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materialSharedAxisEnterReturnTransition(MaterialSharedAxis.X)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScanBarcodeBinding.bind(view)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            scanBarcodeViewModel = mScanBarcodeViewModel
        }

        // Go to DiscResultScanFragment
        mScanBarcodeViewModel.navigateToDiscResultScan.observe(viewLifecycleOwner) {
            it?.let { discResultScan ->
                goToDiscResultScan(
                    ScanBarcodeFragmentDirections
                        .actionScanBarcodeFragmentToDiscResultScanFragment(discResultScan)
                )
                mScanBarcodeViewModel.onNavigateToDiscResultScanDone()
            }
        }

        setupMenu()
        startCamera()
    }

    override fun onResume() {
        super.onResume()
        processingBarcode.set(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    private fun setupMenu() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.STARTED
        )
    }

    /**
     * https://developer.android.com/codelabs/camerax-getting-started#0
     */
    private fun startCamera() {
        // Create an instance of the ProcessCameraProvider
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

            cameraProviderFuture.addListener(Runnable {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(
                            binding.previewViewFragmentScanBarcode.surfaceProvider
                        )
                    }

                // Setup the ImageAnalyzer for the ImageAnalysis use case
                val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                            if (processingBarcode.compareAndSet(false, true)) {
                                searchBarcode(barcode)
                            }
                        })
                    }

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalyzer
                    )
                } catch (exc: Exception) {
                    Timber.e("Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(requireContext()))

        } catch (e: Exception) {
            Timber.e("Error", e)
        }
    }

    private fun searchBarcode(barcode: String) {
        mScanBarcodeViewModel.searchBarcode(barcode)
    }

    private fun goToDiscResultScan(directions: NavDirections) {
        materialSharedAxisExitReenterTransition(MaterialSharedAxis.Z)
        findNavController().navigate(directions)
    }
}