package com.example.phonerepair.model

enum class TicketStatus {
    NEW,
    DIAGNOSING,
    WAITING_PARTS,
    REPAIRING,
    READY,
    ISSUED,
    REJECTED
}

data class RepairTicket(
    val ticketNumber: String = "SC-2401",
    val deviceName: String = "Samsung Galaxy S23",
    val status: TicketStatus = TicketStatus.NEW,
    val severity: Int = 2,
    val diagnosticsDone: Boolean = false,
    val partsInStock: Boolean = false,
    val clientApproved: Boolean = false,
    val isPaid: Boolean = false,
    val isWarranty: Boolean = false,
    val estimateRub: Int = 0
) {
    val canStartDiagnostics: Boolean
        get() = status == TicketStatus.NEW

    val canFinishDiagnostics: Boolean
        get() = status == TicketStatus.DIAGNOSING

    val canMarkPartsArrived: Boolean
        get() = status == TicketStatus.WAITING_PARTS && !partsInStock

    val canApprove: Boolean
        get() = status in listOf(
            TicketStatus.DIAGNOSING,
            TicketStatus.WAITING_PARTS
        ) && diagnosticsDone && !clientApproved

    val canPay: Boolean
        get() = status in listOf(
            TicketStatus.WAITING_PARTS,
            TicketStatus.REPAIRING,
            TicketStatus.READY
        ) && !isPaid && !isWarranty && estimateRub > 0

    val canToggleWarranty: Boolean
        get() = status == TicketStatus.NEW

    val canChangeSeverity: Boolean
        get() = status == TicketStatus.NEW

    val canStartRepair: Boolean
        get() = status == TicketStatus.WAITING_PARTS &&
            diagnosticsDone &&
            partsInStock &&
            clientApproved &&
            (isPaid || isWarranty)

    val canCompleteRepair: Boolean
        get() = status == TicketStatus.REPAIRING

    val canIssue: Boolean
        get() = status == TicketStatus.READY && (isPaid || isWarranty)

    val canReject: Boolean
        get() = status in listOf(
            TicketStatus.NEW,
            TicketStatus.DIAGNOSING,
            TicketStatus.WAITING_PARTS
        )

    val isTerminal: Boolean
        get() = status == TicketStatus.ISSUED || status == TicketStatus.REJECTED

    val statusTitle: String
        get() = when (status) {
            TicketStatus.NEW -> "Новая заявка"
            TicketStatus.DIAGNOSING -> "На диагностике"
            TicketStatus.WAITING_PARTS -> "Ожидание запчастей"
            TicketStatus.REPAIRING -> "В ремонте"
            TicketStatus.READY -> "Готов к выдаче"
            TicketStatus.ISSUED -> "Выдан клиенту"
            TicketStatus.REJECTED -> "Отклонён"
        }
}
