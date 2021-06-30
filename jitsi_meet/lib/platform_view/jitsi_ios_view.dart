import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:jitsi_meet/jitsi_meet.dart';

class JitsiIOSView extends StatelessWidget {
  final JitsiMeetingOptions options;

  JitsiIOSView(this.options);

  @override
  Widget build(BuildContext context) {
    // This is used in the platform side to register the view.
    final String viewType = 'com.breez.client/jitsi';
    // Pass parameters to the platform side.
    final Map<String?, bool> creationParams = options.getFeatureFlags();

    return UiKitView(
      viewType: viewType,
      layoutDirection: TextDirection.ltr,
      creationParams: creationParams,
      creationParamsCodec: const StandardMessageCodec(),
    );
  }
}
