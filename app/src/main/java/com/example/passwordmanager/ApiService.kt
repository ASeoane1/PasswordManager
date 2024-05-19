package com.example.passwordmanager

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("documents/get_user_documents")
    fun getUserDocuments(@Body requestBody: UserDocumentsRequest): Call<UserDocumentsResponse>

    @Headers("Content-Type: application/json")
    @POST("documents/get_document")
    fun getDocument(@Body requestBody: DocumentRequest): Call<DocumentResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/authenticate")
    fun authenticate(@Body requestBody: AuthRequest): Call<AuthResponse>
}

data class UserDocumentsRequest(
    val user: String
)

data class DocumentRequest(
    val user: String,
    val document_name: String
)

data class UserDocumentsResponse(
    val documents: List<String>
)

data class DocumentResponse(
    val response: Map<String, String>
)

data class AuthRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user_id: String
)
