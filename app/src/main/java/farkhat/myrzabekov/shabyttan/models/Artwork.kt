package farkhat.myrzabekov.shabyttan.models

data class Artwork(
    var id: Long? = null,
    val author: String? = null,
    val author_ru: String? = null,
    val description: String? = null,
    val description_ru: String? = null,
    val funFact: String? = null,
    val funFact_ru: String? = null,
    val imageURL: String? = null,
    val title: String? = null,
    val title_ru: String? = null,
    var date: String? = null,
    val likesCount: Int? = 0
)