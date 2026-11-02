package com.aquasense.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquasense.app.data.ApiException
import com.aquasense.app.data.AppPrefs
import com.aquasense.app.data.AppUser
import com.aquasense.app.data.AquaSenseApi
import com.aquasense.app.data.AuthRepository
import com.aquasense.app.data.DashboardSnapshot
import com.aquasense.app.data.DemoData
import com.aquasense.app.data.DispenseSession
import com.aquasense.app.data.Device
import com.aquasense.app.ui.theme.Amber
import com.aquasense.app.ui.theme.AmberSoft
import com.aquasense.app.ui.theme.Aqua
import com.aquasense.app.ui.theme.AquaSoft
import com.aquasense.app.ui.theme.Canvas
import com.aquasense.app.ui.theme.Card as CardColor
import com.aquasense.app.ui.theme.Coral
import com.aquasense.app.ui.theme.CoralSoft
import com.aquasense.app.ui.theme.Ink
import com.aquasense.app.ui.theme.InkMuted
import com.aquasense.app.ui.theme.Line
import com.aquasense.app.ui.theme.Teal
import com.aquasense.app.ui.theme.TealDeep
import kotlinx.coroutines.launch

@Composable
fun AquaSenseApp(prefs: AppPrefs) {
    var onboardingSeen by remember { mutableStateOf(prefs.hasSeenOnboarding()) }
    var user by remember { mutableStateOf(prefs.currentUser()) }

    when {
        !onboardingSeen -> OnboardingScreen {
            prefs.markOnboardingSeen()
            onboardingSeen = true
        }
        user == null -> AuthScreen(
            onDemo = {
                val demoUser = AppUser("Aayush", "demo-user", demoMode = true)
                prefs.saveUser(demoUser)
                user = demoUser
            },
            onAuthenticated = { authenticatedUser, token ->
                prefs.saveUser(authenticatedUser, token)
                user = authenticatedUser
            },
        )
        else -> HomeShell(
            prefs = prefs,
            user = user!!,
            onSignOut = {
                prefs.clearUser()
                user = null
            },
        )
    }
}

private data class IntroPage(
    val eyebrow: String,
    val title: String,
    val body: String,
    val icon: ImageVector,
    val tint: Color,
)

