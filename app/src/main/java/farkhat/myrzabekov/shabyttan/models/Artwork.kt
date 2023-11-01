package farkhat.myrzabekov.shabyttan.models

data class Artwork(
    val id: Int? = null,
    val author: String? = null,
    val description: String? = null,
    val funFact: String? = null,
    val imageURL: String? = null,
    val title: String? = null,
    val date: String? = null,
    val likesCount: Int? = 0
)