sealed class PermissionAction {
    object OnPermissionGranted : PermissionAction()
    object OnPermissionDenied : PermissionAction()
}
