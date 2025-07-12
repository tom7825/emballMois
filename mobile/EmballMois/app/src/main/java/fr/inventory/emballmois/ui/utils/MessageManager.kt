package fr.inventory.emballmois.ui.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageManager @Inject constructor() {
    private val _messageEvents = MutableSharedFlow<String>()
    val messageEvents = _messageEvents.asSharedFlow()
    suspend fun showMessage(message: String) = _messageEvents.emit(message)
}