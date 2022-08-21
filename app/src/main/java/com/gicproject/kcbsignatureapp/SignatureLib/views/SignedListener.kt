package com.gicproject.kcbsignatureapp.SignatureLib.views

interface SignedListener {
    fun onStartSigning()
    fun onSigned()
    fun onClear()
}
