package com.example.platepals

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import com.example.platepals.model.Model
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import com.example.platepals.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.UUID


class EditPostActivity : AppCompatActivity() {
    private var postId: String? = null
    private val selectedTagIds = mutableSetOf<String>()
    private val tagViews = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_post)

        postId = intent?.getStringExtra("postId") ?: null
        postId?.let {
            getPostDetails(it)
        }

        loadTags()

        val recipeNameText = findViewById<EditText>(R.id.recipeNameText)
        val ingredientsText = findViewById<EditText>(R.id.ingredientsText)
        val instructionsText = findViewById<EditText>(R.id.instructionsText)
        val saveButton = findViewById<Button>(R.id.save)

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

        val backButton : Button = findViewById(R.id.back)
        backButton.setOnClickListener {
            super.onBackPressed()
        }

    }

    private fun savePost(
        recipeNameText: EditText,
        ingredientsText: EditText,
        instructionsText: EditText
    ) {
        val currentUser = Firebase.auth.currentUser

        val recipeName = recipeNameText.text.toString()
        val ingredients = ingredientsText.text.toString()
        val instructions = instructionsText.text.toString()

        if(!(currentUser?.email ?: "").isEmpty()){
            val newPost = Post(
                id = postId ?: UUID.randomUUID().toString(),
                title = recipeName,
                author=currentUser?.email ?:"",
                ingredients = ingredients,
                instructions = instructions,
                tags = selectedTagIds.toList(),
            )

            Model.shared.addPost(newPost, postId !== null) { success ->
                if (success) {
                    Toast.makeText(this, "Post saved successfully", Toast.LENGTH_SHORT).show()

                    //TODO navigate to home
                } else {
                    Toast.makeText(this, "Error saving post", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this, "Error saving post, try logging in again", Toast.LENGTH_SHORT).show()
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
            runOnUiThread {
                findViewById<EditText>(R.id.recipeNameText).setText(post?.title)
                findViewById<EditText>(R.id.ingredientsText).setText(post?.ingredients)
                findViewById<EditText>(R.id.instructionsText).setText(post?.instructions)

                if((post?.tags?.size ?:0) > 0){
                    for(tagId in post?.tags!!){
                        selectedTagIds.add(tagId)
                    }
                }

                selectTags()

            }
        }
    }

    private fun loadTags() {
        val flow = findViewById<androidx.constraintlayout.helper.widget.Flow>(R.id.tagsFlow)
        val mainLayout = findViewById<ConstraintLayout>(R.id.main)

        Model.shared.getAllTags { tags ->
            runOnUiThread {
                val tagIds = mutableListOf<Int>()

                for (tag in tags) {
                    val tagView = createTagView(tag.name, tag.id)
                    mainLayout.addView(tagView)
                    tagIds.add(tagView.id)
                    tagViews.add(tagView)  // Add the tag view to the list
                }

                flow.referencedIds = tagIds.toIntArray()
                selectTags()  // Call selectTags after loading the tags
            }
        }
    }

    private fun selectTags() {
        // Loop through the tag views in the list
        for (tagView in tagViews) {
            val tagId = tagView.tag.toString()  // Assuming you set the tagId as the tag in the view

            // Check if the tag is in the selectedTagIds set
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
        val tagView = LayoutInflater.from(this).inflate(R.layout.item_tag, null) as TextView
        tagView.id = ViewCompat.generateViewId()
        tagView.text = tagName
        tagView.tag = tagId  // Store the tag ID as a tag

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