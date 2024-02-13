import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseManager(private val firebaseAnalytics: FirebaseAnalytics) {

    fun logEvent(eventName: String, params: Bundle?) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    fun logError(message: String, throwable: Throwable) {
        //FirebaseCrashlytics.getInstance().recordException(throwable)
        //FirebaseCrashlytics.getInstance().log(message)
    }

}