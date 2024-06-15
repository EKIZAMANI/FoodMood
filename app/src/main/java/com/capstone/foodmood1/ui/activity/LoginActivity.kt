package com.capstone.foodmood1.ui.activity

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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capstone.foodmood1.BuildConfig
import com.capstone.foodmood1.data.repository.UserRepository
import com.capstone.foodmood1.databinding.ActivityLoginBinding
import com.capstone.foodmood1.ui.model.UserViewModel
import com.capstone.foodmood1.ui.model.ViewModelFactory
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
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userModel: UserViewModel
    private var showPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

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
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
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
                }
            }

            spannable.setSpan(
                ForegroundColorSpan(Color.BLUE),
                32, text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
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

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
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
                    userRepository.createUser(
                        user!!.uid,
                        user.displayName.toString(),
                        "FOMO_google",
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
            userModel = ViewModelProvider(
                this,
                ViewModelFactory(this@LoginActivity)
            )[UserViewModel::class.java]

            currentUser.getIdToken(true)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result?.token?.let { it1 -> userModel.setToken(it1) }
                }
            }

            currentUser.displayName?.let { userModel.setName(it) }
            currentUser.email?.let { userModel.setEmail(it) }
            currentUser.photoUrl?.let { userModel.setPhoto(it.toString()) }
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
}