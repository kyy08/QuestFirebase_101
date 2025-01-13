package com.example.pam_pertemuan15.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_pertemuan15.model.Mahasiswa
import com.example.pam_pertemuan15.repository.RepositoryMhs
import kotlinx.coroutines.launch

data class MahasiswaEvent (
    val nim: String = "",
    val nama: String = "",
    val gender: String = "",
    val alamat: String = "",
    val kelas: String = "",
    val angkatan: String = "",
    val judulskripsi: String = "",
    val pembimbing1: String = "",
    val pembimbing2: String = "",
)

fun MahasiswaEvent.toMahasiswa(): Mahasiswa = Mahasiswa(
    nim = nim,
    nama = nama,
    gender = gender,
    alamat = alamat,
    kelas = kelas,
    angkatan = angkatan,
    judulskripsi = judulskripsi,
    pembimbing1 = pembimbing1,
    pembimbing2 = pembimbing2,
)

data class FormErrorState (
    val nim: String? = null,
    val nama: String? = null,
    val gender: String? = null,
    val alamat: String? = null,
    val kelas: String? = null,
    val angkatan: String? = null,
    val judulskripsi: String? = null,
    val pembimbing1: String? = null,
    val pembimbing2: String? = null,
) {
    fun isValid(): Boolean {
        return nim == null && nama == null && gender == null && alamat == null && kelas == null && angkatan == null
                && judulskripsi == null && pembimbing1 == null && pembimbing2 == null
    }
}
data class InsertUiState(
    val insertUiEvent: MahasiswaEvent = MahasiswaEvent(),
    val isEntryValid: FormErrorState = FormErrorState()
)

sealed class FormState {
    object Idle : FormState()
    object Loading : FormState()
    data class Success(val message: String) : FormState()
    data class Error(val message: String) : FormState()
}

class InsertViewModel (
    private val mhs: RepositoryMhs
): ViewModel() {

    var uiEvent: InsertUiState by mutableStateOf(InsertUiState())
        private set

    var uiState: FormState by mutableStateOf(FormState.Idle)
        private set

    // Memperbarui State berdasarkan input
    fun updateUiEvent(event: MahasiswaEvent) {
        uiEvent = uiEvent.copy(insertUiEvent = event)
    }

    // Validasi data input pengguna
    fun validateFields (): Boolean {
        val event = uiEvent.insertUiEvent
        val errorState = FormErrorState(
            nim = if (event.nim.isEmpty()) "NIM tidak boleh kosong" else null,
            nama = if (event.nama.isEmpty()) "Nama tidak boleh kosong" else null,
            gender = if (event.gender.isEmpty()) "Jenis Kelamin tidak boleh kosong" else null,
            alamat = if (event.alamat.isEmpty()) "Alamat tidak boleh kosong" else null,
            kelas = if (event.kelas.isEmpty()) "Kelas tidak boleh kosong" else null,
            angkatan = if (event.angkatan.isEmpty()) "Angkatan tidak boleh kosong" else null,
            judulskripsi = if (event.judulskripsi.isEmpty()) "Judulskripsi tidak boleh kosong" else null,
            pembimbing1 = if (event.pembimbing1.isEmpty()) "Pembimbing1 tidak boleh kosong" else null,
            pembimbing2 = if (event.pembimbing2.isEmpty()) "Pembimbing2 tidak boleh kosong" else null
        )
        uiEvent = uiEvent.copy(isEntryValid = errorState)
        return errorState.isValid()
    }

    fun insertMhs () {
        if (validateFields()) {
            viewModelScope.launch {
                uiState = FormState.Loading
                try {
                    mhs.insertMhs(uiEvent.insertUiEvent.toMahasiswa())
                    uiState = FormState.Success("Berhasil Menambahkan Mahasiswa")
                } catch (e: Exception) {
                    uiState = FormState.Error("Gagal Menambahkan Mahasiswa")
                }
            }
        } else {
            uiState = FormState.Error("Data Tidak Valid")
        }
    }

    fun resetForm() {
        uiEvent = InsertUiState()
        uiState = FormState.Idle
    }

    fun resetSnackBarMessage() {
        uiState = FormState.Idle
    }
}