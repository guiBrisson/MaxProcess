package com.brisson.maxprocess.domain.model

import androidx.compose.ui.graphics.Color
import com.brisson.maxprocess.ui.util.generateRandomPastelColor
import java.util.Date

data class Client(
    val id: Long,
    val name: String,
    val createdAt: Date,
    val cpf: String?,
    val birthDate: Date?,
    val uf: String?,
    val phones: List<String>?,
    val avatarColor: Color,
) {
    constructor(
        name: String,
        cpf: String?,
        birthDate: Date?,
        uf: String?,
        phones: List<String>?,
    ) : this(
        id = 0,
        name = name,
        createdAt = Date(),
        cpf = cpf,
        birthDate = birthDate,
        uf = uf,
        phones = phones,
        avatarColor = Color.generateRandomPastelColor(),
    )
}
