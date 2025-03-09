import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.platepals.HomeActivity
import com.example.platepals.PostsListViewModel
import com.example.platepals.R
import com.example.platepals.databinding.FragmentPostsListBinding
import com.example.platepals.model.Model
import com.example.platepals.model.Post

interface OnItemClickListener {
    fun onItemClick(post: Post?)
}

class PostsListFragment : Fragment() {

    private var adapter: PostsRecyclerAdapter? = null
    private var binding: FragmentPostsListBinding? = null
    private var viewModel: PostsListViewModel? = null
    private var posts: List<Post>? = null
    private var listener: OnItemClickListener? = null
    private var editable: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this)[PostsListViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        posts = arguments?.getParcelableArrayList(ARG_POSTS)
        editable = arguments?.getBoolean((ARG_EDITABLE)) == true

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostsListBinding.inflate(inflater, container, false)

        binding?.recyclerView?.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.layoutManager = layoutManager

        adapter = PostsRecyclerAdapter(posts ?: emptyList(), editable) { postId ->
            Model.shared.deletePostById(postId){result->
                if(result == false){
                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show()

//                    Toast.makeText(this,"authentication failed", Toast.LENGTH_SHORT).show()
                    //TODO banner
                }else{
//                    posts = posts?.filter { post -> post.id != postId }

                    val position = posts?.indexOfFirst { it.id == postId } ?: -1
                    if (position != -1) {
                        posts = posts?.filter { it.id != postId }
                        adapter?.notifyItemRemoved(position)
                    }
                    //refresh
                }
            }
        }
        binding?.recyclerView?.adapter = adapter

        adapter?.listener = object : OnItemClickListener {
            override fun onItemClick(post: Post?) {
                Log.d("TAG", "On post clicked name: ${post?.title}")


                post?.let {
                    val bundle = bundleOf("post" to it)

                    findNavController().navigate(
                        R.id.action_homeFragment_to_recipeDetailsFragment,
                        bundle
                    )
                }
            }
        }

        binding?.recyclerView?.adapter = adapter

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        private const val ARG_POSTS = "posts"
        private const val ARG_EDITABLE = "editable"

        fun newInstance(posts: List<Post>, editable: Boolean): PostsListFragment {
            return PostsListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_EDITABLE, editable)
                    putParcelableArrayList(ARG_POSTS, ArrayList(posts) as ArrayList<out Parcelable>?)
                }
            }
        }
    }

}