@Composable
private fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = remember {
        listOf(
            IntroPage("MEASURED WATER", "Know every glass.", "AquaSense makes water quality visible with live TDS, pH and tank health in one calm dashboard.", Icons.Default.Science, Aqua),
            IntroPage("PERSONAL HYDRATION", "Hydration, without the logging.", "On-device identity connects each dispense to a household member, so intake stays automatic and private.", Icons.Default.LocalDrink, Amber),
            IntroPage("SAFE REMOTE CARE", "Care for it from anywhere.", "Flush or drain remotely when your purifier is online. Safety interlocks stay at the edge, where they belong.", Icons.Default.Shield, Teal),
        )
    }
    var page by rememberSaveable { mutableIntStateOf(0) }
    val current = pages[page]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            BrandMark(compact = true)
            TextButton(onClick = onFinish) { Text("Skip", color = InkMuted) }
        }
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(164.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(current.tint.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(modifier = Modifier.size(112.dp).clip(CircleShape).background(current.tint.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) {
                Icon(current.icon, null, tint = current.tint, modifier = Modifier.size(54.dp))
            }
        }
        Spacer(Modifier.height(44.dp))
        Text(current.eyebrow, style = MaterialTheme.typography.labelLarge, color = current.tint, letterSpacing = 1.5.sp)
        Spacer(Modifier.height(12.dp))
        Text(current.title, style = MaterialTheme.typography.headlineLarge, color = Ink)
        Spacer(Modifier.height(14.dp))
        Text(current.body, style = MaterialTheme.typography.bodyLarge, color = InkMuted)
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pages.forEachIndexed { index, _ ->
                Box(modifier = Modifier.size(if (index == page) 28.dp else 8.dp, 8.dp).clip(CircleShape).background(if (index == page) current.tint else Line))
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { if (page == pages.lastIndex) onFinish() else page++ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealDeep),
        ) {
            Text(if (page == pages.lastIndex) "Explore AquaSense" else "Continue")
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun AuthScreen(
    onDemo: () -> Unit,
    onAuthenticated: (AppUser, String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val auth = remember { AuthRepository() }
    var createAccount by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Canvas).verticalScroll(rememberScrollState()).padding(24.dp),
    ) {
        Spacer(Modifier.height(28.dp))
        BrandMark()
        Spacer(Modifier.height(54.dp))
        Text(if (createAccount) "Create your water profile" else "Welcome back", style = MaterialTheme.typography.headlineLarge, color = Ink)
        Spacer(Modifier.height(10.dp))
        Text(if (createAccount) "Set up your household and start with a clearer glass." else "Your purifier is ready when you are.", color = InkMuted, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(28.dp))
        if (createAccount) {
            AuthField("Your name", name, { name = it }, "Aayush")
            Spacer(Modifier.height(12.dp))
        }
        AuthField("Email address", email, { email = it }, "you@example.com", KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        AuthField("Password", password, { password = it }, "At least 6 characters", KeyboardType.Password, true)
        if (error != null) {
            Spacer(Modifier.height(12.dp))
            Text(error.orEmpty(), color = Coral, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = {
                error = null
                if (email.isBlank() || password.length < 6 || (createAccount && name.isBlank())) {
                    error = "Please complete the fields to continue."
                } else {
                    busy = true
                    scope.launch {
                        try {
                            val result = if (createAccount) auth.signUp(email.trim(), password, name.trim()) else auth.signIn(email.trim(), password)
                            onAuthenticated(AppUser(result.displayName, result.userId, demoMode = false), result.accessToken)
                        } catch (apiError: ApiException) {
                            error = apiError.message
                        } finally {
                            busy = false
                        }
                    }
                }
            },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealDeep),
        ) {
            if (busy) CircularProgressIndicator(color = CardColor, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            else Text(if (createAccount) "Create account" else "Sign in")
        }
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Line)
            Text("  or  ", color = InkMuted, style = MaterialTheme.typography.labelMedium)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Line)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onDemo, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp)) {
            Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp), tint = Amber)
            Spacer(Modifier.width(8.dp))
            Text("Explore the demo mode")
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(if (createAccount) "Already have an account?" else "New to AquaSense?", color = InkMuted)
            TextButton(onClick = { createAccount = !createAccount; error = null }) {
                Text(if (createAccount) "Sign in" else "Create one", color = Teal)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, null, tint = InkMuted, modifier = Modifier.size(13.dp))
            Spacer(Modifier.width(5.dp))
            Text("Your identity stays on the purifier edge.", color = InkMuted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun AuthField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, keyboardType: KeyboardType = KeyboardType.Text, password: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
    )
}

