package com.example.platepals

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.platepals.databinding.FragmentRecipeDetailsBinding
import com.example.platepals.model.Model
import com.example.platepals.model.Post
import java.math.BigDecimal
import java.math.RoundingMode
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class RecipeDetailsFragment : Fragment() {

    private var binding: FragmentRecipeDetailsBinding? = null
    private var post: Post? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        post = arguments?.getParcelable("post")

        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        binding?.apply {
            recipeTitle.text = post?.title
            rating.text = post?.rating.toString()
            instructionsTextView.text = post?.instructions
            ingredientsTextView.text = post?.ingredients
            authorName.text = post?.author
            creationDate.text = dateFormat.format(post?.createdAt)

            post?.imageUrl?.let {
                if (it.isNotBlank()) {
                    Picasso.get()
                        .load(it)
                        .placeholder(R.drawable.recipe_default)
                        .into(binding?.recipeImage)
                }
            }
        }

            submitRatingButton.setOnClickListener {
                submitRating(ratingBar.rating.toInt())
            }

            backBtn.setOnClickListener{
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        loadTags()

        return binding?.root
    }

    private fun loadTags() {
        val flow = binding?.tagsFlow
        val mainLayout = binding?.main

        Model.shared.getTagsByIds(post?.tags ?: emptyList()) { tags ->
            activity?.runOnUiThread {
                val tagIds = mutableListOf<Int>()

                for (tag in tags) {
                    val tagView = createTagView(tag.name, tag.id)
                    mainLayout?.addView(tagView)
                    tagIds.add(tagView.id)
                }

                flow?.referencedIds = tagIds.toIntArray()
            }
        }
    }

    private fun createTagView(tagName: String, tagId: String): TextView {
        val tagView = LayoutInflater.from(requireContext()).inflate(R.layout.item_tag, binding?.root, false) as TextView
        tagView.id = ViewCompat.generateViewId()
        tagView.text = tagName
        tagView.tag = tagId

        return tagView
    }

    private fun submitRating(value: Int) {
        post?.let {
            val currentRatingSum = it.ratingSum?.toInt() ?: 0
            val currentRatingCount = it.ratingCount?.toInt() ?: 0

            val newRatingSum = currentRatingSum + value
            val newRatingCount = currentRatingCount + 1

            val updatedPost = it.copy(
                ratingSum = newRatingSum,
                ratingCount = newRatingCount,
            )

            Model.shared.addPost(updatedPost, true) { success ->
                activity?.runOnUiThread {
                    if (success) {
                        val newRating = BigDecimal(newRatingSum.toDouble() / newRatingCount.toDouble()).setScale(2, RoundingMode.HALF_UP).toDouble()
                        binding?.rating?.text = newRating.toString()
                        post = updatedPost
                        Toast.makeText(context, "Rating submitted successfully!", Toast.LENGTH_SHORT).show()
                        binding?.ratingBar?.rating = 0f
                    } else {
                        Toast.makeText(context, "Failed to submit rating - Please try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        private const val ARG_POST = "post"

        fun newInstance(post: Post): RecipeDetailsFragment {
            return RecipeDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_POST, post)
                }
            }
        }
    }
}