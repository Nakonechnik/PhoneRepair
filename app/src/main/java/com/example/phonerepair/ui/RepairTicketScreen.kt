package com.example.phonerepair.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phonerepair.model.RepairTicket
import com.example.phonerepair.model.TicketStatus
import com.example.phonerepair.ui.theme.DangerRed
import com.example.phonerepair.ui.theme.MutedGray
import com.example.phonerepair.ui.theme.ReadyGreen
import com.example.phonerepair.ui.theme.TextSecondary
import com.example.phonerepair.ui.theme.WarningAmber
import com.example.phonerepair.viewmodel.RepairTicketViewModel

@Composable
fun RepairTicketScreen(viewModel: RepairTicketViewModel) {
    val ticket = viewModel.ticket
    val screenBg = screenBackground(ticket.status)
    val actionsLocked = ticket.isTerminal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Сервисный центр",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Заявка на ремонт смартфона",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatusBanner(ticket)

        Spacer(modifier = Modifier.height(12.dp))

        TicketInfoCard(ticket)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Свойства",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        PropertyRow("Сложность повреждения", "${ticket.severity} / 5")
        PropertyRow("Диагностика", yesNo(ticket.diagnosticsDone))
        PropertyRow("Запчасти на складе", yesNo(ticket.partsInStock))
        PropertyRow("Согласие клиента", yesNo(ticket.clientApproved))
        PropertyRow("Оплачено", yesNo(ticket.isPaid))
        PropertyRow("Гарантийный ремонт", yesNo(ticket.isWarranty))
        PropertyRow("Смета", if (ticket.estimateRub > 0) "${ticket.estimateRub} ₽" else "—")

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Действия",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (ticket.status == TicketStatus.NEW) {
            SeverityControls(
                severity = ticket.severity,
                enabled = ticket.canChangeSeverity && !actionsLocked,
                onMinus = viewModel::decreaseSeverity,
                onPlus = viewModel::increaseSeverity
            )
            Spacer(modifier = Modifier.height(8.dp))
            ActionButton(
                text = if (ticket.isWarranty) "Снять гарантию" else "Гарантийный ремонт",
                enabled = ticket.canToggleWarranty && !actionsLocked,
                onClick = viewModel::toggleWarranty
            )
        }

        ActionButton(
            text = "Начать диагностику",
            enabled = ticket.canStartDiagnostics && !actionsLocked,
            onClick = viewModel::startDiagnostics
        )
        ActionButton(
            text = "Завершить диагностику",
            enabled = ticket.canFinishDiagnostics && !actionsLocked,
            onClick = viewModel::finishDiagnostics
        )
        ActionButton(
            text = "Запчасти поступили",
            enabled = ticket.canMarkPartsArrived && !actionsLocked,
            onClick = viewModel::markPartsArrived
        )
        ActionButton(
            text = "Клиент согласовал смету",
            enabled = ticket.canApprove && !actionsLocked,
            onClick = viewModel::approveByClient
        )
        ActionButton(
            text = "Оплатить ремонт",
            enabled = ticket.canPay && !actionsLocked,
            onClick = viewModel::pay
        )
        ActionButton(
            text = "Начать ремонт",
            enabled = ticket.canStartRepair && !actionsLocked,
            onClick = viewModel::startRepair
        )
        ActionButton(
            text = "Завершить ремонт",
            enabled = ticket.canCompleteRepair && !actionsLocked,
            onClick = viewModel::completeRepair
        )
        ActionButton(
            text = "Выдать устройство",
            enabled = ticket.canIssue && !actionsLocked,
            onClick = viewModel::issueToClient
        )
        ActionButton(
            text = "Отклонить заявку",
            enabled = ticket.canReject && !actionsLocked,
            onClick = viewModel::reject,
            containerColor = DangerRed
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = viewModel::reset,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сбросить в исходное состояние")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatusBanner(ticket: RepairTicket) {
    val (bg, fg, message) = when (ticket.status) {
        TicketStatus.WAITING_PARTS -> Triple(
            WarningAmber,
            Color.White,
            "Внимание: ремонт нельзя начать, пока нет запчастей, согласия клиента и оплаты (или гарантии)."
        )
        TicketStatus.READY -> Triple(
            ReadyGreen,
            Color.White,
            "Устройство отремонтировано и готово к выдаче."
        )
        TicketStatus.REJECTED -> Triple(
            DangerRed,
            Color.White,
            "Заявка отклонена. Доступно только действие сброса."
        )
        TicketStatus.ISSUED -> Triple(
            MutedGray,
            Color.White,
            "Работа по заявке завершена. Устройство выдано клиенту."
        )
        TicketStatus.REPAIRING -> Triple(
            PrimaryBlueBanner,
            Color.White,
            "Идёт ремонт. Изменение условий заявки заблокировано."
        )
        else -> Triple(
            Color(0xFFE2E8F0),
            Color(0xFF0F172A),
            ticket.statusTitle
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = ticket.statusTitle,
                color = fg,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                color = fg,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private val PrimaryBlueBanner = Color(0xFF1D4ED8)

@Composable
private fun TicketInfoCard(ticket: RepairTicket) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = ticket.ticketNumber,
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
            Text(
                text = ticket.deviceName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PropertyRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SeverityControls(
    severity: Int,
    enabled: Boolean,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Сложность повреждения")
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onMinus, enabled = enabled && severity > 1) {
                Text("−")
            }
            Text(
                text = "$severity",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = onPlus, enabled = enabled && severity < 5) {
                Text("+")
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = Color(0xFFCBD5E1),
            disabledContentColor = Color(0xFF64748B)
        )
    ) {
        Text(text)
    }
}

private fun screenBackground(status: TicketStatus): Color {
    return when (status) {
        TicketStatus.REJECTED -> Color(0xFFFEE2E2)
        TicketStatus.READY -> Color(0xFFDCFCE7)
        TicketStatus.WAITING_PARTS -> Color(0xFFFEF3C7)
        TicketStatus.ISSUED -> Color(0xFFE5E7EB)
        TicketStatus.REPAIRING -> Color(0xFFDBEAFE)
        else -> Color(0xFFF8FAFC)
    }
}

private fun yesNo(value: Boolean): String = if (value) "да" else "нет"