@Composable
private fun BrandMark(compact: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(if (compact) 34.dp else 42.dp).clip(RoundedCornerShape(12.dp)).background(TealDeep), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.WaterDrop, null, tint = Aqua, modifier = Modifier.size(if (compact) 20.dp else 24.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text("AquaSense", color = TealDeep, fontWeight = FontWeight.Bold, fontSize = if (compact) 18.sp else 22.sp)
            if (!compact) Text("water, made personal", color = InkMuted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeShell(
    prefs: AppPrefs,
    user: AppUser,
    onSignOut: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbars = remember { SnackbarHostState() }
    val api = remember(user.userId) { AquaSenseApi(accessToken = prefs.token()) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var snapshot by remember(user.userId) { mutableStateOf(if (user.demoMode) DemoData.dashboard() else null) }
    var sessions by remember(user.userId) { mutableStateOf(if (user.demoMode) DemoData.sessions() else emptyList()) }
    var owner by remember(user.userId) { mutableStateOf(user.demoMode) }
    var loading by remember { mutableStateOf(!user.demoMode) }
    var refreshing by remember { mutableStateOf(false) }
    var actionBusy by remember { mutableStateOf(false) }
    var showClaimDialog by remember { mutableStateOf(false) }

    suspend fun refreshData() {
        if (user.demoMode) {
            snapshot = DemoData.dashboard()
            sessions = DemoData.sessions()
            loading = false
            return
        }
        try {
            val device = api.listDevices().firstOrNull()
            if (device == null) {
                snapshot = null
                sessions = emptyList()
                owner = false
            } else {
                owner = device.role == "owner"
                snapshot = api.dashboard(device.id)
                sessions = api.guestSessions(device.id)
            }
        } catch (error: ApiException) {
            snackbars.showSnackbar(error.message ?: "Could not refresh dashboard")
        } finally {
            loading = false
        }
    }

    LaunchedEffect(user.userId) { refreshData() }

    fun refresh() {
        scope.launch {
            refreshing = true
            refreshData()
            refreshing = false
        }
    }

    fun sendRemoteCommand(command: String) {
        if (snapshot == null) return
        scope.launch {
            actionBusy = true
            try {
                if (user.demoMode) {
                    snackbars.showSnackbar("Demo ACK received — $command is safe to run")
                } else {
                    val result = api.sendCommand(snapshot!!.deviceId, command)
                    snackbars.showSnackbar("${command.replace('_', ' ').replaceFirstChar { it.uppercase() }} ${result.status}")
                    refreshData()
                }
            } catch (error: ApiException) {
                snackbars.showSnackbar(error.message ?: "Command was not delivered")
            } finally {
                actionBusy = false
            }
        }
    }

    Scaffold(
        containerColor = Canvas,
        snackbarHost = { SnackbarHost(snackbars) },
        bottomBar = {
            NavigationBar(containerColor = CardColor, modifier = Modifier.navigationBarsPadding()) {
                val tabs = listOf("Overview" to Icons.Default.Dashboard, "Insights" to Icons.Default.Insights, "Settings" to Icons.Default.Settings)
                tabs.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        alwaysShowLabel = true,
                    )
                }
            }
        },
    ) { padding ->
        when (selectedTab) {
            0 -> DashboardScreen(
                modifier = Modifier.padding(padding),
                user = user,
                snapshot = snapshot,
                sessions = sessions,
                owner = owner,
                loading = loading,
                refreshing = refreshing,
                actionBusy = actionBusy,
                onRefresh = ::refresh,
                onRemoteCommand = ::sendRemoteCommand,
                onClaim = { showClaimDialog = true },
            )
            1 -> InsightsScreen(modifier = Modifier.padding(padding), snapshot = snapshot, sessions = sessions)
            else -> SettingsScreen(
                modifier = Modifier.padding(padding),
                user = user,
                snapshot = snapshot,
                onClaim = { showClaimDialog = true },
                onSignOut = onSignOut,
            )
        }
    }

    if (showClaimDialog) {
        ClaimDeviceDialog(
            onDismiss = { showClaimDialog = false },
            onClaimed = { deviceId ->
                showClaimDialog = false
                scope.launch {
                    try {
                        api.claimDevice(deviceId)
                        snackbars.showSnackbar("Purifier claimed. Welcome home.")
                        refreshData()
                    } catch (error: ApiException) {
                        snackbars.showSnackbar(error.message ?: "Could not claim this purifier")
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(title: String, subtitle: String? = null, onRefresh: (() -> Unit)? = null, refreshing: Boolean = false) {
    TopAppBar(
        title = {
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge, color = Ink)
                if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.labelSmall, color = InkMuted)
            }
        },
        actions = {
            if (onRefresh != null) {
                IconButton(onClick = onRefresh, enabled = !refreshing) {
                    if (refreshing) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Teal)
                    else Icon(Icons.Default.Refresh, "Refresh", tint = Teal)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Canvas),
    )
}

@Composable
private fun EmptyDeviceState(onClaim: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(88.dp).clip(CircleShape).background(AquaSoft), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.QrCodeScanner, null, tint = Teal, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("Connect your purifier", style = MaterialTheme.typography.titleLarge, color = Ink)
        Spacer(Modifier.height(8.dp))
        Text("Scan or paste the device ID from your AquaSense hardware to see live water health here.", color = InkMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onClaim, shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = TealDeep)) { Text("Connect purifier") }
    }
}

@Composable
private fun DashboardScreen(
    modifier: Modifier,
    user: AppUser,
    snapshot: DashboardSnapshot?,
    sessions: List<DispenseSession>,
    owner: Boolean,
    loading: Boolean,
    refreshing: Boolean,
    actionBusy: Boolean,
    onRefresh: () -> Unit,
    onRemoteCommand: (String) -> Unit,
    onClaim: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        AppTopBar(
            title = "Good morning, ${user.displayName.substringBefore(' ').ifBlank { "there" }}",
            subtitle = "Here is your water, at a glance",
            onRefresh = onRefresh,
            refreshing = refreshing,
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(40.dp), color = Teal)
        } else if (snapshot == null) {
            Card(modifier = Modifier.padding(16.dp), colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(24.dp)) {
                EmptyDeviceState(onClaim = onClaim)
            }
        } else {
            StatusHeader(snapshot)
            HydrationCard(snapshot)
            SectionTitle("Live purifier health", "Updated just now")
            HealthGrid(snapshot)
            SectionTitle("Your household", "Automatic dispense activity")
            HouseholdCard(sessions = sessions, sessionCount = snapshot.sessionCountToday)
            SectionTitle("Care, when you need it", "Edge-safe remote controls")
            CareCard(
                snapshot = snapshot,
                owner = owner,
                busy = actionBusy,
                onCommand = onRemoteCommand,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatusHeader(snapshot: DashboardSnapshot) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(if (snapshot.status == "offline") Coral else Aqua))
        Spacer(Modifier.width(8.dp))
        Text(if (snapshot.status == "offline") "Purifier offline" else "Purifier online", color = if (snapshot.status == "offline") Coral else Teal, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(8.dp))
        Text("•", color = InkMuted)
        Spacer(Modifier.width(8.dp))
        Text(snapshot.name, color = InkMuted)
    }
}

@Composable
private fun HydrationCard(snapshot: DashboardSnapshot) {
    val goal = 3000
    val progress = hydrationProgress(snapshot.waterConsumedTodayMl, goal)
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = TealDeep),
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("TODAY'S HYDRATION", color = Aqua, style = MaterialTheme.typography.labelLarge, letterSpacing = 1.2.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${snapshot.waterConsumedTodayMl / 1000.0}", color = CardColor, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(7.dp))
                        Text("L / 3.0 L", color = CardColor.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 7.dp), fontWeight = FontWeight.SemiBold)
                    }
                }
                Box(modifier = Modifier.size(58.dp).clip(CircleShape).background(Aqua.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocalDrink, null, tint = Aqua, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().height(9.dp).clip(CircleShape).background(CardColor.copy(alpha = 0.16f))) {
                Box(modifier = Modifier.fillMaxWidth(progress).height(9.dp).clip(CircleShape).background(Aqua))
            }
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${(progress * 100).toInt()}% of your daily goal", color = CardColor.copy(alpha = 0.72f), style = MaterialTheme.typography.labelMedium)
                Text(if (progress >= 1f) "Goal reached" else "${goal - snapshot.waterConsumedTodayMl} ml to go", color = Amber, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, action: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = Ink)
        Text(action, style = MaterialTheme.typography.labelMedium, color = InkMuted)
    }
}

