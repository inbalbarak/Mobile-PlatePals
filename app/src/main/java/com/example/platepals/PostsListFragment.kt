import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.platepals.PostsListViewModel
import com.example.platepals.adapter.PostsRecyclerAdapter
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this)[PostsListViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        posts = arguments?.getParcelableArrayList(ARG_POSTS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPostsListBinding.inflate(inflater, container, false)

        binding?.recyclerView?.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.layoutManager = layoutManager

        adapter = PostsRecyclerAdapter(posts ?: emptyList())

        adapter?.listener = object : OnItemClickListener {
            override fun onItemClick(post: Post?) {
                Log.d("TAG", "On post clicked name: ${post?.title}")
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

        fun newInstance(posts: List<Post>): PostsListFragment {
            return PostsListFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_POSTS, ArrayList(posts) as ArrayList<out Parcelable>?)
                }
            }
        }
    }
}