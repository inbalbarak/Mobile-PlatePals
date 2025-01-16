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
import android.widget.EditText


class EditPostActivity : AppCompatActivity() {
    private var postId: String? = null
    private val selectedTagIds = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_post)
        loadTags()

        postId = intent?.getStringExtra("postId") ?: null
        // If postId is not null, get the post details
        postId?.let {
            getPostDetails(it)
        }

    }

    private fun getPostDetails(postId: String) {
        // Fetch post by id using the model
        Model.shared.getPostById(postId) { post ->
            runOnUiThread {
                findViewById<EditText>(R.id.recipeNameText).setText(post?.title)
                findViewById<EditText>(R.id.ingredientsText).setText(post?.ingredients)
                findViewById<EditText>(R.id.instructionsText).setText(post?.instructions)
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
                }

                flow.referencedIds = tagIds.toIntArray()
            }
        }
    }

    private fun createTagView(tagName: String, tagId:String): TextView {
        val tagView = LayoutInflater.from(this).inflate(R.layout.item_tag, null) as TextView
        tagView.id = ViewCompat.generateViewId()
        tagView.text = tagName

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