@Composable
private fun HealthGrid(snapshot: DashboardSnapshot) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        HealthCard(
            modifier = Modifier.weight(1f),
            label = "TDS",
            value = snapshot.currentTds?.let { "$it" } ?: "—",
            unit = "ppm",
            detail = tdsQuality(snapshot.currentTds),
            icon = Icons.Default.Science,
            tint = Aqua,
            background = AquaSoft,
        )
        HealthCard(
            modifier = Modifier.weight(1f),
            label = "FILTER",
            value = snapshot.filterLifeLiters?.let { "${it.toInt()}" } ?: "—",
            unit = "L left",
            detail = "${((snapshot.filterLifeLiters ?: 0.0) / 10).coerceIn(0.0, 100.0).toInt()}% life",
            icon = Icons.Default.FilterAlt,
            tint = Amber,
            background = AmberSoft,
        )
        HealthCard(
            modifier = Modifier.weight(1f),
            label = "TANK",
            value = snapshot.tankLevelMl?.let { "${it / 1000.0}" } ?: "—",
            unit = "L",
            detail = "Ready to pour",
            icon = Icons.Default.WaterDrop,
            tint = Teal,
            background = AquaSoft,
        )
    }
}

@Composable
private fun HealthCard(modifier: Modifier, label: String, value: String, unit: String, detail: String, icon: ImageVector, tint: Color, background: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardColor)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(background), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(17.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(label, color = InkMuted, style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = Ink, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.width(3.dp))
                Text(unit, color = InkMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 2.dp))
            }
            Spacer(Modifier.height(4.dp))
            Text(detail, color = tint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
private fun HouseholdCard(sessions: List<DispenseSession>, sessionCount: Int) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = CardColor)) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar("Y", Teal)
                    Spacer(Modifier.width(2.dp))
                    Avatar("K", Aqua)
                    Spacer(Modifier.width(2.dp))
                    Avatar("G", Amber)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("${sessionCount.coerceAtLeast(sessions.size)} dispenses", fontWeight = FontWeight.Bold, color = Ink)
                        Text("Tracked automatically today", color = InkMuted, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "View activity", tint = InkMuted, modifier = Modifier.size(18.dp))
            }
            if (sessions.isNotEmpty()) {
                HorizontalDivider(color = Line)
                sessions.take(3).forEach { session ->
                    SessionRow(session)
                }
            }
        }
    }
}

