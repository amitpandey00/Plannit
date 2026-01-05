package com.example.plannit.data.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val context: Context) {

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getWebClientId())
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/calendar.events"))
            .build()

        GoogleSignIn.getClient(context, gso)
    }

    private fun getWebClientId(): String {
        // Get Web Client ID from google_config.xml
        return try {
            context.resources.getString(
                context.resources.getIdentifier("google_web_client_id", "string", context.packageName)
            )
        } catch (e: Exception) {
            // Fallback to android client id if web client id not found
            context.resources.getString(
                context.resources.getIdentifier("google_client_id", "string", context.packageName)
            )
        }
    }
    
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    suspend fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }
    
    suspend fun getIdToken(): String? {
        val account = getLastSignedInAccount() ?: return null
        return account.idToken
    }

    fun getAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }
    
    suspend fun signOut() {
        try {
            googleSignInClient.signOut().await()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun revokeAccess() {
        try {
            googleSignInClient.revokeAccess().await()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(context) != null
    }
}
