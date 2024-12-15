package com.kodeleku.project_gestor

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kodeleku.project_gestor.databinding.ActivityProjectDetailBinding
import com.kodeleku.project_gestor.models.Project
import kotlinx.coroutines.launch

class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectDetailBinding
    private var projectId: Int = 0
    private var currentProject: Project? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar ViewBinding
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el ID del proyecto de los extras
        projectId = intent.getIntExtra("PROJECT_ID", 0)

        if (projectId != 0) {
            loadProjectDetails()
        } else {
            Toast.makeText(this, "Error al cargar el proyecto", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Configurar el botón de guardar
        binding.btnUpdateProject.setOnClickListener {
            updateProjectDetails()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadProjectDetails() {
        lifecycleScope.launch {
            val dao = MyApplication.database.projectDao()
            currentProject = dao.getProjectById(projectId)

            if (currentProject != null) {
                // Mostrar los detalles en los campos correspondientes
                binding.tvProjectName.text = currentProject!!.name
                binding.tvProjectLanguage.text = "Lenguaje ID: ${currentProject!!.languageId}" // Puedes usar una query para obtener el nombre del lenguaje si es necesario
                binding.etProjectDescription.setText(currentProject!!.description)
                binding.etProjectHours.setText(currentProject!!.hours.toString())
                binding.etProjectPriority.setText(currentProject!!.priority.toString())
            } else {
                Toast.makeText(this@ProjectDetailActivity, "Proyecto no encontrado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateProjectDetails() {
        // Validar los inputs
        val newDescription = binding.etProjectDescription.text.toString()
        val newHours = binding.etProjectHours.text.toString().toIntOrNull()
        val newPriority = binding.etProjectPriority.text.toString().toIntOrNull()

        if (newDescription.isNotBlank() && newHours != null && newPriority != null) {
            lifecycleScope.launch {
                currentProject?.let { project ->
                    val updatedProject = project.copy(
                        description = newDescription,
                        hours = newHours,
                        priority = newPriority
                    )

                    // Actualizar en la base de datos
                    MyApplication.database.projectDao().updateProject(updatedProject)

                    Toast.makeText(this@ProjectDetailActivity, "Proyecto actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
        }
    }
}