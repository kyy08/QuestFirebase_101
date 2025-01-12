package com.example.pam_pertemuan15.repository

import com.example.pam_pertemuan15.model.Mahasiswa
import kotlinx.coroutines.flow.Flow

interface RepositoryMhs {
    // operasi create update delete pakai suspend
    suspend fun insertMhs(mahasiswa: Mahasiswa)
    fun getAllMahasiswa(): Flow<List<Mahasiswa>>
    fun getMhs (nim: String): Flow<Mahasiswa>
    suspend fun deleteMhs(mahasiswa: Mahasiswa)
    suspend fun updateMhs(mahasiswa: Mahasiswa)
}