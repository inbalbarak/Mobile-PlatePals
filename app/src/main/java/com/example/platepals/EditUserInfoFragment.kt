package com.example.platepals

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.platepals.model.Model
import com.example.platepals.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.squareup.picasso.Picasso

class EditUserInfoFragment : Fragment() {
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_user_info, container, false)


        val username = arguments?.getString(USERNAME)
        val avatarUrl = arguments?.getString(AVATAR_URL)


        val updatedUsername = view.findViewById<TextView>(R.id.updateUsernameText)
        val updatedProfileImage = view.findViewById<ImageView>(R.id.updatedProfileImage)
        val takePhotoButton = view.findViewById<ImageButton>(R.id.takePhotoButton)


        avatarUrl?.let {
            if (it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.empty_user_icon)
                    .into(updatedProfileImage)
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            updatedProfileImage?.setImageBitmap(bitmap)
            didSetProfileImage = true
        }

        takePhotoButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }

        updatedUsername.text = username

        val auth = Firebase.auth

        val email = auth.currentUser?.email ?: ""

        val updateButton: Button = view.findViewById(R.id.updateProfileBtn)
        updateButton.setOnClickListener {

            Model.shared.getUserByEmail(email) { user ->

                if (user != null) {
                    var image: Bitmap? = null;

                    if (didSetProfileImage) {
                        updatedProfileImage?.isDrawingCacheEnabled = true
                        updatedProfileImage?.buildDrawingCache()
                        val bitmap = (updatedProfileImage?.drawable as BitmapDrawable).bitmap
                        image = bitmap
                    }

                    val updatedUser = user.copy(
                        username = updatedUsername.text.toString(),
                    )

                    Model.shared.upsertUser(updatedUser, image = image) { success ->
                        if (success) {
                            val rating =
                                if (user.ratingCount?.toInt() == 0) 0 else (user.ratingSum?.toInt()
                                    ?: 1) / (user.ratingCount?.toInt() ?: 1)
                            val displayFragment = DisplayUserInfoFragment.newInstance(
                                username ?: "",
                                rating,
                                avatarUrl ?: ""
                            )

                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, displayFragment)
                                .addToBackStack(null)
                                .commit()

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to update user info",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        return view
    }

    companion object {
        private const val USERNAME = "username"
        private const val AVATAR_URL = "avatarUrl"

        fun newInstance(username: String, avatarUrl: String): EditUserInfoFragment {
            val fragment = EditUserInfoFragment()
            val args = Bundle()
            args.putString(USERNAME, username)
            args.putString(AVATAR_URL, avatarUrl)
            fragment.arguments = args
            return fragment
        }
    }
}
