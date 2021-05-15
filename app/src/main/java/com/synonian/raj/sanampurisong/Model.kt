package com.synonian.raj.sanampurisong

class Model(val music :List<MusicList>)
class MusicList(
    val title : String,
    val artist : String,
    val url : String
)
class Game (val data : List<GameList>)
class GameList  (
    val title : String?,
    val thumbnailUrl: String,
    val thumbnailUrl100: String,
    val url : String,
    val rkScore : Int?,
    val author : String?,
    val rks : Int?,
    val  desc_es : String?,
    val desc_de : String?,
    val desc_fr : String?,
    val  desc_it : String?,
    val orientation : String

)
