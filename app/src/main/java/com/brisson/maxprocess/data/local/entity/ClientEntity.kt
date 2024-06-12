package com.brisson.maxprocess.data.local.entity

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brisson.maxprocess.ui.util.generateRandomPastelColor
import java.util.Date

@Entity(tableName = "client")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    @ColumnInfo(name = "created_at") val createdAt: Date = Date(),
    val cpf: String?,
    @ColumnInfo(name = "birth_date") val birthDate: Date?,
    val uf: String?,
    val phones: List<String>?,
    @ColumnInfo(name = "avatar_color") val avatarColor: Color = Color.generateRandomPastelColor(),
)
