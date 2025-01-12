package com.example.pam_pertemuan15.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.pam_pertemuan15.model.Mahasiswa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NetworkRepositoryMhs(
        private val firestore: FirebaseFirestore
    ) : RepositoryMhs {
        override suspend fun insertMhs(mahasiswa: Mahasiswa) {
            try {
                firestore.collection("Mahasiswa").add(mahasiswa).await()
            } catch (e: Exception) {
                throw Exception("Error adding Mahasiswa: ${e.message}")
            }
        }

        override fun getAllMahasiswa(): Flow<List<Mahasiswa>> = callbackFlow {
            val mhsCollection = firestore.collection("Mahasiswa")
                .orderBy("nim", Query.Direction.ASCENDING)
                .addSnapshotListener{
                        value, error ->
                    if (value != null) {
                        val mhsList = value.documents.mapNotNull {
                            // convert from document firestore to data class
                            it.toObject(Mahasiswa::class.java)
                        }
                        //fungsi untuk mengirim collection ke dataclass
                        trySend(mhsList)
                    }
                }
            awaitClose{
                //menutup collection dari firebase
                mhsCollection.remove()
            }
        }

        override fun getMhs(nim: String): Flow<Mahasiswa> = callbackFlow {
            val mhsDocument = firestore.collection("Mahasiswa")
                .document(nim)
                .addSnapshotListener{ value, error ->
                    if (value != null) {
                        val mhs = value.toObject(Mahasiswa::class.java)!!
                        trySend(mhs)
                    }
                }
            awaitClose{
                mhsDocument.remove()
            }
        }

        override suspend fun deleteMhs(mahasiswa: Mahasiswa) {
            try {
                val querySnapshot = firestore.collection("Mahasiswa")
                    .whereEqualTo("nim", mahasiswa.nim)  // get documentID with field nim
                    .get()
                    .await()

                if (querySnapshot.isEmpty) {
                    Log.e("NetworkRepositoryMhs", "Dokumen dengan nim ${mahasiswa.nim} tidak ditemukan")
                    return
                }

                val document = querySnapshot.documents.first() // Get top 1 if more than 1
                val documentId = document.id  // Get Document ID

                firestore.collection("Mahasiswa")
                    .document(documentId) // search by Document ID location
                    .delete()
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                    .await()
                Log.d("NetworkRepositoryMhs", "Berhasil menghapus data Mahasiswa: ${mahasiswa.nim}")
            } catch (e: Exception) {
                throw Exception("Gagal Menghapus data Mahasiswa: ${e.message}")
            }
        }

        override suspend fun updateMhs(mahasiswa: Mahasiswa) {
            try {
                firestore.collection("Mahasiswa")
                    .document(mahasiswa.nim)
                    .set(mahasiswa)
                    .await()
            } catch (e: Exception) {
                throw Exception("Gagal Mengupdate data Mahasiswa: ${e.message}")
            }
        }
    }
