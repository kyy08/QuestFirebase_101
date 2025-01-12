package com.example.pam_pertemuan15.di


import com.example.pam_pertemuan15.MahasiswaApp
import com.example.pam_pertemuan15.repository.NetworkRepositoryMhs
import com.example.pam_pertemuan15.repository.RepositoryMhs
import com.google.firebase.firestore.FirebaseFirestore

interface InterfaceContainerApp {
    val repositoryMhs: RepositoryMhs
}

class ContainerApp(private val context: MahasiswaApp): InterfaceContainerApp {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    override val repositoryMhs: RepositoryMhs by lazy {
        NetworkRepositoryMhs(firestore)
    }
}
