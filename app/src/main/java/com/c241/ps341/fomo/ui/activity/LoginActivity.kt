@file:Suppress("DEPRECATION")

package com.c241.ps341.fomo.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.c241.ps341.fomo.data.UserData
import com.c241.ps341.fomo.data.repository.UserRepository
import com.c241.ps341.fomo.databinding.ActivityLoginBinding
import com.c241.ps341.fomo.ui.model.UserViewModel
import com.c241.ps341.fomo.ui.model.ViewModelFactory
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.c241.ps341.fomo.BuildConfig
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userModel: UserViewModel
    private lateinit var db: FirebaseDatabase
    private var showPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        userModel = ViewModelProvider(
            this,
            ViewModelFactory(this@LoginActivity)
        )[UserViewModel::class.java]
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        db = Firebase.database
        val userRef = db.reference.child("user")
        val currentUser = auth.currentUser
        loginCheck(currentUser)
//        updateUI(currentUser)

        with(binding) {
            setContentView(root)
            btnLogin.isEnabled = false
            btnPassword.visibility = View.GONE

            etEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btnLogin.isEnabled =
                        etEmail.text.toString()
                            .isNotEmpty() && etEmail.error == null && etPassword.text.toString()
                            .isNotEmpty() && etPassword.error == null
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            etPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        btnPassword.visibility = View.GONE
                    } else {
                        btnPassword.visibility = View.VISIBLE
                    }

                    btnLogin.isEnabled =
                        etEmail.text.toString()
                            .isNotEmpty() && etEmail.error == null && etPassword.text.toString()
                            .isNotEmpty() && etPassword.error == null
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            btnPassword.setOnClickListener {
                showPassword = !showPassword

                if (showPassword) {
                    ivPasswordOff.visibility = View.GONE
                    ivPasswordOn.visibility = View.VISIBLE
                    etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                } else {
                    ivPasswordOff.visibility = View.VISIBLE
                    ivPasswordOn.visibility = View.GONE
                    etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                }
            }

            btnLogin.setOnClickListener {
                val progressDialog = ProgressDialog.show(this@LoginActivity, null, "Harap tunggu")
                auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            val query =
                                userRef.orderByChild("email").equalTo(etEmail.text.toString())

                            query.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    progressDialog.dismiss()
                                    if (dataSnapshot.exists()) {
                                        for (userSnapshot in dataSnapshot.children) {
                                            val user = userSnapshot.getValue(UserData::class.java)
                                            val password = hashPassword(etPassword.text.toString())

                                            if (user?.password == password) {
                                                auth.currentUser?.uid?.let { it1 ->
                                                    userModel.setId(it1)
                                                }

                                                user.name?.let { it1 -> userModel.setName(it1) }
                                                user.email?.let { it1 -> userModel.setEmail(it1) }
                                                user.photoUrl?.let { it1 -> userModel.setPhoto(it1) }
                                                startActivity(
                                                    Intent(
                                                        this@LoginActivity,
                                                        MainActivity::class.java
                                                    )
                                                )
                                                finish()
                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Database error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e(
                                        "FirebaseDatabase",
                                        "Database error: ${databaseError.message}"
                                    )
                                }
                            })
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(
                                baseContext, "Akun tersebut belum terdaftar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

            btnGoogle.setOnClickListener {
                signIn()
            }

            val text = "if you don’t an account you can Register here!"
            val spannable = SpannableString(text)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = Color.BLUE
                }
            }

            spannable.setSpan(
                clickableSpan,
                32, text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvRegister.text = spannable
            tvRegister.movementMethod = LinkMovementMethod.getInstance()
            tvRegister.highlightColor = Color.TRANSPARENT
        }
    }

    private fun signIn() {
        val credentialManager = CredentialManager.create(this)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.CLIENT_ID)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userRepository = UserRepository()
                    val password = hashPassword("FOMO_google")
                    userRepository.createUser(
                        user!!.uid,
                        user.displayName.toString(),
                        password,
                        user.email.toString(),
                        user.photoUrl.toString()
                    )
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            userModel.setId(currentUser.uid)
            currentUser.displayName?.let { userModel.setName(it) }
            currentUser.email?.let { userModel.setEmail(it) }
            currentUser.photoUrl?.let { userModel.setPhoto(it.toString()) }
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun loginCheck(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}