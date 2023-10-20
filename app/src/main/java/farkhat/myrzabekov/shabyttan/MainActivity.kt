package farkhat.myrzabekov.shabyttan

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import farkhat.myrzabekov.shabyttan.databinding.ActivityMainBinding


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