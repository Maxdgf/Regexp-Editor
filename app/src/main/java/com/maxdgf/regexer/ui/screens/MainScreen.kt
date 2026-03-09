package com.maxdgf.regexer.ui.screens

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toString
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maxdgf.regexer.FILES_TYPE
import kotlinx.coroutines.delay

import com.maxdgf.regexer.OPEN_SOURCE_PROJECT_DESCRIPTION
import com.maxdgf.regexer.R
import com.maxdgf.regexer.REGEXER_APP_GITHUB_REPO_LINK
import com.maxdgf.regexer.REGEXER_APP_INFO
import com.maxdgf.regexer.TEXT_LIMIT
import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.entities.RegexpPatternEntity
import com.maxdgf.regexer.core.regex.RegexpSyntaxAnnotatedStringBuilder
import com.maxdgf.regexer.core.system_utils.AppManager
import com.maxdgf.regexer.core.system_utils.ClipBoardManager
import com.maxdgf.regexer.core.system_utils.FileManager
import com.maxdgf.regexer.core.system_utils.Toaster
import com.maxdgf.regexer.core.system_utils.UrlOpener
import com.maxdgf.regexer.ui.components.AlertUiDialog
import com.maxdgf.regexer.ui.components.BottomUiSheet
import com.maxdgf.regexer.ui.components.ColorPickerSheet
import com.maxdgf.regexer.ui.components.NoDataUiDescriptionBlock
import com.maxdgf.regexer.ui.components.RegexUiMatch
import com.maxdgf.regexer.ui.components.RegexerUiDialogTitle
import com.maxdgf.regexer.ui.components.SavedRegexpPatternUiItem
import com.maxdgf.regexer.ui.components.SimpleUiDialogTitle
import com.maxdgf.regexer.ui.data_management.view_models.AppDataStoreViewModel
import com.maxdgf.regexer.ui.data_management.view_models.SavedRegexpPatternsState
import com.maxdgf.regexer.ui.data_management.view_models.AppState
import com.maxdgf.regexer.ui.utils.CurrentThemeColor
import com.maxdgf.regexer.ui.utils.RegexFieldVisualTransformation
import com.maxdgf.regexer.ui.utils.TextFieldVisualTransformation
import kotlinx.coroutines.launch