@Composable
private fun Avatar(letter: String, tint: Color) {
    Box(modifier = Modifier.size(30.dp).clip(CircleShape).border(2.dp, CardColor, CircleShape).background(tint.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
        Text(letter, color = tint, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SessionRow(session: DispenseSession) {
    val person = when (session.userId) {
        "you" -> "You"
        "krish" -> "Krish"
        null -> "Guest"
        else -> "Household member"
    }
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(11.dp)).background(if (person == "Guest") AmberSoft else AquaSoft), contentAlignment = Alignment.Center) {
            Icon(if (person == "Guest") Icons.Default.Person else Icons.Default.LocalDrink, null, tint = if (person == "Guest") Amber else Teal, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(11.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(person, color = Ink, fontWeight = FontWeight.SemiBold)
            Text(session.startTs, color = InkMuted, style = MaterialTheme.typography.labelSmall)
        }
        Text("${session.volumeMl} ml", color = Teal, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CareCard(snapshot: DashboardSnapshot, owner: Boolean, busy: Boolean, onCommand: (String) -> Unit) {
    var pendingCommand by remember { mutableStateOf<String?>(null) }
    val filterPercent = filterLifePercent(snapshot.filterLifeLiters)
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = CardColor)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(AmberSoft), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CleaningServices, null, tint = Amber, modifier = Modifier.size(21.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Filter health", color = Ink, fontWeight = FontWeight.Bold)
                    Text("${(filterPercent * 100).toInt()}% of estimated life remaining", color = InkMuted, style = MaterialTheme.typography.labelSmall)
                }
                if (filterPercent < 0.15f) Icon(Icons.Default.NotificationsActive, "Filter alert", tint = Coral)
            }
            Spacer(Modifier.height(15.dp))
            Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(AmberSoft)) {
                Box(modifier = Modifier.fillMaxWidth(filterPercent).height(8.dp).clip(CircleShape).background(if (filterPercent < 0.15f) Coral else Amber))
            }
            Spacer(Modifier.height(14.dp))
            if (owner) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(onClick = { pendingCommand = "flush" }, enabled = !busy, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.filledTonalButtonColors(containerColor = AquaSoft, contentColor = Teal)) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Flush filter")
                    }
                    OutlinedButton(onClick = { pendingCommand = "drain" }, enabled = !busy, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.Default.Opacity, null, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Drain tank")
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, null, tint = InkMuted, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(7.dp))
                    Text("Remote care is available to the purifier owner.", color = InkMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
    if (pendingCommand != null) {
        val command = pendingCommand!!
        AlertDialog(
            onDismissRequest = { pendingCommand = null },
            title = { Text(if (command == "flush") "Flush the filter?" else "Drain the tank?") },
            text = { Text(if (command == "flush") "The purifier will run a short maintenance flush. It will not start while dispensing." else "This sends a remote drain request. The edge controller will block it if dispensing is active.") },
            confirmButton = {
                Button(onClick = { pendingCommand = null; onCommand(command) }, colors = ButtonDefaults.buttonColors(containerColor = TealDeep)) { Text("Confirm") }
            },
            dismissButton = { TextButton(onClick = { pendingCommand = null }) { Text("Not now") } },
        )
    }
}

@Composable
private fun InsightsScreen(modifier: Modifier, snapshot: DashboardSnapshot?, sessions: List<DispenseSession>) {
    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Insights", subtitle = "Small signals, better habits")
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = TealDeep)) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text("THIS WEEK", color = Aqua, style = MaterialTheme.typography.labelLarge, letterSpacing = 1.3.sp)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("${((snapshot?.waterConsumedWeekMl ?: 14800) / 1000.0)} L", color = CardColor, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(9.dp))
                    Text("total intake", color = CardColor.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 6.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text("You are building a steady rhythm. Keep a glass close during your afternoon dip.", color = CardColor.copy(alpha = 0.75f), style = MaterialTheme.typography.bodyMedium)
            }
        }
        SectionTitle("Daily rhythm", "Last 7 days")
        WeeklyChart()
        SectionTitle("Quality signals", "Live from your purifier")
        QualityInsights(snapshot)
        SectionTitle("Recent moments", "${sessions.size} tracked")
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = CardColor)) {
            Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                sessions.take(3).forEachIndexed { index, session ->
                    SessionRow(session)
                    if (index < sessions.take(3).lastIndex) HorizontalDivider(color = Line)
                }
                if (sessions.isEmpty()) Text("Your first dispense will appear here.", color = InkMuted, modifier = Modifier.padding(vertical = 20.dp))
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun WeeklyChart() {
    val values = listOf(0.48f, 0.62f, 0.7f, 0.42f, 0.85f, 0.68f, 0.8f)
    val labels = listOf("M", "T", "W", "T", "F", "S", "S")
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = CardColor)) {
        Row(modifier = Modifier.fillMaxWidth().height(170.dp).padding(horizontal = 14.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Bottom) {
            values.forEachIndexed { index, value ->
                Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (index == 4) "3.1L" else "", style = MaterialTheme.typography.labelSmall, color = if (index == 4) Teal else Color.Transparent)
                    Spacer(Modifier.height(5.dp))
                    Box(modifier = Modifier.fillMaxWidth(0.68f).height((value * 104).dp).clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)).background(if (index == 4) Teal else AquaSoft))
                    Spacer(Modifier.height(8.dp))
                    Text(labels[index], color = InkMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun QualityInsights(snapshot: DashboardSnapshot?) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        QualityCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Science,
            title = "TDS",
            value = "${snapshot?.currentTds ?: 86} ppm",
            detail = "Within target",
            tint = Aqua,
            background = AquaSoft,
        )
        QualityCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.WaterDrop,
            title = "pH",
            value = snapshot?.ph?.let { "%.1f".format(it) } ?: "—",
            detail = if (snapshot?.ph != null) "Balanced" else "Sensor pending",
            tint = Teal,
            background = AquaSoft,
        )
        QualityCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Thermostat,
            title = "Temperature",
            value = "23.4 °C",
            detail = "Stable",
            tint = Amber,
            background = AmberSoft,
        )
    }
}

