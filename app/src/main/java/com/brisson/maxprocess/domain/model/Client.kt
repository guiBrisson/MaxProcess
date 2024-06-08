package com.brisson.maxprocess.domain.model

import java.util.Date

data class Client(
    val id: Long,
    val name: String,
    val createdAt: Date,
    val cpf: String?,
    val birthDate: Date?,
    val uf: String?,
    val phones: List<String>?,
)
