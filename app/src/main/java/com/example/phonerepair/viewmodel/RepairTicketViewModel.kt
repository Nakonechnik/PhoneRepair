package com.example.phonerepair.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.phonerepair.model.RepairTicket
import com.example.phonerepair.model.TicketStatus

class RepairTicketViewModel : ViewModel() {
    var ticket by mutableStateOf(RepairTicket())
        private set

    fun startDiagnostics() {
        if (!ticket.canStartDiagnostics) return
        ticket = ticket.copy(status = TicketStatus.DIAGNOSING)
    }

    fun finishDiagnostics() {
        if (!ticket.canFinishDiagnostics) return
        val estimate = ticket.severity * 2500
        ticket = ticket.copy(
            diagnosticsDone = true,
            estimateRub = estimate,
            status = TicketStatus.WAITING_PARTS,
            partsInStock = ticket.severity <= 2
        )
    }

    fun markPartsArrived() {
        if (!ticket.canMarkPartsArrived) return
        ticket = ticket.copy(partsInStock = true)
    }

    fun approveByClient() {
        if (!ticket.canApprove) return
        ticket = ticket.copy(clientApproved = true)
    }

    fun pay() {
        if (!ticket.canPay) return
        ticket = ticket.copy(isPaid = true)
    }

    fun toggleWarranty() {
        if (!ticket.canToggleWarranty) return
        ticket = ticket.copy(isWarranty = !ticket.isWarranty)
    }

    fun increaseSeverity() {
        if (!ticket.canChangeSeverity) return
        if (ticket.severity >= 5) return
        ticket = ticket.copy(severity = ticket.severity + 1)
    }

    fun decreaseSeverity() {
        if (!ticket.canChangeSeverity) return
        if (ticket.severity <= 1) return
        ticket = ticket.copy(severity = ticket.severity - 1)
    }

    fun startRepair() {
        if (!ticket.canStartRepair) return
        ticket = ticket.copy(status = TicketStatus.REPAIRING)
    }

    fun completeRepair() {
        if (!ticket.canCompleteRepair) return
        ticket = ticket.copy(status = TicketStatus.READY)
    }

    fun issueToClient() {
        if (!ticket.canIssue) return
        ticket = ticket.copy(status = TicketStatus.ISSUED)
    }

    fun reject() {
        if (!ticket.canReject) return
        ticket = ticket.copy(status = TicketStatus.REJECTED)
    }

    fun reset() {
        ticket = RepairTicket()
    }
}
