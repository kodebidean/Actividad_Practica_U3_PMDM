package com.kodeleku.project_gestor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kodeleku.project_gestor.adapters.ProjectAdapter
import com.kodeleku.project_gestor.databinding.ActivityProjectListBinding
import com.kodeleku.project_gestor.models.Project
import kotlinx.coroutines.launch
import androidx.activity.result.contract.ActivityResultContracts

class ProjectListActivity : BaseActivity() {

    private lateinit var binding: ActivityProjectListBinding
    private lateinit var adapter: ProjectAdapter

    // Registrar el callback para manejar los resultados de ProjectDetailActivity
    private val projectDetailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Recargar proyectos después de actualizar un proyecto
            loadProjects()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar ViewBinding
        binding = ActivityProjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView
        binding.projectRecyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Configurar botones
        binding.btnAddProject.setOnClickListener { openCreateProjectScreen() }
        binding.btnAddLanguage.setOnClickListener { openCreateLanguageScreen() }

        // Cargar proyectos al iniciar la actividad
        loadProjects()
    }

    override fun onResume() {
        super.onResume()
        // Recargar proyectos para reflejar actualizaciones
        loadProjects()
    }

    private fun loadProjects() {
        lifecycleScope.launch {
            val projects = MyApplication.database.projectDao().getAllProjects()
            val languages = MyApplication.database.languageDao().getAllLanguages()

            if (projects.isNotEmpty()) {
                val languageMap = languages.associateBy({ it.id }, { it.name })
                adapter = ProjectAdapter(projects.toMutableList(), languageMap) { project ->
                    openProjectDetailScreen(project)
                }
                binding.projectRecyclerView.adapter = adapter
            } else {
                Toast.makeText(this@ProjectListActivity, "No hay proyectos disponibles", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun openProjectDetailScreen(project: Project) {
        val intent = Intent(this, ProjectDetailActivity::class.java)
        intent.putExtra("PROJECT_ID", project.id)
        projectDetailLauncher.launch(intent) // Usar ActivityResultContracts para obtener resultados
    }

    private fun openCreateProjectScreen() {
        val intent = Intent(this, CreateProjectActivity::class.java)
        startActivity(intent) // No necesita callback
    }

    private fun openCreateLanguageScreen() {
        val intent = Intent(this, CreateLanguageActivity::class.java)
        startActivity(intent) // Tampoco necesita callback
    }
}
