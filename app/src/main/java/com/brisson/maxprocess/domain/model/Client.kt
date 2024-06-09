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
    )
}
