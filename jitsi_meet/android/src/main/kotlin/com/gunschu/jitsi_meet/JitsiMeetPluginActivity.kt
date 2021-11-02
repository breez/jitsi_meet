package com.gunschu.jitsi_meet

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.gunschu.jitsi_meet.JitsiMeetPlugin.Companion.SET_LOCAL_PARTICIPANT_PROPERTY
import com.gunschu.jitsi_meet.JitsiMeetPlugin.Companion.JITSI_MEETING_CLOSE
import com.gunschu.jitsi_meet.JitsiMeetPlugin.Companion.JITSI_PLUGIN_TAG
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

/**
 * Activity extending JitsiMeetActivity in order to override the conference events
 */
class JitsiMeetPluginActivity : JitsiMeetActivity() {
    companion object {
        @JvmStatic
        fun launchActivity(context: Context?,
                           options: JitsiMeetConferenceOptions) {
            var intent = Intent(context, JitsiMeetPluginActivity::class.java).apply {
                action = "org.jitsi.meet.CONFERENCE"
                putExtra("JitsiMeetConferenceOptions", options)
            }
            context?.startActivity(intent)
        }
    }

    var onStopCalled: Boolean = false;

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (isInPictureInPictureMode){
            JitsiMeetEventStreamHandler.instance.onPictureInPictureWillEnter()
        }
        else {
            JitsiMeetEventStreamHandler.instance.onPictureInPictureTerminated()
        }

        if (isInPictureInPictureMode == false && onStopCalled) {
            // Picture-in-Picture mode has been closed, we can (should !) end the call
            getJitsiView().leave()
        }
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent?.action) {
                JITSI_MEETING_CLOSE -> finish()
                SET_LOCAL_PARTICIPANT_PROPERTY -> setLocalParticipantProperty(intent, context)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        onStopCalled = true;
        unregisterReceiver(myReceiver)
    }

    override fun onResume() {
        super.onResume()
        onStopCalled = false
        val intentFilter: IntentFilter = IntentFilter()
        intentFilter.addAction(JITSI_MEETING_CLOSE)
        intentFilter.addAction(SET_LOCAL_PARTICIPANT_PROPERTY)
        registerReceiver(myReceiver, intentFilter)
    }

    override fun onConferenceWillJoin(data: HashMap<String, Any>) {
        Log.d(JITSI_PLUGIN_TAG, String.format("JitsiMeetPluginActivity.onConferenceWillJoin: %s", data))
        JitsiMeetEventStreamHandler.instance.onConferenceWillJoin(data)
        super.onConferenceWillJoin(data)
    }

    override fun onConferenceJoined(data: HashMap<String, Any>) {
        Log.d(JITSI_PLUGIN_TAG, String.format("JitsiMeetPluginActivity.onConferenceJoined: %s", data))
        JitsiMeetEventStreamHandler.instance.onConferenceJoined(data)
        super.onConferenceJoined(data)
    }

    override fun onConferenceTerminated(data: HashMap<String, Any>) {
        Log.d(JITSI_PLUGIN_TAG, String.format("JitsiMeetPluginActivity.onConferenceTerminated: %s", data))
        JitsiMeetEventStreamHandler.instance.onConferenceTerminated(data)
        super.onConferenceTerminated(data)
    }

    override fun onBoost(data: HashMap<String, Any>) {
        Log.d(JITSI_PLUGIN_TAG, String.format("JitsiMeetPluginActivity.onBoost: %s", data))
        JitsiMeetEventStreamHandler.instance.onBoost(data)
        super.onBoost(data)
    }

    override fun changeSatsPerMinute(data: HashMap<String, Any>) {
        Log.d(JITSI_PLUGIN_TAG, String.format("JitsiMeetPluginActivity.changeSatsPerMinute: %s", data))
        JitsiMeetEventStreamHandler.instance.changeSatsPerMinute(data)
        super.changeSatsPerMinute(data)
    }

    override fun setCustomBoostAmount(data: HashMap<String, Any>) {
        Log.d(JITSI_PLUGIN_TAG, String.format("JitsiMeetPluginActivity.setCustomBoostAmount: %s", data))
        JitsiMeetEventStreamHandler.instance.setCustomBoostAmount(data)
        super.setCustomBoostAmount(data)
    }

    override fun setCustomSatsPerMinAmount(data: HashMap<String, Any>) {
        Log.d(JITSI_PLUGIN_TAG, String.format("JitsiMeetPluginActivity.setCustomSatsPerMinAmount: %s", data))
        JitsiMeetEventStreamHandler.instance.setCustomSatsPerMinAmount(data)
        super.setCustomSatsPerMinAmount(data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff();
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn();
    }

    private fun turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // For newer than Android Oreo: call setShowWhenLocked, setTurnScreenOn
            setShowWhenLocked(true)
            setTurnScreenOn(true)

            // If you want to display the keyguard to prompt the user to unlock the phone:
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            // For older versions, do it as you did before.
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }
    }

    private fun turnScreenOffAndKeyguardOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            or WindowManager.LayoutParams.FLAG_FULLSCREEN
                            or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }

    private fun setLocalParticipantProperty(intent: Intent, context: Context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}
