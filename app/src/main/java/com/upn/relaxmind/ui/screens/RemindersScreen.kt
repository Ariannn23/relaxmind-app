package com.upn.relaxmind.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.upn.relaxmind.data.LocalDataRepository
import com.upn.relaxmind.data.models.Reminder
import com.upn.relaxmind.data.models.ReminderType
import com.upn.relaxmind.ui.theme.RelaxMutedText
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.UUID

private val ReminderBlue = Color(0xFF2563EB)
private val ReminderGreen = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isMonthlyView by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showDayDetail by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<Reminder?>(null) }
    
    val allReminders = remember { mutableStateListOf<Reminder>() }
    
    LaunchedEffect(Unit) {
        allReminders.clear()
        allReminders.addAll(LocalDataRepository.getReminders(context))
    }
    
    val remindersForDay = remember(selectedDate, allReminders.size) {
        allReminders.filter { reminder ->
            if (reminder.isWeekly && reminder.repeatDays != null) {
                reminder.repeatDays.contains(selectedDate.dayOfWeek.value)
            } else {
                reminder.dateIso == selectedDate.toString()
            }
        }.sortedBy { it.time }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Agenda", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Tus recordatorios y citas", style = MaterialTheme.typography.bodyMedium, color = RelaxMutedText)
                }
                
                Surface(
                    onClick = { isMonthlyView = !isMonthlyView },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isMonthlyView) Icons.Outlined.FormatListBulleted else Icons.Outlined.CalendarMonth,
                            null, tint = ReminderBlue, modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isMonthlyView) "Lista" else "Mes", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ReminderBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (isMonthlyView) {
                MonthlyCalendarView(selectedDate, { 
                    selectedDate = it
                    showDayDetail = true 
                }, allReminders)
            } else {
                WeekStrip(selectedDate, { selectedDate = it })
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (remindersForDay.isEmpty()) {
                EmptyRemindersState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(remindersForDay) { reminder ->
                        ReminderCard(reminder, 
                            onDelete = { id ->
                                LocalDataRepository.deleteReminder(context, id)
                                allReminders.removeIf { r -> r.id == id }
                            },
                            onEdit = { 
                                editingReminder = it
                                showAddDialog = true 
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { 
                editingReminder = null
                showAddDialog = true 
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp).padding(bottom = 80.dp),
            containerColor = ReminderBlue,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Outlined.Add, null)
        }

        if (showAddDialog) {
            AddReminderBottomSheet(
                editingReminder = editingReminder,
                onDismiss = { 
                    showAddDialog = false
                    editingReminder = null
                },
                onSave = { newReminder ->
                    if (editingReminder != null) {
                        LocalDataRepository.deleteReminder(context, editingReminder!!.id)
                        allReminders.removeIf { it.id == editingReminder!!.id }
                    }
                    LocalDataRepository.saveReminder(context, newReminder)
                    allReminders.add(newReminder)
                    showAddDialog = false
                    editingReminder = null
                }
            )
        }

        if (showDayDetail) {
            DayDetailBottomSheet(
                date = selectedDate,
                reminders = remindersForDay,
                onDismiss = { showDayDetail = false },
                onDelete = { id ->
                    LocalDataRepository.deleteReminder(context, id)
                    allReminders.removeIf { it.id == id }
                },
                onEdit = { 
                    editingReminder = it
                    showDayDetail = false
                    showAddDialog = true 
                }
            )
        }
    }
}

private enum class AddStep { TYPE, DETAILS, MAP, SCHEDULE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderBottomSheet(editingReminder: Reminder?, onDismiss: () -> Unit, onSave: (Reminder) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var step by remember { mutableStateOf(if (editingReminder != null) AddStep.DETAILS else AddStep.TYPE) }
    var selectedType by remember { mutableStateOf(editingReminder?.type ?: ReminderType.MEDICATION) }
    
    var title by remember { mutableStateOf(editingReminder?.title ?: "") }
    var dosage by remember { mutableStateOf(editingReminder?.dosage ?: "") }
    var doctor by remember { mutableStateOf(editingReminder?.doctor ?: "") }
    var location by remember { mutableStateOf(editingReminder?.location ?: "") }
    
    var isWeekly by remember { mutableStateOf(editingReminder?.isWeekly ?: false) }
    var repeatDays by remember { mutableStateOf(editingReminder?.repeatDays?.toSet() ?: setOf<Int>()) }
    var selectedDate by remember { mutableStateOf(if (editingReminder?.dateIso != null) LocalDate.parse(editingReminder.dateIso) else LocalDate.now()) }
    
    // Parse time if editing
    val initialTime = editingReminder?.time?.split(" ", ":")
    var selectedHour by remember { mutableIntStateOf(initialTime?.get(0)?.toIntOrNull() ?: 8) }
    var selectedMinute by remember { mutableIntStateOf(initialTime?.get(1)?.toIntOrNull() ?: 0) }
    var selectedPeriod by remember { mutableStateOf(initialTime?.get(2) ?: "AM") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = null
    ) {
        Box(modifier = Modifier.padding(top = 12.dp)) {
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "addStep"
            ) { currentStep ->
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 40.dp)) {
                    Spacer(modifier = Modifier.height(12.dp))
                    when (currentStep) {
                        AddStep.TYPE -> StepChooseType { type ->
                            selectedType = type
                            step = AddStep.DETAILS
                        }
                        AddStep.DETAILS -> StepDetails(
                            selectedType, title, { title = it }, dosage, { dosage = it },
                            doctor, { doctor = it }, location, { location = it },
                            onNext = { 
                                if (selectedType == ReminderType.MEDICAL_APPOINTMENT) step = AddStep.MAP 
                                else step = AddStep.SCHEDULE 
                            },
                            onBack = { if (editingReminder == null) step = AddStep.TYPE else onDismiss() }
                        )
                        AddStep.MAP -> StepMapPicker(
                            onLocationSelected = { loc ->
                                location = loc
                                step = AddStep.SCHEDULE
                            },
                            onBack = { step = AddStep.DETAILS }
                        )
                        AddStep.SCHEDULE -> StepSchedule(
                            selectedType, isWeekly, { isWeekly = it }, repeatDays, { repeatDays = it },
                            selectedDate, { selectedDate = it }, selectedHour, { selectedHour = it },
                            selectedMinute, { selectedMinute = it }, selectedPeriod, { selectedPeriod = it },
                            onBack = { 
                                if (selectedType == ReminderType.MEDICAL_APPOINTMENT) step = AddStep.MAP 
                                else step = AddStep.DETAILS 
                            },
                            onFinish = {
                                val timeStr = String.format("%02d:%02d %s", selectedHour, selectedMinute, selectedPeriod)
                                onSave(Reminder(
                                    editingReminder?.id ?: UUID.randomUUID().toString(), title, selectedType, selectedDate.toString(),
                                    timeStr, dosage.ifBlank { null }, doctor.ifBlank { null }, location.ifBlank { null },
                                    isWeekly = isWeekly, repeatDays = if (isWeekly) repeatDays.toList() else null
                                ))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepChooseType(onTypeSelected: (ReminderType) -> Unit) {
    Text("¿Qué deseas agregar?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(24.dp))
    TypeOptionCard("Medicamento", "Dosis y horarios de medicinas", Icons.Outlined.Medication, ReminderGreen) { onTypeSelected(ReminderType.MEDICATION) }
    Spacer(modifier = Modifier.height(16.dp))
    TypeOptionCard("Cita Médica", "Turnos con especialistas", Icons.Outlined.LocalHospital, ReminderBlue) { onTypeSelected(ReminderType.MEDICAL_APPOINTMENT) }
}

@Composable
private fun TypeOptionCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = color.copy(0.08f), border = BorderStroke(1.dp, color.copy(0.2f))) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(color.copy(0.15f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = RelaxMutedText)
            }
            Icon(Icons.AutoMirrored.Outlined.ArrowForwardIos, null, tint = color.copy(0.4f), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun StepDetails(type: ReminderType, title: String, onTitleChange: (String) -> Unit, dosage: String, onDosageChange: (String) -> Unit, doctor: String, onDoctorChange: (String) -> Unit, location: String, onLocationChange: (String) -> Unit, onNext: () -> Unit, onBack: () -> Unit) {
    val color = if (type == ReminderType.MEDICATION) ReminderGreen else ReminderBlue
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = RelaxMutedText) }
        Text(if (type == ReminderType.MEDICATION) "Detalles de Medicina" else "Datos de la Cita", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(24.dp))
    OutlinedTextField(title, onTitleChange, label = { Text(if (type == ReminderType.MEDICATION) "Nombre del medicamento" else "Motivo de la consulta") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
    Spacer(modifier = Modifier.height(16.dp))
    if (type == ReminderType.MEDICATION) {
        OutlinedTextField(dosage, onDosageChange, label = { Text("Dosis (ej: 1 tableta)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
    } else {
        OutlinedTextField(doctor, onDoctorChange, label = { Text("Nombre del Doctor") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(location, onLocationChange, label = { Text("Clínica / Ubicación") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), readOnly = true, placeholder = { Text("Se seleccionará en el mapa") })
    }
    Spacer(modifier = Modifier.height(32.dp))
    Button(onNext, enabled = title.isNotBlank(), modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = color)) { Text("Siguiente") }
}

@Composable
private fun StepMapPicker(onLocationSelected: (String) -> Unit, onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val userLocation = LatLng(-12.0912, -77.0425) 
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(userLocation, 16f) }

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = RelaxMutedText) }
            Text("Ubicación de la Cita", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar clínica o dirección...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Outlined.Search, null, tint = ReminderBlue) },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ReminderBlue)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(modifier = Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(24.dp)).border(1.dp, MaterialTheme.colorScheme.outline.copy(0.1f), RoundedCornerShape(24.dp))) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                Marker(state = MarkerState(position = userLocation), title = "Seleccionar esta ubicación")
            }
            
            // GPS Button overlay
            FloatingActionButton(
                onClick = { /* Animate to current location */ },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).size(48.dp),
                containerColor = Color.White,
                contentColor = ReminderBlue,
                shape = CircleShape
            ) { Icon(Icons.Outlined.MyLocation, null) }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = ReminderBlue)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Dirección Seleccionada", style = MaterialTheme.typography.labelSmall, color = RelaxMutedText)
                    Text("Calle Las Orquídeas 456, San Isidro", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onLocationSelected("Calle Las Orquídeas 456, San Isidro") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ReminderBlue)
        ) { Text("Confirmar Ubicación") }
    }
}

@Composable
private fun StepSchedule(type: ReminderType, isWeekly: Boolean, onWeeklyChange: (Boolean) -> Unit, repeatDays: Set<Int>, onRepeatDaysChange: (Set<Int>) -> Unit, selectedDate: LocalDate, onDateChange: (LocalDate) -> Unit, selectedHour: Int, onHourChange: (Int) -> Unit, selectedMinute: Int, onMinuteChange: (Int) -> Unit, selectedPeriod: String, onPeriodChange: (String) -> Unit, onBack: () -> Unit, onFinish: () -> Unit) {
    val color = if (type == ReminderType.MEDICATION) ReminderGreen else ReminderBlue
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = RelaxMutedText) }
        Text("Programar Horario", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)).padding(4.dp)) {
        FrequencyChip("Fecha única", !isWeekly, color) { onWeeklyChange(false) }
        FrequencyChip("Repetir semanal", isWeekly, color) { onWeeklyChange(true) }
    }
    Spacer(modifier = Modifier.height(20.dp))
    if (isWeekly) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("L", "M", "M", "J", "V", "S", "D").forEachIndexed { i, d ->
                val id = i + 1
                val isSelected = repeatDays.contains(id)
                DayChip(d, isSelected, color) {
                    val new = repeatDays.toMutableSet()
                    if (isSelected) new.remove(id) else new.add(id)
                    onRepeatDaysChange(new)
                }
            }
        }
    } else {
        WeekStrip(selectedDate, onDateChange)
    }
    Spacer(modifier = Modifier.height(24.dp))
    
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(12.dp), color = color.copy(0.05f), border = BorderStroke(1.dp, color.copy(0.1f))) {
            Text(
                text = String.format("%02d : %02d %s", selectedHour, selectedMinute, selectedPeriod),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = color,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Fixed Time Picker Row - Perfect alignment
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        WheelNumberPicker(1..12, selectedHour, onHourChange)
        Spacer(modifier = Modifier.width(16.dp))
        WheelNumberPicker(0..59, selectedMinute, onMinuteChange, "%02d")
        Spacer(modifier = Modifier.width(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            PeriodChip("AM", selectedPeriod == "AM", color) { onPeriodChange("AM") }
            PeriodChip("PM", selectedPeriod == "PM", color) { onPeriodChange("PM") }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
    Button(onFinish, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = color)) { Text("Guardar Recordatorio") }
}

@Composable
private fun RowScope.FrequencyChip(label: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.weight(1f).height(40.dp), shape = RoundedCornerShape(10.dp), color = if (selected) color else Color.Transparent, contentColor = if (selected) Color.White else RelaxMutedText) {
        Box(contentAlignment = Alignment.Center) { Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun DayChip(label: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.size(42.dp), shape = CircleShape, color = if (selected) color else MaterialTheme.colorScheme.surfaceVariant.copy(0.4f), contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface) {
        Box(contentAlignment = Alignment.Center) { Text(label, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp) }
    }
}

@Composable
private fun PeriodChip(label: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.size(54.dp, 38.dp), shape = RoundedCornerShape(10.dp), color = if (selected) color.copy(0.15f) else Color.Transparent, border = BorderStroke(1.dp, if (selected) color else MaterialTheme.colorScheme.outline.copy(0.2f)), contentColor = if (selected) color else RelaxMutedText) {
        Box(contentAlignment = Alignment.Center) { Text(label, fontWeight = FontWeight.Black, fontSize = 14.sp) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelNumberPicker(range: IntRange, current: Int, onValueChange: (Int) -> Unit, format: String = "%d") {
    val list = range.toList()
    val state = rememberLazyListState(initialFirstVisibleItemIndex = list.indexOf(current).coerceAtLeast(0))
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = state)
    
    LaunchedEffect(state.isScrollInProgress) {
        if (!state.isScrollInProgress) {
            val centerIndex = state.firstVisibleItemIndex
            if (centerIndex in list.indices) {
                onValueChange(list[centerIndex])
            }
        }
    }

    Box(modifier = Modifier.size(75.dp, 130.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))) {
        // Selection indicator - Centered exactly
        Box(modifier = Modifier.align(Alignment.Center).fillMaxWidth().height(42.dp).background(MaterialTheme.colorScheme.primary.copy(0.12f)))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 44.dp), // Adjusted to center better
            state = state,
            flingBehavior = snapFlingBehavior
        ) {
            items(list) { v ->
                val isSelected = v == current
                Box(modifier = Modifier.height(42.dp), contentAlignment = Alignment.Center) {
                    Text(
                        String.format(format, v), 
                        style = if (isSelected) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleMedium, 
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal, 
                        color = if (isSelected) MaterialTheme.colorScheme.primary else RelaxMutedText,
                        modifier = Modifier.clickable { onValueChange(v) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekStrip(selectedDate: LocalDate, onDaySelected: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        for (i in 0..6) {
            val date = today.plusDays(i.toLong())
            val isSelected = date == selectedDate
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDaySelected(date) }) {
                Text(getDayName(date.dayOfWeek.value).take(1), style = MaterialTheme.typography.labelSmall, color = if (isSelected) ReminderBlue else RelaxMutedText)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(shape = CircleShape, color = if (isSelected) ReminderBlue else Color.Transparent, modifier = Modifier.size(36.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text(date.dayOfMonth.toString(), color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface) }
                }
            }
        }
    }
}

@Composable
private fun MonthlyCalendarView(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit, reminders: List<Reminder>) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7
    
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = RelaxMutedText) }
            Text("${getMonthName(currentMonth.monthValue)} ${currentMonth.year}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) { Icon(Icons.AutoMirrored.Outlined.ArrowForwardIos, null, tint = RelaxMutedText, modifier = Modifier.size(16.dp)) }
        }
        
        val totalCells = ((daysInMonth + firstDayOfMonth + 6) / 7) * 7
        for (row in 0 until totalCells / 7) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (col in 0 until 7) {
                    val dayNum = row * 7 + col - firstDayOfMonth + 1
                    if (dayNum in 1..daysInMonth) {
                        val date = currentMonth.atDay(dayNum)
                        val isSelected = date == selectedDate
                        val dayReminders = reminders.filter { it.dateIso == date.toString() || (it.isWeekly && it.repeatDays?.contains(date.dayOfWeek.value) == true) }
                        
                        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(if (isSelected) ReminderBlue else Color.Transparent).clickable { onDateSelected(date) }, contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(dayNum.toString(), color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
                                if (dayReminders.isNotEmpty() && !isSelected) {
                                    Row(horizontalArrangement = Arrangement.Center) {
                                        if (dayReminders.any { it.type == ReminderType.MEDICATION }) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(ReminderGreen))
                                        if (dayReminders.any { it.type == ReminderType.MEDICAL_APPOINTMENT }) {
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(ReminderBlue))
                                        }
                                    }
                                }
                            }
                        }
                    } else Spacer(modifier = Modifier.size(44.dp))
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(reminder: Reminder, onDelete: (String) -> Unit, onEdit: (Reminder) -> Unit) {
    val isAppt = reminder.type == ReminderType.MEDICAL_APPOINTMENT
    val color = if (isAppt) ReminderBlue else ReminderGreen
    var showOptions by remember { mutableStateOf(false) }
    Surface(
        onClick = { showOptions = !showOptions }, 
        modifier = Modifier.fillMaxWidth(), 
        shape = RoundedCornerShape(20.dp), 
        color = MaterialTheme.colorScheme.surface, 
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, color.copy(0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(color.copy(0.1f)), contentAlignment = Alignment.Center) {
                    Icon(if (isAppt) Icons.Outlined.LocalHospital else Icons.Outlined.Medication, null, tint = color)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(reminder.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    if (reminder.dosage != null) Text(reminder.dosage!!, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                    if (reminder.location != null) Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Place, null, modifier = Modifier.size(10.dp), tint = RelaxMutedText)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(reminder.location!!, style = MaterialTheme.typography.bodySmall, color = RelaxMutedText)
                    }
                }
                Text(reminder.time, fontWeight = FontWeight.Black, color = color, fontSize = 16.sp)
            }
            if (showOptions) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onEdit(reminder) }) { 
                        Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar") 
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onDelete(reminder.id) }) { 
                        Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(16.dp), tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar", color = Color.Red) 
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayDetailBottomSheet(date: LocalDate, reminders: List<Reminder>, onDismiss: () -> Unit, onDelete: (String) -> Unit, onEdit: (Reminder) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val meds = reminders.filter { it.type == ReminderType.MEDICATION }
    val appts = reminders.filter { it.type == ReminderType.MEDICAL_APPOINTMENT }
    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = MaterialTheme.colorScheme.surface, dragHandle = { BottomSheetDefaults.DragHandle() }) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 60.dp).heightIn(min = 500.dp)) {
            Text("Pendientes - ${date.dayOfMonth} ${getMonthName(date.monthValue)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(24.dp))
            
            if (meds.isNotEmpty() && appts.isNotEmpty()) {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        TabChip("Medicinas (${meds.size})", selectedTab == 0, ReminderGreen) { selectedTab = 0 }
                        TabChip("Citas (${appts.size})", selectedTab == 1, ReminderBlue) { selectedTab = 1 }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            val currentList = if (meds.isNotEmpty() && appts.isNotEmpty()) (if (selectedTab == 0) meds else appts) else reminders
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 40.dp)) {
                items(currentList) { reminder ->
                    ReminderCard(reminder, onDelete = onDelete, onEdit = onEdit)
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabChip(label: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) color else Color.Transparent,
        contentColor = if (selected) Color.White else RelaxMutedText
    ) {
        Box(contentAlignment = Alignment.Center) { Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
    }
}

@Composable
private fun EmptyRemindersState() {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Outlined.CalendarMonth, null, modifier = Modifier.size(64.dp), tint = RelaxMutedText.copy(0.3f))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Día despejado", fontWeight = FontWeight.Bold, color = RelaxMutedText)
    }
}

private fun getMonthName(m: Int) = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")[m-1]
private fun getDayName(d: Int) = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")[d-1]
