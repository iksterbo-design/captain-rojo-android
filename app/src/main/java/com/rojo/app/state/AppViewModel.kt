package com.rojo.app.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojo.app.models.AdScene
import com.rojo.app.models.Project
import com.rojo.app.repository.ProjectRepository
import com.rojo.app.services.GenerationResult
import com.rojo.app.services.GenerationService
import com.rojo.app.services.HealthResult
import com.rojo.app.services.HealthService
import com.rojo.app.services.LinkResult
import com.rojo.app.services.LinkService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Single source of truth for the app's UI state, owned by the ViewModel.
 *
 * Holds the current navigation target, the selected project and the loading
 * flags; exposes outcome flows the screens subscribe to, and the entry
 * functions that start each runbook.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val generationService: GenerationService,
    private val linkService: LinkService,
    private val healthService: HealthService
) : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    /** Outcome flows the screens subscribe to (one per runbook). */
    private val _generateOutcome = MutableStateFlow<GenerationResult>(GenerationResult.Loading)
    val generateOutcome: StateFlow<GenerationResult> = _generateOutcome

    private val _renderOutcome = MutableStateFlow<GenerationResult>(GenerationResult.Loading)
    val renderOutcome: StateFlow<GenerationResult> = _renderOutcome

    private val _linkOutcome = MutableStateFlow<LinkResult>(LinkResult.Error("Not started"))
    val linkOutcome: StateFlow<LinkResult> = _linkOutcome

    private val _healthOutcome = MutableStateFlow<HealthResult>(HealthResult.Loading)
    val healthOutcome: StateFlow<HealthResult> = _healthOutcome

    /** List of persisted projects (repository is the storage owner). */
    val projectsState: StateFlow<List<Project>> = _state
        .map { it.projects }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectNav(nav: NavItem) {
        _state.value = _state.value.copy(selectedNav = nav)
    }

    fun createProject(name: String) {
        val project = repository.create(name)
        _state.value = _state.value.copy(selectedProject = project, projects = repository.loadAll())
    }

    fun selectProject(project: Project?) {
        _state.value = _state.value.copy(selectedProject = project)
    }

    fun startGenerate(prompt: String, style: String) {
        val project = _state.value.selectedProject ?: run {
            _state.value = _state.value.copy(lastError = "No project selected — create one first.")
            return
        }
        _state.value = _state.value.copy(generateLoading = true, lastError = null)
        viewModelScope.launch {
            generationService.startGenerate(prompt, style).collect { result ->
                _state.value = _state.value.copy(generateLoading = false)
                when (result) {
                    is GenerationResult.Loading -> {}
                    is GenerationResult.Success -> _generateOutcome.value = result
                    is GenerationResult.Error -> {
                        _state.value = _state.value.copy(lastError = result.message)
                        _generateOutcome.value = result
                    }
                }
            }
        }
    }

    fun startRender(scenes: List<AdScene>, length: String) {
        val project = _state.value.selectedProject ?: run {
            _state.value = _state.value.copy(lastError = "No project selected — create one first.")
            return
        }
        _state.value = _state.value.copy(generateLoading = true, lastError = null)
        viewModelScope.launch {
            generationService.startRender(scenes, length).collect { result ->
                _state.value = _state.value.copy(generateLoading = false)
                when (result) {
                    is GenerationResult.Loading -> {}
                    is GenerationResult.Success -> _renderOutcome.value = result
                    is GenerationResult.Error -> {
                        _state.value = _state.value.copy(lastError = result.message)
                        _renderOutcome.value = result
                    }
                }
            }
        }
    }

    fun startLink(email: String, device: String, errorText: String) {
        _state.value = _state.value.copy(linkLoading = true, lastError = null)
        viewModelScope.launch {
            linkService.startLink(email, device, errorText).collect { result ->
                _state.value = _state.value.copy(linkLoading = false)
                when (result) {
                    is LinkResult.Error -> {
                        _state.value = _state.value.copy(lastError = result.message)
                        _linkOutcome.value = result
                    }
                    else -> _linkOutcome.value = result
                }
            }
        }
    }

    fun startReRunCheck() {
        _state.value = _state.value.copy(healthLoading = true, lastError = null)
        viewModelScope.launch {
            healthService.runCheck().collect { result ->
                _state.value = _state.value.copy(healthLoading = false)
                when (result) {
                    is HealthResult.Loading -> {}
                    is HealthResult.Success -> _healthOutcome.value = result
                    is HealthResult.Error -> {
                        _state.value = _state.value.copy(lastError = result.message)
                        _healthOutcome.value = result
                    }
                }
            }
        }
    }
}
