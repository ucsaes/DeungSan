package com.example.deungsan.data.model

data class Mountain(
    val id: Int,
    val name: String,
    val height: Int,
    val location: String,
    val imagePath: String,
    var text: String = ""
)