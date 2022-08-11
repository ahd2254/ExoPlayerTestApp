package com.fanap.exoplayertestapp

import java.io.Serializable


class Movie(
    val id: Int,
    val name: String,
    val url: String,
    val cover: String,
    val streamType: String
):Serializable
