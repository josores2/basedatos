package com.josesorli.misamigos

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback

import androidx.core.view.get

//Importamos las librerías de AdMob de Google
import com.google.android.gms.ads.MobileAds


class publiHandler (contexto : Context) {

    private var mAdManagerInterstitialAd : AdManagerInterstitialAd? = null
    private val actividad = contexto as Activity
    private var TAG = actividad.javaClass.simpleName

    private var adRequest = AdManagerAdRequest.Builder().build()

    //Función para iniciializar la llamada a la publicidad de AdMob
    fun inicializarPubli() {

        MobileAds.initialize(actividad) {}
    }


    //Función para controlar lo que el usuario hace con la publicidad
    fun manejarAccionPublicidad() {
        mAdManagerInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mAdManagerInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                mAdManagerInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
    }

    //Función para cargar la publicidad, antes de mostrarla
    fun cargarPublicidad() {
        AdManagerInterstitialAd.load(
            actividad,
            "ca-app-pub-5549461559475994/3545336604",
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.toString())
                    mAdManagerInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mAdManagerInterstitialAd = interstitialAd
                }
            })
    }

    //Función que muestra la publicidad intersicial, una vez se ha cargado previamente
    fun mostrarPubli() {
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd?.show(actividad)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

}