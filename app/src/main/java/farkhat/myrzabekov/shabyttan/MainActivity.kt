package farkhat.myrzabekov.shabyttan

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import farkhat.myrzabekov.shabyttan.databinding.ActivityMainBinding
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.repositories.FirestoreRepository


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        bottomNavigationView = binding.bottomnavigation

        bottomNavigationView.setupWithNavController(navController)


//        generateAndAddData()

//        addArtworkToFirebaseDatabase()

//        val apiInterface = ApiUtilities.getInstance().create(ApiInterface::class.java)
//
//        val artworksRepository = ArtworksRepository(apiInterface)
//
//        artworksViewModel = ViewModelProvider(this, ArtworksViewModelFactory(artworksRepository))[ArtworksViewModel::class.java]
//
//        artworksViewModel.artworks.observe(this) {
//            Log.d("MyTag", it.toString())
//            Log.d("MyTag", it.data[0].title)
//        }
    }

    private fun generateAndAddData() {
        val artworks = listOf(
            // Vincent van Gogh artworks
            Artwork(
                id = 0,
                "Vincent van Gogh",
                "An iconic work of post-Impressionism, Starry Night is one of the most recognized pieces in the history of Western art.",
                "Van Gogh painted Starry Night while in an asylum in Saint-Rémy, in 1889.",
                "https://example.com/starrynight.jpg",
                "Starry Night",
            ),
            Artwork(
                id = 1,
                "Vincent van Gogh",
                "Sunflowers are the subject of two series of still life paintings by Vincent van Gogh. The earlier series executed in Paris depicts the flowers lying on the ground, while the second set depicts bouquets in vases.",
                "There are total of five 'Sunflower' paintings, each with a different arrangement.",
                "https://example.com/sunflowers.jpg",
                "Sunflowers",
            ),

            // Claude Monet artworks
            Artwork(
                id = 2,
                "Claude Monet",
                "The paintings depict Monet's flower garden at his home in Giverny, and were the main focus of Monet's artistic production during the last thirty years of his life.",
                "Water Lilies is a series of approximately 250 oil paintings by French Impressionist Claude Monet.",
                "Water Lilies",
                "https://example.com/waterlilies.jpg"
            ),
            Artwork(
                id = 3,
                "Claude Monet",
                "This painting became the source of the movement's name, after Louis Leroy's article 'The Exhibition of the Impressionists' satirically implied that the painting was at most, a sketch.",
                "This artwork gave birth to the term 'Impressionism'.",
                "https://example.com/impression_sunrise.jpg",
                "Impression, Sunrise",
            )

        )
        val repository = FirestoreRepository()
        for (artwork in artworks) {
            repository.addArtwork(artwork)
        }
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is TextInputEditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun addArtworkToFirebaseDatabase() {
        val db = Firebase.firestore

        val artwork = hashMapOf(
            "image_url" to "https://openaccess-cdn.clevelandart.org/1953.155/1953.155_web.jpg",
            "title" to "Spring Flowers",
            "author" to "Claude Monet",
            "description" to "This early work reveal's Monet's fascination with capturing the transitory effects that became the primary focus of his later innovations. Painted with almost scientific accuracy, this still life has a freshness and immediacy derived partly from its composition. Isolated against a dark background, the fully mature peonies, potted hydrangeas, and basketed lilacs spill downward and outward from the geraniums at the rear. At the same time, Monet's energetic brushwork conveys the sparkling play of light on leaves and petals.",
            "fun_fact" to "Monet is quoted as saying, \"I perhaps owe having become a painter to flowers.\" He painted this work in 1864, the first productive year of his career.",
        )

        db.collection("artworks")
            .add(artwork)
            .addOnSuccessListener { documentReference ->
                Log.d("MyTag", "DocumentSnapshot added with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.w("MyTag", "Error adding document", e)
            }
    }
}