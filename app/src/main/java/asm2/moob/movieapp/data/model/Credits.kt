package asm2.moob.movieapp.data.model

import com.google.gson.annotations.SerializedName

data class Credits(
    val id: Int,
    val cast: List<Cast>
)

data class Cast(
    val id: Int,
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String?,
    val character: String
) 