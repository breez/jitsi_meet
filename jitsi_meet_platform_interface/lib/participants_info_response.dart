class ParticipantsInfoResponse {
  final bool isSuccess;
  final String? message;
  final dynamic? error;

  ParticipantsInfoResponse({
    required this.isSuccess,
    this.message,
    this.error,
  });

  @override
  String toString() {
    return 'ParticipantsInfoResponse{isSuccess: $isSuccess, '
        'message: $message, error: $error}';
  }
}
