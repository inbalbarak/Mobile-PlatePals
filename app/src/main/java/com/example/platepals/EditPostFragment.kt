package com.example.platepals

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.helper.widget.Flow
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.platepals.model.Model
import com.example.platepals.model.Post
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class EditPostFragment : Fragment() {
    private var postId: String? = null
    private val selectedTagIds = mutableSetOf<String>()
    private val tagViews = mutableListOf<TextView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = arguments?.getString(POST_ID)
        postId?.let {
            getPostDetails(it)
        }

        loadTags()

        val recipeNameText = view.findViewById<EditText>(R.id.recipeNameText)
        val ingredientsText = view.findViewById<EditText>(R.id.ingredientsText)
        val instructionsText = view.findViewById<EditText>(R.id.instructionsText)
        val saveButton = view.findViewById<Button>(R.id.save)

        recipeNameText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState(recipeNameText, ingredientsText, instructionsText, saveButton)
            }
        })

        ingredientsText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState(recipeNameText, ingredientsText, instructionsText, saveButton)
            }
        })

        instructionsText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState(recipeNameText, ingredientsText, instructionsText, saveButton)
            }
        })

        updateSaveButtonState(recipeNameText, ingredientsText, instructionsText, saveButton)

        saveButton.setOnClickListener {
            savePost(recipeNameText, ingredientsText, instructionsText)
        }

        val backButton: Button = view.findViewById(R.id.back)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    companion object {
        private const val POST_ID = "postId"

        fun newInstance(postId: String?): EditPostFragment {
            val fragment = EditPostFragment()
            val args = Bundle()
            args.putString(POST_ID, postId)
            fragment.arguments = args
            return fragment
        }
    }

    private fun savePost(
        recipeNameText: EditText,
        ingredientsText: EditText,
        instructionsText: EditText
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        val recipeName = recipeNameText.text.toString()
        val ingredients = ingredientsText.text.toString()
        val instructions = instructionsText.text.toString()

        if (!(currentUser?.email ?: "").isEmpty()) {
            val newPost = Post(
                id = postId ?: UUID.randomUUID().toString(),
                title = recipeName,
                author = currentUser?.email ?: "",
                ingredients = ingredients,
                instructions = instructions,
                imageUrl = "", // TODO get uploaded photo
                tags = selectedTagIds.toList(),
            )

            Model.shared.addPost(newPost, postId != null) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Post saved successfully", Toast.LENGTH_SHORT).show()

                    // TODO: Navigate to home, if necessary
                } else {
                    Toast.makeText(requireContext(), "Error saving post", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Error saving post, try logging in again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSaveButtonState(
        recipeNameText: EditText,
        ingredientsText: EditText,
        instructionsText: EditText,
        saveButton: Button
    ) {
        // Check if any of the fields are empty
        val isAnyFieldEmpty = recipeNameText.text.isBlank() ||
                ingredientsText.text.isBlank() ||
                instructionsText.text.isBlank()

        // Disable the Save button if any field is empty
        saveButton.isEnabled = !isAnyFieldEmpty
    }

    private fun getPostDetails(postId: String) {
        // Fetch post by id using the model
        Model.shared.getPostById(postId) { post ->
            activity?.runOnUiThread {
                view?.findViewById<EditText>(R.id.recipeNameText)?.setText(post?.title)
                view?.findViewById<EditText>(R.id.ingredientsText)?.setText(post?.ingredients)
                view?.findViewById<EditText>(R.id.instructionsText)?.setText(post?.instructions)

                post?.tags?.let { tags ->
                    selectedTagIds.addAll(tags)
                }

                selectTags()
            }
        }
    }

    private fun loadTags() {
        val flow = view?.findViewById<Flow>(R.id.tagsFlow)
        val mainLayout = view?.findViewById<ConstraintLayout>(R.id.main)

        Model.shared.getAllTags { tags ->
            activity?.runOnUiThread {
                val tagIds = mutableListOf<Int>()

                for (tag in tags) {
                    val tagView = createTagView(tag.name, tag.id)
                    mainLayout?.addView(tagView)
                    tagIds.add(tagView.id)
                    tagViews.add(tagView)
                }

                flow?.referencedIds = tagIds.toIntArray()
                selectTags()
            }
        }
    }

    private fun selectTags() {
        for (tagView in tagViews) {
            val tagId = tagView.tag.toString()

            if (selectedTagIds.contains(tagId)) {
                tagView.setBackgroundResource(R.drawable.orange_filled_rounded_text_field)
                tagView.setTextColor(Color.WHITE)
            } else {
                tagView.setBackgroundResource(R.drawable.orange_rounded_text_field)
                tagView.setTextColor(Color.parseColor("#FF9B05"))
            }
        }
    }

    private fun createTagView(tagName: String, tagId: String): TextView {
        val tagView = LayoutInflater.from(requireContext()).inflate(R.layout.item_tag, null) as TextView
        tagView.id = ViewCompat.generateViewId()
        tagView.text = tagName
        tagView.tag = tagId

        tagView.setOnClickListener {
            if (selectedTagIds.contains(tagId)) {
                selectedTagIds.remove(tagId)
                tagView.setBackgroundResource(R.drawable.orange_rounded_text_field)
                tagView.setTextColor(Color.parseColor("#FF9B05"))
            } else {
                selectedTagIds.add(tagId)
                tagView.setBackgroundResource(R.drawable.orange_filled_rounded_text_field)
                tagView.setTextColor(Color.WHITE)
            }
        }

        return tagView
    }
}