/**Creates a main app screen.*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    appState: AppState = viewModel(),
    savedRegexpPatternsState: SavedRegexpPatternsState = hiltViewModel(),
    appDataStoreViewModel: AppDataStoreViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed) // modal drawer sheet state
    val drawerStateScope = rememberCoroutineScope()

    val haptic = LocalHapticFeedback.current // get haptic
    val activity = LocalActivity.current // get activity
    val context = LocalContext.current // get context
    val configuration = LocalConfiguration.current // get screen configuration

    // classes init
    val currentThemeColor = remember { CurrentThemeColor() }
    val appManager = remember { AppManager(activity, context) }
    val clipBoardManager = remember { ClipBoardManager(context) }
    val regexpSyntaxAnnotatedStringBuilder = remember { RegexpSyntaxAnnotatedStringBuilder() }
    val urlOpener = remember { UrlOpener(context) }
    val toaster = remember { Toaster(context) }
    val fileManager = remember { FileManager(context) }

    // activity result launchers
    val pickTextFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val fileUri = result.data?.data

        fileUri?.let { uri ->
            val openedFileContent = fileManager.openTextFile(uri) // open and read text file

            appState.updateTextInputFieldState(openedFileContent)
            toaster.showToast("Text file opened!")
        }
    }

    // viewmodel state value observers
    val regexFlagsList by appState.regexFlagsList.collectAsState()
    val isGlobalSearchState by appState.isRegexGlobalSearch.collectAsState()
    val isLiteralFlagEnabledState by appState.isLiteralFlagEnabled.collectAsState()
    val savedRegexpPatternsList by savedRegexpPatternsState.savedRegexpPatternsList.collectAsState()
    val currentSelectionColor by appDataStoreViewModel.currentSelectionColor.collectAsState()

    // asynchronous setting the color in the datastore parameter when the state of the match highlight color changes
    LaunchedEffect(appState.regexSelectionMatchesColor) {
        appState.regexSelectionMatchesColor?.let { color ->
            appDataStoreViewModel.saveCurrentSelectionColor(color.value.toLong())
        }
        delay(10) // delay 10 ms
    }

    // modal drawer with saved regexp patterns list
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RegexerUiDialogTitle(
                        titleText = "Saved regexp patterns",
                        spawnDismissDialogButton = true,
                        dismissButtonPainter = painterResource(R.drawable.baseline_arrow_back_24),
                        dismissDialogButtonFunction = { drawerStateScope.launch { drawerState.close() } }
                    )

                    if (savedRegexpPatternsList.isNotEmpty())
                        // saved regexp patterns list
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            itemsIndexed(
                                items = savedRegexpPatternsList,
                                key = { index, regexp -> regexp.uuid }
                            ) { index, regexp ->
                                SavedRegexpPatternUiItem(
                                    regexp = regexpSyntaxAnnotatedStringBuilder.setRegexpSyntaxStyleOnRegexStringPattern(
                                        buildAnnotatedString { append(regexp.regexpString) }, // setting style
                                    ),
                                    regexpName = regexp.name,
                                    deleteButtonFunction = {
                                        savedRegexpPatternsState.deleteRegexpByUuid(regexp.uuid) // delete
                                        drawerStateScope.launch { drawerState.close() } // close modal drawer sheet
                                    },
                                    itemClickFunction = {
                                        appState.apply {
                                            updateRegexInputFieldState(regexp.regexpString)
                                            updateIsRegexGlobalSearch(regexp.isGlobalSearchState)
                                            setSelectedFlags(regexp.flags)
                                        }

                                        toaster.showToast(regexp.name)
                                        drawerStateScope.launch { drawerState.close() } // close modal drawer sheet
                                    },
                                    flagsString = regexp.flags
                                )

                                if (index < savedRegexpPatternsList.lastIndex) HorizontalDivider() // divider
                            }
                        }
                    else
                        // show no-data description block
                        NoDataUiDescriptionBlock(
                            description = "No saved regexp patterns :(",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )

                    Button(
                        onClick = {
                            if (savedRegexpPatternsList.isNotEmpty()) savedRegexpPatternsState.deleteAllRegexpPatterns()
                            else {
                                drawerStateScope.launch { drawerState.close() } // close modal drawer sheet
                                toaster.showToast("nothing to delete!")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) { Text(text = "delete all") }
                }
            }
        }
    ) {
        Scaffold(
            topBar = { // top app bar
                TopAppBar(
                    navigationIcon = {
                        Image(
                            bitmap = ImageBitmap.imageResource(R.drawable.regexer_logo_mini),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(25.dp)
                        )
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic
                                appState.updateBottomCheatSheetState(true)
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_book_24),
                                contentDescription = null
                            )
                        }

                        IconButton(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic
                            appState.updateDropdownMenuState(true)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.outline_menu_24),
                                contentDescription = null
                            )
                        }

                        Box {
                            DropdownMenu(
                                expanded = appState.dropdownMenuState,
                                onDismissRequest = { appState.updateDropdownMenuState(false) }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        appState.updateAboutAppInfoSheetState(true)
                                        appState.updateDropdownMenuState(false)
                                    },
                                    text = {
                                        Row {
                                            Icon(
                                                painter = painterResource(R.drawable.outline_info_24),
                                                contentDescription = null,
                                                modifier = Modifier.align(Alignment.CenterVertically)
                                            )

                                            Text(
                                                text = "About app",
                                                modifier = Modifier.padding(start = 10.dp)
                                            )
                                        }
                                    }
                                )

                                DropdownMenuItem(
                                    onClick = {
                                        drawerStateScope.launch { drawerState.open() } // open modal sheet drawer
                                        appState.updateDropdownMenuState(false)
                                    },
                                    text = {
                                        Row {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_save_24),
                                                contentDescription = null,
                                                modifier = Modifier.align(Alignment.CenterVertically)
                                            )

                                            Text(
                                                text = "Saved regexps",
                                                modifier = Modifier.padding(start = 10.dp)
                                            )
                                        }
                                    }
                                )

                                HorizontalDivider()

                                DropdownMenuItem(
                                    onClick = { appManager.breakApp() },
                                    text = {
                                        Row {
                                            Icon(
                                                painter = painterResource(R.drawable.outline_exit_to_app_24),
                                                contentDescription = null,
                                                modifier = Modifier.align(Alignment.CenterVertically)
                                            )

                                            Text(
                                                text = "Exit",
                                                modifier = Modifier.padding(start = 10.dp)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            // Regex exception view alert dialog
            AlertUiDialog(
                state = appState.regexExceptionView,
                onDismissRequestFunction = { appState.updateRegexExceptionViewState(false) }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    SimpleUiDialogTitle(
                        titleTextContent = "Error view",
                        titleTextColor = Color.Red,
                        titleIconPainter = painterResource(R.drawable.outline_error_24),
                        titleIconTint = Color.Red,
                        onDismissRequestFunction = { appState.updateRegexExceptionViewState(false) }
                    )

                    Box(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = Color.Red,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .height(200.dp)
                    ) {
                        val verticalScroll = rememberScrollState()
                        val horizontalScroll = rememberScrollState()

                        Text(
                            text = appState.regexExceptionMessage ?: "",
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                                .verticalScroll(verticalScroll)
                                .horizontalScroll(horizontalScroll)
                        )
                    }
                }
            }

            // all matches list alert dialog
            AlertUiDialog(
                state = appState.regexpMatchesLisDialogState,
                onDismissRequestFunction = { appState.updateRegexpMatchesListDialogState(false) }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    SimpleUiDialogTitle(
                        titleTextContent = "All matches list",
                        titleIconPainter = painterResource(R.drawable.outline_check_circle_24),
                        onDismissRequestFunction = { appState.updateRegexpMatchesListDialogState(false) }
                    )

                    Box(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .height(200.dp)
                            .padding(10.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(
                                items = appState.allMatchesByRegexpList,
                                key = { index, match -> match.id }
                            ) { index, match ->
                                RegexUiMatch(
                                    index = index + 1,
                                    matchValue = match.match,
                                    start = match.start,
                                    end = match.end
                                )

                                if (index < appState.allMatchesByRegexpList.lastIndex) HorizontalDivider()
                            }
                        }
                    }
                }
            }

            // Save regexp pattern alert dialog
            AlertUiDialog(
                state = appState.saveRegexpPatternDialogState,
                onDismissRequestFunction = {
                    appState.updateSaveRegexpName("")
                    appState.updateSaveRegexpPatternDialogState(false)
                }
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SimpleUiDialogTitle(
                        titleIconPainter = painterResource(R.drawable.baseline_save_24),
                        titleTextContent = "Save your regexp pattern",
                        onDismissRequestFunction = {
                            appState.updateSaveRegexpName("")
                            appState.updateSaveRegexpPatternDialogState(false)
                        }
                    )

                    OutlinedTextField(
                        value = appState.saveRegexpName,
                        onValueChange = { newValue -> appState.updateSaveRegexpName(newValue) },
                        placeholder = {
                            Text(
                                text = "enter your regexp pattern name",
                                modifier = Modifier.basicMarquee(Int.MAX_VALUE)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary
                        )
                    )

                    Row {
                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                if (appState.saveRegexpName.isNotEmpty()) {
                                    savedRegexpPatternsState.addRegexp(
                                        RegexpPatternEntity(
                                            name = appState.saveRegexpName,
                                            regexpString = appState.regexInputFieldState,
                                            isGlobalSearchState = isGlobalSearchState,
                                            flags = appState.getSelectedFlags(),
                                        )
                                    )
                                    appState.apply {
                                        updateSaveRegexpName("")
                                        updateSaveRegexpPatternDialogState(false)
                                    }
                                } else toaster.showToast("⚠️Name is empty!")
                            },
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) { Text(text = "save") }
                    }
                }
            }

            // Regex flags selection dialog
            AlertUiDialog(
                state = appState.regexFlagsView,
                onDismissRequestFunction = { appState.updateRegexFlagsView(false) }
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SimpleUiDialogTitle(
                        titleIconPainter = painterResource(R.drawable.baseline_settings_24),
                        titleTextContent = "Flags and other",
                        onDismissRequestFunction = { appState.updateRegexFlagsView(false) }
                    )

                    Box(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .height(200.dp)
                    ) {
                        // regexp flags list view
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                        ) {
                            items(
                                items = regexFlagsList,
                                key = { flag -> flag.id }
                            ) { flag ->
                                Row {
                                    Text(
                                        text = flag.name,
                                        modifier = Modifier
                                            .weight(1f)
                                            .align(Alignment.CenterVertically)
                                    )
                                    Checkbox(
                                        checked = flag.isSelected,
                                        onCheckedChange = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic
                                            appState.setSelectedRegexFlagState(
                                                when (flag.isSelected) {
                                                    true -> false
                                                    false -> true
                                                },
                                                flag.name
                                            )
                                        },
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider()

                    Row {
                        Text(
                            text = "🌐 Global search",
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                        Checkbox(
                            checked = isGlobalSearchState,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic
                                appState.updateIsRegexGlobalSearch(
                                    when (isGlobalSearchState) {
                                        true -> false
                                        false -> true
                                    }
                                )
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val dimThemeColor = currentThemeColor.getAdaptedCurrentThemeColor(true, alphaFactor = 0.5f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    appState.regexExceptionMessage?.let { // if regex exception message is not null, show error view button
                        Box(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color.Red,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .height(58.dp)
                                .padding(10.dp)
                                .clickable(onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic
                                    appState.updateRegexExceptionViewState(true)
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_error_24),
                                contentDescription = null,
                                tint = Color.Red,
                            )
                        }
                    }

                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = appState.regexInputFieldState,
                        onValueChange = { newValue -> appState.updateRegexInputFieldState(newValue) },
                        leadingIcon = {
                            IconButton(onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic
                                appState.updateRegexFlagsView(true)
                            }) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ) { Text(text = appState.regexFlagsEnabledCount.toString()) }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_settings_24),
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        trailingIcon = {
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                // save regexp pattern icon button
                                // checking if there is a regular expression for saving and there is no exception.
                                AnimatedVisibility(
                                    visible = (appState.regexInputFieldState.isNotEmpty() && appState.regexExceptionMessage == null)
                                ) {
                                    IconButton(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic
                                            appState.updateSaveRegexpPatternDialogState(true)
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_save_24),
                                            contentDescription = null
                                        )
                                    }
                                }

                                IconButton(onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic

                                    if (appState.regexInputFieldState.isNotEmpty()) {
                                        appState.updateRegexInputFieldState("")
                                        toaster.showToast("regexp cleared!") // toast message
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_clear_24),
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        placeholder = {
                            Text(
                                text = "enter your regexp pattern...",
                                color = dimThemeColor,
                                modifier = Modifier.basicMarquee(Int.MAX_VALUE)
                            )
                        },
                        singleLine = true,
                        visualTransformation = RegexFieldVisualTransformation(
                            appState.regexExceptionMessage,
                            isLiteralFlagEnabledState
                        ), // visual transformation
                        colors = OutlinedTextFieldDefaults.colors(
                            // if regex exception message not null -> set red colors
                            focusedBorderColor = appState.regexExceptionMessage?.let {
                                Color.Red
                            } ?: MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = appState.regexExceptionMessage?.let {
                                MaterialTheme.colorScheme.error
                            } ?: MaterialTheme.colorScheme.onSecondary
                        )
                    )

                    // regexp matches view
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .align(Alignment.CenterVertically)
                            .height(58.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) // haptic
                                    if (appState.allMatchesByRegexpList.isNotEmpty()) appState.updateRegexpMatchesListDialogState(true)
                                    else toaster.showToast("no matches!")
                                }
                            )
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.outline_check_circle_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )

                            Text(
                                text = appState.matchesCount.toString(), // all matches count now
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = appState.isTestTextFieldFocusedState) {
                    // text data and actions view
                    Box(
                        modifier = Modifier
                            .padding(
                                top = 2.dp,
                                bottom = 2.dp,
                                start = 10.dp,
                                end = 10.dp
                            )
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .padding(5.dp)
                            .fillMaxWidth()
                    ) {
                        // test text field tool buttons row panel
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            // clear all text from text field button
                            Button( // clear content in test text field button
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic

                                    if (appState.textInputFieldState.isNotEmpty()) {
                                        appState.updateTextInputFieldState("") // clear all text from text field state
                                        toaster.showToast("test text cleared!")
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(5.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_clear_24),
                                    contentDescription = null
                                )
                            }

                            // paste text from clipboard button
                            Button( // paste text in test text field button
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic
                                    val clipData = clipBoardManager.getClipboardText() // get data from clipboard

                                    if (clipData.isNotEmpty()) {
                                        appState.updateTextInputFieldState(clipData) // set data to text field state
                                        toaster.showToast("text pasted!")
                                    } else toaster.showToast("nothing to paste!")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(5.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_content_paste_24),
                                    contentDescription = null
                                )
                            }

                            // colorpicker view button
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic
                                    appState.updateColorPickerState(true)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(5.dp),
                            ) {
                                Box(
                                    modifier = Modifier.background(Color.fromColorLong(currentSelectionColor)), // background - current matches count
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_draw_24),
                                        contentDescription = null
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) //haptic

                                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                        type = FILES_TYPE
                                    }
                                    pickTextFileLauncher.launch(intent) // launching open text file intent
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(5.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_file_open_24),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .onFocusChanged { appState.updateIsTestTextFieldFocusedState(it.isFocused) }, // updating isFocused state
                    value = appState.textInputFieldState,
                    onValueChange = { newValue ->
                        if (newValue.length < TEXT_LIMIT) {
                            appState.updateTextInputFieldState(newValue)
                        } else {
                            appState.updateTextInputFieldState(newValue.substring(0, TEXT_LIMIT))
                            toaster.showToast("⚠️text too large!") // text is large, max 2500
                        }
                    },
                    placeholder = {
                        Text(
                            text = "enter your test text...",
                            color = dimThemeColor,
                            modifier = Modifier.basicMarquee(Int.MAX_VALUE)
                        )
                    },
                    visualTransformation = TextFieldVisualTransformation(
                        appState.currentRegexAsString,
                        Color.fromColorLong(currentSelectionColor),
                        regexFlagsList.filter { flag -> flag.isSelected },
                        isGlobalSearchState,
                        appState
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary
                    )
                )
            }

            // Regex cheat sheet
            BottomUiSheet(
                state = appState.bottomCheatSheetState,
                skipPartiallyExpanded = true,
                onDismissRequestFunction = { appState.updateBottomCheatSheetState(false) },
                gesturesEnabled = false,
                titleContent = {
                    RegexerUiDialogTitle(
                        titleText = "Regexp mini-cheat sheet",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        spawnDismissDialogButton = true,
                        dismissDialogButtonFunction = { appState.updateBottomCheatSheetState(false) }
                    )
                }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    val verticalScroll = rememberScrollState()

                    Text(
                        text = regexpSyntaxAnnotatedStringBuilder.setRegexpSyntaxStyleOnRegexStringPattern(
                            buildAnnotatedString {
                                append(
                                    "● Character classes:\n" +
                                            ".\tany character except newline\n" +
                                            "\\w \\d \\s\tword, digit, whitespace\n" +
                                            "\\W \\D \\S\tnot word, digit, whitespace\n" +
                                            "[abc]\tany of a, b, or c\n" +
                                            "[^abc]\tnot a, b, or c\n" +
                                            "[a-g]\tcharacter between a & g\n" +
                                            "\n" +
                                            "● Anchors:\n" +
                                            "^abc$\tstart / end of the string\n" +
                                            "\\b\tword boundary\n" +
                                            "\n" +
                                            "● Escaped characters:\n" +
                                            "\\. \\* \\\\\tescaped special characters\n" +
                                            "\\t \\n \\r\t tab, linefeed, carriage return\n" +
                                            "\\u00A9\tunicode escaped ©\n" +
                                            "\n" +
                                            "● Groups & Lookaround:\n" +
                                            "(abc)\tcapture group\n" +
                                            "\\1\tbackreference to group #1\n" +
                                            "(?:abc)\tnon-capturing group\n" +
                                            "(?=abc)\tpositive lookahead\n" +
                                            "(?!abc)\tnegative lookahead\n" +
                                            "\n" +
                                            "● Quantifiers & Alternation:\n" +
                                            "a* a+ a?\t0 or more, 1 or more, 0 or 1\n" +
                                            "a{5} a{2,}\texactly five, two or more\n" +
                                            "a{1,3}\tbetween one & three\n" +
                                            "a+? a{2,}?\tmatch as few as possible\n" +
                                            "ab|cd\tmatch ab or cd"
                                )
                            },
                        ),
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .verticalScroll(verticalScroll)
                    )
                }
            }
        }

        // About Regexer APP sheet
        BottomUiSheet(
            state = appState.aboutAppInfoSheetState,
            skipPartiallyExpanded = true,
            onDismissRequestFunction = { appState.updateAboutAppInfoSheetState(false) },
            gesturesEnabled = false,
            titleContent = {
                RegexerUiDialogTitle( // title component
                    titleText = "About ${stringResource(R.string.app_name)} App",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    spawnDismissDialogButton = true,
                    dismissDialogButtonFunction = { appState.updateAboutAppInfoSheetState(false) }
                )
            }
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // project info
                Text(
                    text = REGEXER_APP_INFO,
                    modifier = Modifier.fillMaxWidth()
                )

                // open source project description
                Text(
                    text = OPEN_SOURCE_PROJECT_DESCRIPTION,
                    modifier = Modifier.fillMaxWidth()
                )

                // project repo clickable text link
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color.Cyan,
                                textDecoration = TextDecoration.Underline
                            )
                        ) { append(REGEXER_APP_GITHUB_REPO_LINK) }
                    },
                    modifier = Modifier.clickable(
                        onClick = {
                            urlOpener.openUrl(REGEXER_APP_GITHUB_REPO_LINK)
                        }
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // app version name
                Text(
                    text = appManager.getAppVersionName()?.let { "version: $it" } ?: "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Color picker sheet
        ColorPickerSheet(
            state = appState.colorPickerState,
            onDismissRequestFunction = { appState.updateColorPickerState(false) },
            onColorChangedFunction = appState::updateRegexSelectionMatchesColorState,
            initialColor = Color.fromColorLong(currentSelectionColor),
            configuration = configuration,
            title = "Select regexp match color"
        )
    }
}