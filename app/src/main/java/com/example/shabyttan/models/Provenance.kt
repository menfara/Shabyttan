package com.example.shabyttan.models

data class Provenance(
    val citations: List<Any>,
    val date: String,
    val description: String,
    val footnotes: List<String>
)