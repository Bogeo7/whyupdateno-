package com.example.app

data class recipe(
    val name: String,
    var Ingredient : List<String>,
    val chief: String,
    val step1: String,
    val step2: String,
    val step3: String,
    val step4: String,
    val step5: String,
    val step6: String,
    val step7: String,
    val step8: String,
    val url: String,
    var matchCount : Int
)

data class refrige(
    val name: String
)

data class recycler(
    var Tname: String,
    var Tcheif: String
)