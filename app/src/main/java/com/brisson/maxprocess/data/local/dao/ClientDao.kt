package com.brisson.maxprocess.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.brisson.maxprocess.data.local.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM client")
    fun getAll(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM client WHERE id = :id")
    fun loadById(id: Long): ClientEntity?

    @Query("SELECT * FROM client WHERE name LIKE '%' || :searchQuery || '%'")
    fun loadSearch(searchQuery: String?): Flow<List<ClientEntity>>

    @Upsert
    fun upsert(client: ClientEntity): Long?

    @Delete
    fun delete(client: ClientEntity)
}
