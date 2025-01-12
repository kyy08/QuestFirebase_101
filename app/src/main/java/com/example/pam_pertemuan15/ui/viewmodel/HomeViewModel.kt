package com.example.pam_pertemuan15.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_pertemuan15.model.Mahasiswa
import com.example.pam_pertemuan15.repository.RepositoryMhs
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed class HomeUiState {
    data class Success(val data: List<Mahasiswa>) : HomeUiState()
    data class Error(val e: Throwable) : HomeUiState()
    object Loading : HomeUiState()
}

class HomeViewModel (
    private val repoMhs: RepositoryMhs
): ViewModel() {
    var mhsUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getMhs()
    }

    fun deleteMhs (mhs: Mahasiswa) {
        viewModelScope.launch {
            try {
                repoMhs.deleteMhs(mhs)
            } catch (e: Exception) {
                mhsUiState = HomeUiState.Error(e)
            }
        }
    }

    fun getMhs() {
        viewModelScope.launch {
            repoMhs.getAllMahasiswa().onStart {
                mhsUiState = HomeUiState.Loading
            }
                .catch {
                    mhsUiState = HomeUiState.Error(e = it)
                }
                .collect {
                    mhsUiState = if (it.isEmpty()) {
                        HomeUiState.Error(Exception("Belum ada data mahasiswa"))
                    }
                    else {
                        HomeUiState.Success(data = it)
                    }
                }
        }
    }
}

