import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:jitsi_meet/jitsi_meet.dart';

import 'jitsi_android_view.dart';
import 'jitsi_ios_view.dart';

class JitsiView extends StatelessWidget {
  final JitsiMeetingOptions options;

  JitsiView(this.options);

  @override
  Widget build(BuildContext context) {
    switch (defaultTargetPlatform) {
      case TargetPlatform.android:
        return JitsiAndroidView(options);
      case TargetPlatform.iOS:
        return JitsiIOSView(options);
      default:
        throw UnsupportedError("Unsupported platform view");
    }
  }
}