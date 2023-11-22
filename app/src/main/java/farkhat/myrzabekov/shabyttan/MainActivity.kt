package farkhat.myrzabekov.shabyttan

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import farkhat.myrzabekov.shabyttan.databinding.ActivityMainBinding
import farkhat.myrzabekov.shabyttan.fragments.ArtworkBottomSheetFragment
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.viewmodels.FirestoreViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: FirestoreViewModel by viewModels()
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupNavigation()
        handleIntent(intent)

//        addEmptyRussianFields()
//        decrementDateByOne()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrementDateByOne() {
        val db = FirebaseFirestore.getInstance()
        val artworksCollection = db.collection("Artworks")

        artworksCollection.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val batch = db.batch()
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                for (document in task.result!!) {
                    val dateString = document.getString("date")
                    if (dateString != null) {
                        val date = LocalDate.parse(dateString, dateFormatter)
                        val decrementedDate = date.minusDays(1)
                        val newDateString = decrementedDate.format(dateFormatter)
                        batch.update(document.reference, "date", newDateString)
                    }
                }

                batch.commit().addOnSuccessListener {
                    println("All dates in 'Artworks' collection have been decremented by one day.")
                }.addOnFailureListener { e ->
                    println("Error updating documents: ${e.message}")
                }
            } else {
                println("Error getting documents: ${task.exception?.message}")
            }
        }
    }

    fun addEmptyRussianFields() {
        val db = FirebaseFirestore.getInstance()

        val artworksCollection = db.collection("Artworks")
            .whereEqualTo("date", "22/11/2023")

        artworksCollection.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val batch = db.batch()

                for (document in task.result!!) {
                    // Create a map with the new fields
                    val updates = hashMapOf(
                        "author_ru" to "",
                        "description_ru" to "",
                        "title_ru" to "",
                        "funFact_ru" to ""
                    )

                    batch.update(document.reference, updates as Map<String, Any>)
                }

                batch.commit().addOnSuccessListener {
                    println("All documents in 'Artworks' collection updated with empty Russian fields.")
                }.addOnFailureListener { e ->
                    println("Error updating documents: ${e.message}")
                }
            } else {
                println("Error getting documents: ${task.exception?.message}")
            }
        }
    }


    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
            ?: return
        navController = navHostFragment.navController
        binding.bottomnavigation.setupWithNavController(navController)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is TextInputEditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            if (Intent.ACTION_VIEW == it.action && it.data != null) {
                handleDeepLink(it.data!!)
            }
        }
    }

    private fun handleDeepLink(data: Uri) {
        data.pathSegments.takeIf { it.size > 1 && it[0] == "artwork" }?.let {
            viewModel.fetchArtworkById(it[1].toLong())
            viewModel.deeplinkArtworkLiveData.observe(this, ::onArtworkClick)
        }
    }

    private fun onArtworkClick(artwork: Artwork?) {
        artwork?.let {
            val bottomSheetFragment = ArtworkBottomSheetFragment.newInstance(
                it.id,
                it.title,
                it.author,
                it.description,
                it.funFact,
                it.imageURL
            )
            bottomSheetFragment.show(supportFragmentManager, "ArtworkBottomSheetTag")
        }
    }
}