@Composable
private fun QualityCard(modifier: Modifier, icon: ImageVector, title: String, value: String, detail: String, tint: Color, background: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(background), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(13.dp))
            Text(title, color = InkMuted, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Text(value, color = Ink, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(tint))
                Spacer(Modifier.width(5.dp))
                Text(detail, color = tint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    modifier: Modifier,
    user: AppUser,
    snapshot: DashboardSnapshot?,
    onClaim: () -> Unit,
    onSignOut: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Settings", subtitle = "Your home, your rules")
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = CardColor)) {
            Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(TealDeep), contentAlignment = Alignment.Center) {
                    Text(user.displayName.take(1).uppercase(), color = CardColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.width(13.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.displayName, color = Ink, fontWeight = FontWeight.Bold)
                    Text(if (user.demoMode) "Demo profile" else "Connected account", color = InkMuted, style = MaterialTheme.typography.labelSmall)
                }
                Icon(Icons.Default.CheckCircle, null, tint = Aqua, modifier = Modifier.size(20.dp))
            }
        }
        SettingsSectionLabel("PURIFIER")
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = CardColor)) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(AquaSoft), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.WaterDrop, null, tint = Teal, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(snapshot?.name ?: "No purifier connected", color = Ink, fontWeight = FontWeight.Bold)
                        Text(snapshot?.firmwareVersion ?: "Ready for your device", color = InkMuted, style = MaterialTheme.typography.labelSmall)
                    }
                    if (snapshot != null) StatusPill(snapshot.status)
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Line)
                Spacer(Modifier.height(14.dp))
                if (user.demoMode) {
                    Text("You are viewing the competition-ready demo dashboard.", color = InkMuted, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(12.dp))
                }
                OutlinedButton(onClick = onClaim, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (snapshot == null) "Connect a purifier" else "Add another purifier")
                }
            }
        }
        SettingsSectionLabel("PRIVACY & RELIABILITY")
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = CardColor)) {
            SettingRow(Icons.Default.Lock, "Private by design", "Face embeddings stay on the purifier edge", Aqua)
            SettingRow(Icons.Default.CloudOff, "Works through outages", "Buffered events sync when the network returns", Amber)
            SettingRow(Icons.Default.Wifi, "Secure connection", "REST API with your Supabase session", Teal)
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onSignOut, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(17.dp), tint = Coral)
            Spacer(Modifier.width(7.dp))
            Text("Sign out", color = Coral)
        }
        Spacer(Modifier.height(22.dp))
        Text("AquaSense • ATOMQUEST 2026", modifier = Modifier.align(Alignment.CenterHorizontally), color = InkMuted, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsSectionLabel(text: String) {
    Text(text, modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 8.dp), color = InkMuted, style = MaterialTheme.typography.labelSmall, letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, detail: String, tint: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(35.dp).clip(RoundedCornerShape(11.dp)).background(tint.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, color = Ink, fontWeight = FontWeight.SemiBold)
            Text(detail, color = InkMuted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    val online = status != "offline"
    Surface(shape = RoundedCornerShape(50), color = if (online) AquaSoft else CoralSoft) {
        Row(modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (online) Aqua else Coral))
            Spacer(Modifier.width(5.dp))
            Text(if (online) "Online" else "Offline", color = if (online) Teal else Coral, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ClaimDeviceDialog(onDismiss: () -> Unit, onClaimed: (String) -> Unit) {
    var deviceId by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Connect a purifier") },
        text = {
            Column {
                Text("Paste the UUID printed on your device or in the backend seed data.", color = InkMuted)
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = deviceId,
                    onValueChange = { deviceId = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Device UUID") },
                    placeholder = { Text("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx") },
                    shape = RoundedCornerShape(14.dp),
                )
            }
        },
        confirmButton = {
            Button(onClick = { onClaimed(deviceId.trim()) }, enabled = deviceId.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = TealDeep)) { Text("Connect") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
