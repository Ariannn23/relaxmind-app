package com.upn.relaxmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.ui.theme.RelaxBackground
import com.upn.relaxmind.ui.theme.RelaxGreen
import com.upn.relaxmind.ui.theme.RelaxMutedText
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import java.time.LocalDate
import com.upn.relaxmind.data.LocalDataRepository
import com.upn.relaxmind.data.models.Reminder
import com.upn.relaxmind.data.models.ReminderType
import java.time.format.DateTimeFormatter
import java.time.YearMonth
import java.util.UUID

private val ReminderBlue = Color(0xFF2563EB)
private val ReminderGreen = Color(0xFF10B981)
private val ReminderPurple = Color(0xFF7C3AED)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isMonthlyView by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    // Load reminders from repository
    val allReminders = remember { mutableStateListOf<Reminder>() }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        allReminders.clear()
        allReminders.addAll(LocalDataRepository.getReminders(context))
    }
    
    val remindersForDay = remember(selectedDate, allReminders.size) {
        allReminders.filter { it.dateIso == selectedDate.toString() }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Header with Toggle
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Agenda",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Tus recordatorios y citas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RelaxMutedText
                    )
                }
                
                // Toggle Button
                Surface(
                    onClick = { isMonthlyView = !isMonthlyView },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isMonthlyView) Icons.Outlined.FormatListBulleted else Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = ReminderBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isMonthlyView) "Lista" else "Mes",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ReminderBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (isMonthlyView) {
                MonthlyCalendarView(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    reminders = allReminders
                )
            } else {
                WeekStrip(selectedDate = selectedDate, onDaySelected = { selectedDate = it })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reminders List for Selected Day
            if (remindersForDay.isEmpty()) {
                EmptyRemindersState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(remindersForDay) { reminder ->
                        ReminderCard(reminder, onDelete = {
                            LocalDataRepository.deleteReminder(context, it)
                            allReminders.removeIf { r -> r.id == it }
                        })
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .padding(bottom = 80.dp), // Clear navbar
            containerColor = ReminderBlue,
            contentColor = Color.White
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Agregar recordatorio")
        }

        if (showAddDialog) {
            AddReminderBottomSheet(
                onDismiss = { showAddDialog = false },
                onSave = { newReminder ->
                    LocalDataRepository.saveReminder(context, newReminder)
                    allReminders.add(newReminder)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun WeekStrip(selectedDate: LocalDate, onDaySelected: (LocalDate) -> Unit) {
    val today = remember { LocalDate.now() }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in -2..4) { // 2 days ago to 4 days ahead
            val date = today.plusDays(i.toLong())
            val dayName = getDayName(date.dayOfWeek.value).take(1)
            val dayNum = date.dayOfMonth.toString()
            val isSelected = date == selectedDate
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDaySelected(date) }
            ) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) ReminderBlue else RelaxMutedText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) ReminderBlue else Color.Transparent,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = dayNum,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlyCalendarView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    reminders: List<Reminder>
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7 // 0 = Sunday
    
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        // Month Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = RelaxMutedText)
            }
            Text(
                text = "${getMonthName(currentMonth.monthValue)} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowForwardIos, null, tint = RelaxMutedText, modifier = Modifier.size(16.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Days of week header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("D", "L", "M", "M", "J", "V", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = RelaxMutedText
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar Grid
        val totalCells = ((daysInMonth + firstDayOfMonth + 6) / 7) * 7
        Column {
            for (row in 0 until totalCells / 7) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    for (col in 0 until 7) {
                        val dayIndex = row * 7 + col
                        val dayNum = dayIndex - firstDayOfMonth + 1
                        
                        if (dayNum in 1..daysInMonth) {
                            val date = currentMonth.atDay(dayNum)
                            val isSelected = date == selectedDate
                            val hasReminders = reminders.any { it.dateIso == date.toString() }
                            val reminderTypes = reminders.filter { it.dateIso == date.toString() }.map { it.type }.distinct()
                            
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ReminderBlue else Color.Transparent)
                                    .clickable { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = dayNum.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (hasReminders && !isSelected) {
                                        Row(horizontalArrangement = Arrangement.Center) {
                                            reminderTypes.forEach { type ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(if (type == ReminderType.MEDICATION) ReminderGreen else ReminderBlue)
                                                        .padding(horizontal = 1.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.size(40.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun getMonthName(month: Int): String {
    return when(month) {
        1 -> "Enero"; 2 -> "Febrero"; 3 -> "Marzo"; 4 -> "Abril"
        5 -> "Mayo"; 6 -> "Junio"; 7 -> "Julio"; 8 -> "Agosto"
        9 -> "Septiembre"; 10 -> "Octubre"; 11 -> "Noviembre"; 12 -> "Diciembre"
        else -> ""
    }
}

private fun getDayName(dayOfWeek: Int): String {
    return when(dayOfWeek) {
        1 -> "Lunes"; 2 -> "Martes"; 3 -> "Miércoles"; 4 -> "Jueves"
        5 -> "Viernes"; 6 -> "Sábado"; 7 -> "Domingo"
        else -> ""
    }
}@Composable
private fun ReminderCard(reminder: Reminder, onDelete: (String) -> Unit) {
    val isAppointment = reminder.type == ReminderType.MEDICAL_APPOINTMENT
    val icon = if (isAppointment) Icons.Outlined.LocalHospital else Icons.Outlined.Medication
    val color = if (isAppointment) ReminderBlue else ReminderGreen
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = color.copy(0.2f))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { showDeleteConfirm = !showDeleteConfirm }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!reminder.dosage.isNullOrBlank()) {
                        Text(
                            text = "Dosis: ${reminder.dosage}",
                            style = MaterialTheme.typography.bodySmall,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    val details = if (isAppointment) {
                        listOfNotNull(reminder.doctor, reminder.location).joinToString(" - ")
                    } else {
                        reminder.notes ?: ""
                    }
                    if (details.isNotBlank()) {
                        Text(
                            text = details,
                            style = MaterialTheme.typography.bodySmall,
                            color = RelaxMutedText
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = reminder.time,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    if (!reminder.frequency.isNullOrBlank()) {
                        Text(
                            text = reminder.frequency ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = RelaxMutedText
                        )
                    }
                }
            }
            
            androidx.compose.animation.AnimatedVisibility(visible = showDeleteConfirm) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDelete(reminder.id) }) {
                        Text("Eliminar", color = Color(0xFFEF4444), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRemindersState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(ReminderBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = ReminderBlue.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin recordatorios",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tienes el día libre. Toca el botón\n+ para agregar una cita o medicamento.",
            style = MaterialTheme.typography.bodyMedium,
            color = RelaxMutedText,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderBottomSheet(onDismiss: () -> Unit, onSave: (Reminder) -> Unit) {
    var selectedType by remember { mutableStateOf(ReminderType.MEDICATION) }
    var title by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("08:00 AM") }
    var frequency by remember { mutableStateOf("Diario") }
    var doctor by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Nuevo Recordatorio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            // Selector de Tipo
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TypeButton(
                    label = "Medicamento",
                    isSelected = selectedType == ReminderType.MEDICATION,
                    color = ReminderGreen,
                    onClick = { selectedType = ReminderType.MEDICATION }
                )
                TypeButton(
                    label = "Cita Médica",
                    isSelected = selectedType == ReminderType.MEDICAL_APPOINTMENT,
                    color = ReminderBlue,
                    onClick = { selectedType = ReminderType.MEDICAL_APPOINTMENT }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(if (selectedType == ReminderType.MEDICATION) "Nombre del Medicamento" else "Motivo de la Cita") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (selectedType == ReminderType.MEDICATION) {
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosificación (ej: 50mg, 1 tableta)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frecuencia (ej: Diario, Cada 8h)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                OutlinedTextField(
                    value = doctor,
                    onValueChange = { doctor = it },
                    label = { Text("Doctor / Especialista") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicación / Centro de Salud") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Outlined.Map, null, tint = ReminderBlue, modifier = Modifier.size(20.dp)) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Hora") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = selectedDate.toString(),
                    onValueChange = { },
                    label = { Text("Fecha") },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(12.dp),
                    readOnly = true
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas adicionales") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            Reminder(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                type = selectedType,
                                dateIso = selectedDate.toString(),
                                time = time,
                                dosage = dosage.ifBlank { null },
                                doctor = doctor.ifBlank { null },
                                location = location.ifBlank { null },
                                frequency = frequency.ifBlank { null },
                                notes = notes.ifBlank { null }
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == ReminderType.MEDICATION) ReminderGreen else ReminderBlue
                )
            ) {
                Text("Guardar Recordatorio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun TypeButton(label: String, isSelected: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else RelaxMutedText,
            fontWeight = FontWeight.Bold
        )
    }
}
