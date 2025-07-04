package com.example.deungsan.data

import java.io.IOException

fun getJsonDataFromAsset(): String? {
    val jsonString: String
    try {
        jsonString = requireActivity().assets.open("communityData.json")
            .bufferReader()
            .use { it.readText() }
    }catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}
