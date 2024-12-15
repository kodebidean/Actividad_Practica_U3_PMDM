package com.kodeleku.project_gestor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kodeleku.project_gestor.adapters.ProjectAdapter
import com.kodeleku.project_gestor.databinding.ActivityProjectListBinding
import com.kodeleku.project_gestor.models.Project
import kotlinx.coroutines.launch

class ProjectListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectListBinding
    private lateinit var adapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar ViewBinding
        binding = ActivityProjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView
        binding.projectRecyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar botones
        binding.btnAddProject.setOnClickListener {
            openCreateProjectScreen()
        }

        binding.btnAddLanguage.setOnClickListener {
            openCreateLanguageScreen()
        }

        // Cargar proyectos
        loadProjects()
    }

    private fun loadProjects() {
        lifecycleScope.launch {
            val projects = MyApplication.database.projectDao().getAllProjects()
            val languages = MyApplication.database.languageDao().getAllLanguages()

            if (projects.isNotEmpty()) {
                val languageMap = languages.associateBy({ it.id }, { it.name }) // Crear el mapa de lenguajes
                adapter = ProjectAdapter(projects, languageMap) { project ->
                    navigateToProjectDetail(project)
                }
                binding.projectRecyclerView.adapter = adapter
            } else {
                Toast.makeText(this@ProjectListActivity, "No hay proyectos disponibles", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToProjectDetail(project: Project) {
        val intent = Intent(this, ProjectDetailActivity::class.java)
        intent.putExtra("PROJECT_ID", project.id)
        startActivity(intent)
    }

    // Método para abrir la pantalla de crear proyecto
    private fun openCreateProjectScreen() {
        val intent = Intent(this, CreateProjectActivity::class.java)
        startActivity(intent)
    }

    // Método para abrir la pantalla de crear lenguaje
    private fun openCreateLanguageScreen() {
        val intent = Intent(this, CreateLanguageActivity::class.java)
        startActivity(intent)
    }
}
