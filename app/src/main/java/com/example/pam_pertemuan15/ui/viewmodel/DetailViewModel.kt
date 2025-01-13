package com.example.pam_pertemuan15.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_pertemuan15.model.Mahasiswa
import com.example.pam_pertemuan15.navigation.DestinasiDetail
import com.example.pam_pertemuan15.repository.RepositoryMhs
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel (
    savedStateHandle: SavedStateHandle,
    private val repositoryMhs: RepositoryMhs,

    ) : ViewModel() {
    private val nim: String = checkNotNull(savedStateHandle[DestinasiDetail.titleRes])

    val detailUiState: StateFlow<DetailUiState> = repositoryMhs.getMhs(nim)
        .filterNotNull()
        .map {
            DetailUiState(
                detailUiEvent = it.toDetailUiEvent(),
                isLoading = false,
            )
        }
        .onStart {
            emit(DetailUiState(isLoading = true))
            delay(600)
        }
        .catch {
            emit(
                DetailUiState(
                    isLoading = false,
                    isError = true,
                    errorMessage = it.message ?: "Terjadi kesalahan",
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2000),
            initialValue = DetailUiState(
                isLoading = true,
            ),
        )

    fun deleteMhs() {
        detailUiState.value.detailUiEvent.toMahasiswa().let {
            viewModelScope.launch {
                repositoryMhs.deleteMhs(it)
            }
        }
    }
}

data class DetailUiState(
    val detailUiEvent: MahasiswaEvent = MahasiswaEvent (),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
) {
    val isUiEventEmpty: Boolean
        get() = detailUiEvent == MahasiswaEvent()

    val isUiEventNotEmpty: Boolean
        get() = detailUiEvent != MahasiswaEvent ()
}

/*
Data class untuk menampung data yang akan ditampilkan di UI
*/

//memindahkan data dari entity ke ui
fun Mahasiswa.toDetailUiEvent () : MahasiswaEvent {
    return MahasiswaEvent(
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
}