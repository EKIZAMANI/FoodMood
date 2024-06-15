package com.capstone.foodmood1.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.capstone.foodmood1.R
import com.capstone.foodmood1.databinding.FragmentProfileBinding
import com.capstone.foodmood1.ui.activity.EditActivity
import com.capstone.foodmood1.ui.activity.LoginActivity
import com.capstone.foodmood1.ui.activity.MyFoodActivity
import com.capstone.foodmood1.ui.model.ProfileViewModel
import com.capstone.foodmood1.ui.model.UserViewModel
import com.capstone.foodmood1.ui.model.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var userModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN).also {
            activity?.window?.decorView?.systemUiVisibility = it
        }
        activity?.window?.statusBarColor = Color.TRANSPARENT
        val root: View = binding.root
        auth = Firebase.auth

        userModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[UserViewModel::class.java]

        with(binding) {
            lifecycleScope.launch {
                Glide.with(requireActivity())
                    .load(Uri.parse(userModel.getPhoto()))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivPhoto)
                tvName.text = userModel.getName()
                tvEmail.text = userModel.getEmail()
            }

            ivPhoto.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), 1
                    )
                } else {
                    showOptions()
                }
            }

            btnEditProfile.setOnClickListener {
                Intent(activity, EditActivity::class.java).also {
                    it.putExtra("extra_name", tvName.text)
                    it.putExtra("extra_email", tvEmail.text)
                    startActivity(it)
                }
            }

            btnMyRecipe.setOnClickListener {
                startActivity(Intent(activity, MyFoodActivity::class.java))
            }

            btnLogout.setOnClickListener {
                val progressDialog = ProgressDialog.show(requireContext(), null, "Harap tunggu")
                progressDialog.show()
                requireActivity().lifecycleScope.launch {
                    val credentialManager = CredentialManager.create(requireContext())
                    auth.signOut()
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                    progressDialog.dismiss()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                showOptions()
            } else {
                // Handle permission denied case
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                binding.ivPhoto.setImageURI(imageUri)
            } else {
                val extras = data?.extras
                val imageBitmap = extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    val path = MediaStore.Images.Media.insertImage(
                        requireContext().contentResolver,
                        imageBitmap,
                        "Title",
                        null
                    )
                    val uri = Uri.parse(path)
                    binding.ivPhoto.setImageURI(uri)
                }
            }
        }
    }

    private fun setCategoryOnClick(view: View) {
        view.setOnClickListener {
            findNavController().navigate(R.id.action_start_to_result)
        }
    }

    private fun showOptions() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val chooser = Intent.createChooser(intent, "Pilih gambar")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent, captureIntent))
        startActivityForResult(chooser, 1)
    }
}