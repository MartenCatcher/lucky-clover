package lucky.clover.ui.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import lucky.clover.R

class PhotosFragment : Fragment() {

    private lateinit var photosViewModel: PhotosViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        photosViewModel =
            ViewModelProviders.of(this).get(PhotosViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_photos, container, false)
        val textView: TextView = root.findViewById(R.id.text_photos)
        photosViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
